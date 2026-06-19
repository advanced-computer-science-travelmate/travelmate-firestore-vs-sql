import { useState, useEffect } from "react";
import axios from "axios";
import { searchPlacesByDestination } from "../Services/itineraryService";

function ItineraryBuilder({ trip }) {
  const [searchTextByDay, setSearchTextByDay] = useState({});
  const [suggestionsByDay, setSuggestionsByDay] = useState({});
  const [selectedPlacesByDay, setSelectedPlacesByDay] = useState({});
  const [loadingDay, setLoadingDay] = useState(null);
  const [isSaving, setIsSaving] = useState(false);

  const destination = trip.destination || trip.destinationName;
  const tripId = trip.id;

  // 🚀 FETCH EXISTING ITINERARY FROM FIRESTORE ON LOAD
  useEffect(() => {
    if (tripId) {
      axios.get(`http://localhost:8080/api/travel/destinations/firestore`)
        .then((res) => {
          // Find the dynamic Firestore tracking object mapped to this trip
          const matchedData = res.data.find(
            d => d.tripId === tripId || d.name?.toLowerCase() === destination?.toLowerCase()
          );
          if (matchedData && matchedData.selectedPlacesByDay) {
            setSelectedPlacesByDay(matchedData.selectedPlacesByDay);
          }
        })
        .catch(err => console.error("Failed to load itinerary from Firestore:", err));
    }
  }, [tripId, destination]);

  function generateTripDays(startDate, endDate) {
    if (!startDate || !endDate) return [];
    const days = [];
    const currentDate = new Date(startDate);
    const finalDate = new Date(endDate);
    let dayNumber = 1;

    while (currentDate <= finalDate) {
      days.push({
        dayNumber,
        date: currentDate.toISOString().split("T")[0],
      });
      currentDate.setDate(currentDate.getDate() + 1);
      dayNumber++;
    }
    return days;
  }

  async function handleSearch(dayDate, value) {
    setSearchTextByDay((prev) => ({ ...prev, [dayDate]: value }));

    if (value.trim().length < 2) {
      setSuggestionsByDay((prev) => ({ ...prev, [dayDate]: [] }));
      return;
    }

    try {
      setLoadingDay(dayDate);
      const places = await searchPlacesByDestination(destination, value);
      setSuggestionsByDay((prev) => ({ ...prev, [dayDate]: places }));
    } catch (error) {
      console.error("Failed to search itinerary places:", error);
    } finally {
      setLoadingDay(null);
    }
  }

  // 🚀 SAVE ADDED PLACES STRAIGHT INTO FIRESTORE DOCUMENT TREES
  async function handleAddPlace(dayDate, place) {
    // 1. Optimistically update local frontend state layout
    setSelectedPlacesByDay((prev) => {
      const currentPlaces = prev[dayDate] || [];
      const alreadyAdded = currentPlaces.some((item) => item.id === place.id);
      if (alreadyAdded) return prev;
      
      return {
        ...prev,
        [dayDate]: [...currentPlaces, place],
      };
    });

    // Clear search states immediately
    setSearchTextByDay((prev) => ({ ...prev, [dayDate]: "" }));
    setSuggestionsByDay((prev) => ({ ...prev, [dayDate]: [] }));

    // 2. Dispatch payload to your Spring Boot Dual-Persistence controller endpoint
    try {
      setIsSaving(true);
      
      // This single API call sends the payload to the backend orchestration layer
      await axios.post(`http://localhost:8080/api/travel/trips/${tripId}/itinerary/save`, {
        tripId: tripId,
        destinationName: destination,
        activityName: place.name,
        activityAddress: place.address || "",
        visitDate: dayDate, // maps to row entries in SQL and arrays in Firestore
        fullSelectedMap: {
          ...selectedPlacesByDay,
          [dayDate]: [...(selectedPlacesByDay[dayDate] || []), place]
        }
      });
      
    } catch (err) {
      console.error("Multi-cloud transaction synchronization aborted:", err);
      alert("Dual-Persistence synchronization failed. Data desynchronization risk detected.");
    } finally {
      setIsSaving(false);
    }
  }

  const tripDays = generateTripDays(trip.startDate, trip.endDate);

  return (
    <div className="mt-6 border-t border-slate-200 pt-5">
      <div className="flex justify-between items-center">
        <div>
          <h4 className="text-lg font-bold text-slate-900">Need an itinerary?</h4>
          <p className="text-sm text-slate-600 mt-1">Build a day-by-day plan for {destination}.</p>
        </div>
        {isSaving && (
          <span className="text-xs font-semibold bg-blue-50 text-blue-600 px-3 py-1.5 rounded-lg animate-pulse">
            Syncing Firestore...
          </span>
        )}
      </div>

      <div className="mt-5 space-y-5">
        {tripDays.map((day) => {
          const searchText = searchTextByDay[day.date] || "";
          const suggestions = suggestionsByDay[day.date] || [];
          const selectedPlaces = selectedPlacesByDay[day.date] || [];

          return (
            <div key={day.date} className="bg-slate-50 border border-slate-200 rounded-2xl p-4">
              <h5 className="font-bold text-slate-900">
                Day {day.dayNumber} - {day.date}
              </h5>

              {/* Added Places Display */}
              {selectedPlaces.length > 0 && (
                <div className="mt-4 space-y-2">
                  {selectedPlaces.map((place) => (
                    <div key={place.id} className="bg-white border border-green-200 rounded-xl px-4 py-3 shadow-xs">
                      <p className="font-semibold text-slate-900">✓ {place.name}</p>
                      {place.address && <p className="text-sm text-slate-600 mt-1">{place.address}</p>}
                    </div>
                  ))}
                </div>
              )}

              {/* Dynamic Input Search Field */}
              <input
                type="text"
                value={searchText}
                onChange={(e) => handleSearch(day.date, e.target.value)}
                placeholder={`Search places in {destination}...`}
                className="mt-4 w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              />

              {loadingDay === day.date && (
                <p className="text-sm text-slate-500 mt-3 animate-pulse">Searching live locations maps...</p>
              )}

              {/* Suggestions List Dropdown */}
              {suggestions.length > 0 && (
                <div className="mt-4 space-y-2 max-h-60 overflow-y-auto pr-1">
                  {suggestions.map((place) => (
                    <button
                      key={place.id}
                      type="button"
                      onClick={() => handleAddPlace(day.date, place)}
                      className="w-full text-left bg-white border border-slate-200 rounded-xl px-4 py-3 hover:bg-blue-50 transition shadow-2xs block"
                    >
                      <p className="font-semibold text-slate-900">+ {place.name}</p>
                      {place.address && <p className="text-sm text-slate-600 mt-1">{place.address}</p>}
                      {place.rating && <p className="text-xs text-amber-600 font-medium mt-1">⭐ {place.rating}</p>}
                    </button>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default ItineraryBuilder;