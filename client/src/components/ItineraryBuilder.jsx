import { useState } from "react";
import { searchPlacesByDestination } from "../Services/itineraryService";

function ItineraryBuilder({ trip }) {
  const [searchTextByDay, setSearchTextByDay] = useState({});
  const [suggestionsByDay, setSuggestionsByDay] = useState({});
  const [selectedPlacesByDay, setSelectedPlacesByDay] = useState({});
  const [loadingDay, setLoadingDay] = useState(null);

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
    setSearchTextByDay((prev) => ({
      ...prev,
      [dayDate]: value,
    }));

    if (value.trim().length < 2) {
      setSuggestionsByDay((prev) => ({
        ...prev,
        [dayDate]: [],
      }));
      return;
    }

    try {
      setLoadingDay(dayDate);

      const places = await searchPlacesByDestination(destination, value);

      setSuggestionsByDay((prev) => ({
        ...prev,
        [dayDate]: places,
      }));
    } catch (error) {
      console.error("Failed to search itinerary places:", error);
    } finally {
      setLoadingDay(null);
    }
  }

  function handleAddPlace(dayDate, place) {
    setSelectedPlacesByDay((prev) => {
      const currentPlaces = prev[dayDate] || [];

      const alreadyAdded = currentPlaces.some(
        (item) => item.id === place.id
      );

      if (alreadyAdded) return prev;

      return {
        ...prev,
        [dayDate]: [...currentPlaces, place],
      };
    });

    setSearchTextByDay((prev) => ({
      ...prev,
      [dayDate]: "",
    }));

    setSuggestionsByDay((prev) => ({
      ...prev,
      [dayDate]: [],
    }));
  }

  const tripDays = generateTripDays(trip.startDate, trip.endDate);
  const destination = trip.destination || trip.destinationName;

  return (
    <div className="mt-6 border-t border-slate-200 pt-5">
      <h4 className="text-lg font-bold text-slate-900">
        Need an itinerary?
      </h4>

      <p className="text-sm text-slate-600 mt-1">
        Build a day-by-day plan for {destination}.
      </p>

      <div className="mt-5 space-y-5">
        {tripDays.map((day) => {
          const searchText = searchTextByDay[day.date] || "";
          const suggestions = suggestionsByDay[day.date] || [];
          const selectedPlaces = selectedPlacesByDay[day.date] || [];

          return (
            <div
              key={day.date}
              className="bg-slate-50 border border-slate-200 rounded-2xl p-4"
            >
              <h5 className="font-bold text-slate-900">
                Day {day.dayNumber} - {day.date}
              </h5>

              {selectedPlaces.length > 0 && (
                <div className="mt-4 space-y-2">
                  {selectedPlaces.map((place) => (
                    <div
                      key={place.id}
                      className="bg-white border border-green-200 rounded-xl px-4 py-3"
                    >
                      <p className="font-semibold text-slate-900">
                        ✓ {place.name}
                      </p>

                      {place.address && (
                        <p className="text-sm text-slate-600 mt-1">
                          {place.address}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              )}

              <input
                type="text"
                value={searchText}
                onChange={(e) => handleSearch(day.date, e.target.value)}
                placeholder={`Search places in ${destination}`}
                className="mt-4 w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

              {loadingDay === day.date && (
                <p className="text-sm text-slate-500 mt-3">
                  Searching places...
                </p>
              )}

              {suggestions.length > 0 && (
                <div className="mt-4 space-y-2">
                  {suggestions.map((place) => (
                    <button
                      key={place.id}
                      type="button"
                      onClick={() => handleAddPlace(day.date, place)}
                      className="w-full text-left bg-white border border-slate-200 rounded-xl px-4 py-3 hover:bg-blue-50 transition"
                    >
                      <p className="font-semibold text-slate-900">
                        + {place.name}
                      </p>

                      {place.address && (
                        <p className="text-sm text-slate-600 mt-1">
                          {place.address}
                        </p>
                      )}

                      {place.rating && (
                        <p className="text-sm text-slate-500 mt-1">
                          ⭐ {place.rating}
                        </p>
                      )}
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