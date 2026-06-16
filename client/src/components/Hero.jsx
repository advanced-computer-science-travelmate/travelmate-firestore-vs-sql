import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getEuropeanDestinations } from "../services/destinationService";

function Hero({ isLoggedIn, onLoginClick }) {
  const navigate = useNavigate();

  const [destinations, setDestinations] = useState([]);
  const [selectedDestination, setSelectedDestination] = useState("");
  const [travelDate, setTravelDate] = useState("");
  const [travelers, setTravelers] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDestinations() {
      try {
        const data = await getEuropeanDestinations();
        setDestinations(data);
      } catch (error) {
        console.error("Failed to load destinations:", error);
      } finally {
        setLoading(false);
      }
    }

    loadDestinations();
  }, []);

  function handleFindTrips() {
    if (!isLoggedIn) {
      onLoginClick();
      return;
    }

    navigate("/trips");
  }

  return (
    <section className="relative overflow-hidden rounded-b-[32px] bg-gradient-to-br from-sky-100 via-blue-50 to-white">
      <div className="max-w-7xl mx-auto px-6 py-20 text-center">
        <p className="text-blue-600 font-semibold mb-3">
          Plan. Explore. Compare.
        </p>

        <h1 className="text-5xl md:text-6xl font-extrabold text-slate-900 leading-tight">
          Explore the world. <br />
          Your adventure awaits.
        </h1>

        <p className="mt-6 text-lg text-slate-600 max-w-2xl mx-auto">
          Create travel plans, manage itineraries, track expenses, and compare
          Firestore with Cloud SQL using a real full-stack application.
        </p>

        <div className="mt-10 bg-white rounded-3xl shadow-xl max-w-4xl mx-auto p-4">
          <div className="grid md:grid-cols-4 gap-3">
            <div className="text-left px-4 py-3 rounded-2xl bg-slate-50">
              <p className="text-xs font-semibold text-slate-500">
                Destination
              </p>

              <select
                value={selectedDestination}
                onChange={(e) => setSelectedDestination(e.target.value)}
                disabled={loading}
                className="w-full bg-transparent outline-none text-slate-800 mt-1"
              >
                <option value="">
                  {loading ? "Loading..." : "Where are you going?"}
                </option>

                {destinations.map((destination) => (
                  <option key={destination.id} value={destination.name}>
                    {destination.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="text-left px-4 py-3 rounded-2xl bg-slate-50">
              <p className="text-xs font-semibold text-slate-500">Dates</p>

              <input
                type="date"
                value={travelDate}
                onChange={(e) => setTravelDate(e.target.value)}
                className="w-full bg-transparent outline-none text-slate-800 mt-1"
              />
            </div>

            <div className="text-left px-4 py-3 rounded-2xl bg-slate-50">
              <p className="text-xs font-semibold text-slate-500">
                Travelers
              </p>

              <input
                type="number"
                min="1"
                value={travelers}
                onChange={(e) => setTravelers(e.target.value)}
                placeholder="2 guests"
                className="w-full bg-transparent outline-none text-slate-800 mt-1"
              />
            </div>

            <button
              onClick={handleFindTrips}
              className="bg-blue-600 text-white rounded-2xl font-semibold hover:bg-blue-700 transition"
            >
              Find Trips →
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}

export default Hero;