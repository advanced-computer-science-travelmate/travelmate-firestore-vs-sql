import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Navbar from "../components/Navbar";
import { destinationService } from "../services/destinationService";
import VotingPoll from "../components/VotingPoll";
import HotelCards from "../components/HotelCards";

function Trips({ isLoggedIn, onLogin, onLogout }) {
  const navigate = useNavigate();
  const [destinations, setDestinations] = useState([]);
  const [isLoadingDestinations, setIsLoadingDestinations] = useState(true);
  const [trips, setTrips] = useState([]);
  const [isLoadingTrips, setIsLoadingTrips] = useState(true); // Set to true initially to prevent layout flash
  const [showForm, setShowForm] = useState(false);
  const [destination, setDestination] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [showVoting, setShowVoting] = useState(false);
  const [adults, setAdults] = useState(1);
  const [children, setChildren] = useState(0);
  const [rooms, setRooms] = useState(1);
  const [selectedHotelTripId, setSelectedHotelTripId] = useState(null);

  const savedSession = localStorage.getItem("userSession");
  const userSession = savedSession ? JSON.parse(savedSession) : null;
  const activeSessionExists = !!userSession;
  
  const currentUserId = userSession ? userSession.noSqlId : "USER-ACTIVE-101";

  // Load destinations once on mount
  useEffect(() => {
    async function loadDestinations() {
      try {
        const data = await destinationService.getEuropeanDestinations();
        setDestinations(data);
      } catch (error) {
        console.error("Failed to load destinations:", error);
      } finally {
        setIsLoadingDestinations(false);
      }
    }
    loadDestinations();
  }, []);

  // Fetch trips from database if user session exists
  useEffect(() => {
    if (activeSessionExists) {
      setIsLoadingTrips(true);
      axios
        .get("http://localhost:8080/api/travel/trips/sql")
        .then((response) => {
          setTrips(response.data);
        })
        .catch((error) => {
          System.err.println("Failed to load backend trips, using fallback storage:", error);
          const localTrips = localStorage.getItem("trips");
          if (localTrips) setTrips(JSON.parse(localTrips));
        })
        .finally(() => {
          setIsLoadingTrips(false);
        });
    } else {
      setIsLoadingTrips(false);
    }
  }, [activeSessionExists]);

  function handleCreateTrip(e) {
    e.preventDefault();

    const selectedDestination = destinations.find(
      (item) => item.name === destination,
    );

    const tripPayload = {
      destinationName: destination,
      startDate: startDate,
      endDate: endDate,
      maxTravelers: 2, 
      userId: currentUserId,
      sqlUserId: userSession ? userSession.sqlId : "1",
      adults,
      children,
      rooms,
    };

    axios
      .post("http://localhost:8080/api/travel/trips/create", tripPayload)
      .then((response) => {
        // 🚀 FIXED: Read the real database structure returned from the Spring Boot backend!
        const savedDatabaseTrip = response.data;

        const realNewCard = {
          id: savedDatabaseTrip.id || savedDatabaseTrip.tripId, // Use the real DB primary key
          destinationName: savedDatabaseTrip.destinationName || destination,
          destination: savedDatabaseTrip.destination || destination, 
          startDate: savedDatabaseTrip.startDate || startDate,
          endDate: savedDatabaseTrip.endDate || endDate,
          image: selectedDestination?.image || "",
          bookings: savedDatabaseTrip.bookings || [], 
          adults: savedDatabaseTrip.adults || adults,
          children: savedDatabaseTrip.children || children,
          rooms: savedDatabaseTrip.rooms || rooms,
        };

        // Maintain array structural consistency safely
        const updatedTrips = Array.isArray(trips) ? [...trips, realNewCard] : [realNewCard];
        setTrips(updatedTrips);
        localStorage.setItem("trips", JSON.stringify(updatedTrips));

        // Clear states
        setDestination("");
        setStartDate("");
        setEndDate("");
        setShowForm(false);
        setAdults(1);
        setChildren(0);
        setRooms(1);
      })
      .catch((error) => {
        console.error("Multi-cloud transaction aborted:", error);
        alert("Dual-Persistence write failure. Reverting layout state context rules.");
      });
  }

  function handleDeleteTrip(id) {
    if (Array.isArray(trips)) {
      const updatedTrips = trips.filter((trip) => trip.id !== id);
      setTrips(updatedTrips);
      localStorage.setItem("trips", JSON.stringify(updatedTrips));
    }
  }

  // 🛑 GUARDRAIL 1: Show a pulse indicator while retrieving the persistent storage record
  if (isLoadingTrips) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-slate-500 font-medium animate-pulse text-lg">
          Synchronizing travel profile session...
        </div>
      </div>
    );
  }

  // 🛑 GUARDRAIL 2: If validation has completed and user is missing, intercept with the Login window
  if (!activeSessionExists) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-16 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-sm p-10 text-center border border-slate-100">
          <h1 className="text-3xl font-bold text-slate-900">Login to plan a trip</h1>
          <p className="text-slate-600 mt-4">Please log in to your account to create and manage your cloud travel itineraries.</p>
          <button
            onClick={() => navigate("/")} // Assuming your route path to Login.jsx is "/"
            className="mt-8 w-full bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition shadow-sm"
          >
            Go to Login
          </button>
        </div>
      </main>
    );
  }

  // 🟢 RUNNING STATE: Mount user layout grid safely once authentication context is resolved
  return (
    <>
      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-10">
            <div>
              <h1 className="text-4xl font-bold text-slate-900">Your Trips</h1>
              <p className="text-slate-600 mt-2">View and manage your travel plans.</p>
            </div>

            <div className="flex gap-4">
              <button
                onClick={() => setShowForm(true)}
                className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
              >
                Plan a new trip
              </button>

              <button
                onClick={() => setShowVoting((prev) => !prev)}
                className="border border-blue-600 text-blue-600 px-6 py-3 rounded-xl font-semibold hover:bg-blue-50 transition"
              >
                Start Group Vote
              </button>
            </div>
          </div>

          {showForm && (
            <form onSubmit={handleCreateTrip} className="bg-white rounded-3xl shadow-sm p-8 mb-10">
              <h2 className="text-2xl font-bold text-slate-900 mb-6">Plan a new trip</h2>
              <div className="grid md:grid-cols-3 gap-6">
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Where to?</label>
                  <select
                    value={destination}
                    onChange={(e) => setDestination(e.target.value)}
                    required
                    disabled={isLoadingDestinations}
                    className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                  >
                    <option value="">{isLoadingDestinations ? "Loading destinations..." : "Select a destination"}</option>
                    {destinations.map((dest) => (
                      <option key={dest.id} value={dest.name}>{dest.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Start date</label>
                  <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} required className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">End date</label>
                  <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} required className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Adults</label>
                  <input type="number" min="1" value={adults} onChange={(e) => setAdults(Number(e.target.value))} className="w-full border border-slate-300 rounded-xl px-4 py-3" />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Children</label>
                  <input type="number" min="0" value={children} onChange={(e) => setChildren(Number(e.target.value))} className="w-full border border-slate-300 rounded-xl px-4 py-3" />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Rooms</label>
                  <input type="number" min="1" value={rooms} onChange={(e) => setRooms(Number(e.target.value))} className="w-full border border-slate-300 rounded-xl px-4 py-3" />
                </div>
              </div>
              <div className="flex gap-4 mt-8">
                <button type="submit" disabled={isLoadingDestinations} className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition disabled:bg-slate-400">Create Trip</button>
                <button type="button" onClick={() => setShowForm(false)} className="border border-slate-300 text-slate-700 px-6 py-3 rounded-xl font-semibold hover:bg-slate-100 transition">Cancel</button>
              </div>
            </form>
          )}

          {showVoting && Array.isArray(trips) && trips.length > 0 && (
            <VotingPoll tripId={trips[0].id} userId={currentUserId} />
          )}

          {!Array.isArray(trips) || trips.length === 0 ? (
            <div className="bg-white rounded-3xl shadow-sm p-12 text-center border border-slate-100">
              <h2 className="text-3xl font-bold text-slate-900">You haven’t created anything yet</h2>
              <p className="text-slate-600 mt-4">Start by planning your first trip.</p>
              <button onClick={() => setShowForm(true)} className="mt-8 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition">Plan a new trip</button>
            </div>
          ) : (
            <div className="grid md:grid-cols-3 gap-6">
              {trips.map((trip) => (
                <div key={trip.id} className="bg-white rounded-3xl shadow-sm overflow-hidden flex flex-col justify-between border border-slate-100 p-6">
                  <div>
                    {(trip.image || (trip.destination && trip.destination.image)) && (
                      <img 
                        src={trip.image || trip.destination.image} 
                        alt={trip.destinationName} 
                        className="h-40 w-full object-cover rounded-2xl mb-4" 
                      />
                    )}

                    <div>
                      <h3 className="text-2xl font-bold text-slate-900">
                        {typeof trip.destination === 'object' && trip.destination !== null 
                          ? trip.destination.name 
                          : (trip.destination || trip.destinationName)}
                      </h3>
                      <p className="text-slate-500 text-xs mt-1.5 font-medium">
                        📅 {trip.startDate} to {trip.endDate}
                      </p>

                      <div className="mt-5 border-t border-slate-100 pt-4">
                        <h4 className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-2.5">
                          Trip Reservations & Accommodations
                        </h4>
                        {trip.bookings && trip.bookings.length > 0 ? (
                          <div className="space-y-2 max-h-40 overflow-y-auto pr-1">
                            {trip.bookings.map((booking) => (
                              <div key={booking.id} className="flex justify-between items-center bg-slate-50/80 p-2.5 rounded-xl border border-slate-200/60">
                                <div>
                                  <span className="text-xs font-bold text-slate-700 block">
                                    {booking.bookingType === "HOTEL" ? "🏨 Hotel Stay" : "✈️ Flight Ticket"}
                                  </span>
                                  <span className="text-[10px] text-slate-400 font-mono tracking-tight block mt-0.5">
                                    Code: {booking.confirmationNumber}
                                  </span>
                                </div>
                                <span className="text-xs font-extrabold text-slate-900 bg-white border border-slate-200/80 px-2 py-1 rounded-md">
                                  ${booking.price}
                                </span>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <div className="bg-slate-50/50 rounded-xl p-3 text-center border border-dashed border-slate-200">
                            <p className="text-[11px] text-slate-400 font-normal italic">No bookings linked to this itinerary yet.</p>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  <div>
                    <div className="flex flex-wrap gap-2 mt-6">
                      <button
                        onClick={() => navigate(`/trips/${trip.id}/itinerary`)}
                        className="border border-blue-600 text-blue-600 px-4 py-2 rounded-xl text-sm font-semibold hover:bg-blue-50 transition flex-1 text-center"
                      >
                        Open Itinerary
                      </button>

                      <button
                        onClick={() => setSelectedHotelTripId(selectedHotelTripId === trip.id ? null : trip.id)}
                        className="border border-green-600 text-green-600 px-4 py-2 rounded-xl text-sm font-semibold hover:bg-green-50 transition flex-1 text-center"
                      >
                        Book Hotels
                      </button>
                      
                      <button
                        onClick={() => handleDeleteTrip(trip.id)}
                        className="border border-red-500 text-red-500 px-3 py-2 rounded-xl text-sm font-semibold hover:bg-red-50 transition"
                      >
                        Delete
                      </button>
                    </div>

                    {selectedHotelTripId === trip.id && (
                      <div className="mt-4 pt-4 border-t border-slate-100">
                        <HotelCards destination={trip.destination || trip.destinationName} />
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </>
  );
}

export default Trips;