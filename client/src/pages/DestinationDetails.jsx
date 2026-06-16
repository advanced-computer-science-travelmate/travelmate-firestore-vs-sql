import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { destinationService } from "../services/destinationService";
import HotelCards from "../components/HotelCards";

function DestinationDetails({ isLoggedIn, onLogin, onLogout }) {
  const { destinationId } = useParams();
  const navigate = useNavigate();

  const [destination, setDestination] = useState(null);
  const [loading, setLoading] = useState(true);

  const [showDateForm, setShowDateForm] = useState(false);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    async function loadDestination() {
      try {
        const data = await destinationService.getEuropeanDestinations();

        const selectedDestination = data.find(
          (item) => item.id === Number(destinationId)
        );

        setDestination(selectedDestination);
      } catch (error) {
        console.error("Failed to load destination:", error);
      } finally {
        setLoading(false);
      }
    }

    loadDestination();
  }, [destinationId]);

  function handleAddToTrip(e) {
    e.preventDefault();

    const savedTrips = localStorage.getItem("trips");
    const trips = savedTrips ? JSON.parse(savedTrips) : [];

    const newTrip = {
    id: Date.now(),
    destination: destination.name,
    startDate,
    endDate,
    image: destination.image,
    };

    localStorage.setItem("trips", JSON.stringify([...trips, newTrip]));

    setMessage("Trip added successfully!");
    setShowDateForm(false);
    setStartDate("");
    setEndDate("");

    setTimeout(() => {
      navigate("/trips");
    }, 800);

    // Create the exact data transfer body your synchronized seed layers expect
  const tripPayload = {
    destinationName: destination.name,
    startDate: startDate,
    endDate: endDate,
    maxTravelers: 2
  };

  // Dispatch simultaneously to your multi-cloud persistence engine
  axios.post('http://localhost:8080/api/travel/trips/create', tripPayload)
    .then((response) => {
      setMessage("🎉 Multi-cloud transaction successful! Trip replicated across systems.");
      setShowDateForm(false);
      setStartDate("");
      setEndDate("");
      
      setTimeout(() => {
        navigate("/trips");
      }, 1000);
    })
    .catch((error) => {
      console.error("Database persistence fallback fail:", error);
      setMessage("⚠️ Sync failed. Reverting to structural fallback storage locally.");
      
      // Fallback to local storage if your backend instance is temporarily offline
      const savedTrips = localStorage.getItem("trips");
      const trips = savedTrips ? JSON.parse(savedTrips) : [];
      localStorage.setItem("trips", JSON.stringify([...trips, { ...tripPayload, id: Date.now(), image: destination.image }]));
      navigate("/trips");
    });
  }

  if (loading) {
    return (
      <>
        <main className="min-h-screen bg-slate-50 px-6 py-16">
          <section className="max-w-7xl mx-auto">
            <p className="text-slate-600">Loading destination...</p>
          </section>
        </main>
      </>
    );
  }

  if (!destination) {
    return (
      <>
        <Navbar
          isLoggedIn={isLoggedIn}
          onLoginClick={onLogin}
          onLogout={onLogout}
        />

        <main className="min-h-screen bg-slate-50 px-6 py-16">
          <section className="max-w-7xl mx-auto">
            <h1 className="text-3xl font-bold text-slate-900">
              Destination not found
            </h1>

            <Link
              to="/destinations"
              className="inline-block mt-6 text-blue-600 font-semibold"
            >
              ← Back to destinations
            </Link>
          </section>
        </main>
      </>
    );
  }

  return (
    <>
      <Navbar
        isLoggedIn={isLoggedIn}
        onLoginClick={onLogin}
        onLogout={onLogout}
      />

      <main className="min-h-screen bg-slate-50">
        <section className="max-w-7xl mx-auto px-6 py-12">
          <Link
            to="/destinations"
            className="text-blue-600 font-semibold text-sm"
          >
            ← Back to destinations
          </Link>

          <div className="mt-8 bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
            <img
              src={destination.image}
              alt={destination.name}
              className="w-full h-80 object-cover"
            />

            <div className="p-8">
              <h1 className="text-4xl font-bold text-slate-900">
                {destination.name}
              </h1>

              <p className="text-slate-600 mt-4 max-w-3xl">
                {destination.overview}
              </p>

              <div className="grid md:grid-cols-2 gap-8 mt-10">
                <div>
                  <h2 className="text-2xl font-bold text-slate-900">
                    Famous Cities
                  </h2>

                  <div className="flex flex-wrap gap-3 mt-4">
                    {destination.famousCities?.map((city) => (
                      <span
                        key={city}
                        className="bg-blue-50 text-blue-700 px-4 py-2 rounded-xl text-sm font-semibold"
                      >
                        {city}
                      </span>
                    ))}
                  </div>
                </div>

                <div>
                  <h2 className="text-2xl font-bold text-slate-900">
                    Famous Places
                  </h2>

                  <div className="flex flex-wrap gap-3 mt-4">
                    {destination.famousPlaces?.map((place) => (
                      <span
                        key={place}
                        className="bg-slate-100 text-slate-700 px-4 py-2 rounded-xl text-sm font-semibold"
                      >
                        {place}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
              {/* Hotels Section */}
              <HotelCards destination={destination.name} />

              {message && (
                <p className="mt-8 bg-green-50 text-green-700 px-5 py-3 rounded-xl font-semibold">
                  {message}
                </p>
              )}

              {!isLoggedIn ? (
                <div className="mt-10 bg-blue-50 border border-blue-100 rounded-2xl p-6">
                  <h3 className="text-xl font-bold text-slate-900">
                    Login to add this destination
                  </h3>

                  <p className="text-slate-600 mt-2">
                    Please login or sign up to save this destination to your
                    trips.
                  </p>

                  <button
                    onClick={onLogin}
                    className="mt-5 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                  >
                    Login / Sign Up
                  </button>
                </div>
              ) : (
                <div className="mt-10">
                  {!showDateForm ? (
                    <button
                      onClick={() => setShowDateForm(true)}
                      className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                    >
                      Add to Trip
                    </button>
                  ) : (
                    <form
                      onSubmit={handleAddToTrip}
                      className="bg-slate-50 border border-slate-200 rounded-2xl p-6 max-w-2xl"
                    >
                      <h3 className="text-xl font-bold text-slate-900">
                        Choose your travel dates
                      </h3>

                      <div className="grid md:grid-cols-2 gap-5 mt-5">
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

                      <div className="flex gap-4 mt-6">
                        <button
                          type="submit"
                          className="bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
                        >
                          Save Trip
                        </button>

                        <button
                          type="button"
                          onClick={() => setShowDateForm(false)}
                          className="border border-slate-300 text-slate-700 px-6 py-3 rounded-xl font-semibold hover:bg-slate-100 transition"
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  )}
                </div>
              )}
            </div>
          </div>
        </section>
      </main>
    </>
  );
}

export default DestinationDetails;