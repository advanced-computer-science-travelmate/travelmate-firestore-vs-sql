import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { getEuropeanDestinations } from "../services/destinationService";
import VotingPoll from "../components/VotingPoll";

function Trips({ isLoggedIn, onLogin, onLogout }) {
  const [destinations, setDestinations] = useState([]);
  const [isLoadingDestinations, setIsLoadingDestinations] = useState(true);

  const [trips, setTrips] = useState(() => {
    const savedTrips = localStorage.getItem("trips");
    return savedTrips ? JSON.parse(savedTrips) : [];
  });

  const [showForm, setShowForm] = useState(false);
  const [destination, setDestination] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [showVoting, setShowVoting] = useState(false);

  useEffect(() => {
    async function loadDestinations() {
      try {
        const data = await getEuropeanDestinations();
        setDestinations(data);
      } catch (error) {
        console.error("Failed to load destinations:", error);
      } finally {
        setIsLoadingDestinations(false);
      }
    }

    loadDestinations();
  }, []);

  useEffect(() => {
    localStorage.setItem("trips", JSON.stringify(trips));
  }, [trips]);

  function handleCreateTrip(e) {
    e.preventDefault();

    const selectedDestination = destinations.find(
      (item) => item.name === destination,
    );

    const newTrip = {
      id: Date.now(),
      destination,
      startDate,
      endDate,
      image: selectedDestination?.image || "",
    };

    setTrips([...trips, newTrip]);

    setDestination("");
    setStartDate("");
    setEndDate("");
    setShowForm(false);
  }

  function handleDeleteTrip(id) {
    const updatedTrips = trips.filter((trip) => trip.id !== id);
    setTrips(updatedTrips);
  }

  return (
    <>
      <Navbar
        isLoggedIn={isLoggedIn}
        onLoginClick={onLogin}
        onLogout={onLogout}
      />

      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-7xl mx-auto">
          {!isLoggedIn ? (
            <div className="bg-white rounded-3xl shadow-sm p-10 text-center">
              <h1 className="text-4xl font-bold text-slate-900">
                Login to plan a trip
              </h1>

              <p className="text-slate-600 mt-4">
                Please login or sign up to create and manage your trips.
              </p>

              <button
                onClick={onLogin}
                className="mt-8 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
              >
                Login / Sign Up
              </button>
            </div>
          ) : (
            <>
              <div className="flex items-center justify-between mb-10">
                <div>
                  <h1 className="text-4xl font-bold text-slate-900">
                    Your Trips
                  </h1>
                  <p className="text-slate-600 mt-2">
                    View and manage your travel plans.
                  </p>
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
                <form
                  onSubmit={handleCreateTrip}
                  className="bg-white rounded-3xl shadow-sm p-8 mb-10"
                >
                  <h2 className="text-2xl font-bold text-slate-900 mb-6">
                    Plan a new trip
                  </h2>

                  <div className="grid md:grid-cols-3 gap-6">
                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-2">
                        Where to?
                      </label>

                      <select
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                        required
                        disabled={isLoadingDestinations}
                        className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                      >
                        <option value="">
                          {isLoadingDestinations
                            ? "Loading destinations..."
                            : "Select a destination"}
                        </option>

                        {destinations.map((destination) => (
                          <option key={destination.id} value={destination.name}>
                            {destination.name}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-2">
                        Start date
                      </label>
                      <input
                        type="date"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                        required
                        className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-2">
                        End date
                      </label>
                      <input
                        type="date"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                        required
                        className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>
                  </div>

                  <div className="flex gap-4 mt-8">
                    <button
                      type="submit"
                      disabled={isLoadingDestinations}
                      className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition disabled:bg-slate-400"
                    >
                      Create Trip
                    </button>

                    <button
                      type="button"
                      onClick={() => setShowForm(false)}
                      className="border border-slate-300 text-slate-700 px-6 py-3 rounded-xl font-semibold hover:bg-slate-100 transition"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              )}
              {showVoting && <VotingPoll />}

              {trips.length === 0 ? (
                <div className="bg-white rounded-3xl shadow-sm p-12 text-center">
                  <h2 className="text-3xl font-bold text-slate-900">
                    You haven’t created anything yet
                  </h2>

                  <p className="text-slate-600 mt-4">
                    Start by planning your first trip.
                  </p>

                  <button
                    onClick={() => setShowForm(true)}
                    className="mt-8 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                  >
                    Plan a new trip
                  </button>
                </div>
              ) : (
                <div className="grid md:grid-cols-3 gap-6">
                  {trips.map((trip) => (
                    <div
                      key={trip.id}
                      className="bg-white rounded-3xl shadow-sm overflow-hidden"
                    >
                      {trip.image && (
                        <img
                          src={trip.image}
                          alt={trip.destination}
                          className="h-40 w-full object-cover"
                        />
                      )}

                      <div className="p-6">
                        <h3 className="text-2xl font-bold text-slate-900">
                          {trip.destination}
                        </h3>

                        <p className="text-slate-600 mt-4">
                          📅 {trip.startDate} to {trip.endDate}
                        </p>

                        <div className="flex gap-3 mt-6">
                          <button className="border border-blue-600 text-blue-600 px-5 py-2 rounded-xl font-semibold hover:bg-blue-50 transition">
                            View Itinerary
                          </button>

                          <button
                            onClick={() => handleDeleteTrip(trip.id)}
                            className="border border-red-500 text-red-500 px-5 py-2 rounded-xl font-semibold hover:bg-red-50 transition"
                          >
                            Delete
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </section>
      </main>
    </>
  );
}

export default Trips;
