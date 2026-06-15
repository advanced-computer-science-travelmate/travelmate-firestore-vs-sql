import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";

function Trips({ isLoggedIn, onLogin, onLogout }) {
  const [trips, setTrips] = useState(() => {
    const savedTrips = localStorage.getItem("trips");
    return savedTrips ? JSON.parse(savedTrips) : [];
  });

  const [showForm, setShowForm] = useState(false);
  const [destination, setDestination] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  useEffect(() => {
    localStorage.setItem("trips", JSON.stringify(trips));
  }, [trips]);

  function handleCreateTrip(e) {
    e.preventDefault();

    const newTrip = {
      id: Date.now(),
      destination,
      startDate,
      endDate,
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

                <button
                  onClick={() => setShowForm(true)}
                  className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                >
                  Plan a new trip
                </button>
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
                      <input
                        type="text"
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                        placeholder="Germany, Paris, Bali..."
                        required
                        className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
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
                      className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
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
                      className="bg-white rounded-3xl shadow-sm p-6"
                    >
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