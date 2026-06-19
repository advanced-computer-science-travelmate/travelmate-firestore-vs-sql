import { useParams, useNavigate } from "react-router-dom";
import ItineraryBuilder from "../components/ItineraryBuilder";

function ItineraryDetails() {
  const { tripId } = useParams(); // 🟢 Dynamically reads whatever ID is in the URL path
  const navigate = useNavigate();

  const savedTrips = localStorage.getItem("trips");
  const trips = savedTrips ? JSON.parse(savedTrips) : [];

  // 1. Dynamic route match finder
  const trip = trips.find((item) => 
    String(item.id) === String(tripId) || String(item.tripId) === String(tripId)
  );

  if (!trip) {
    return (
      <main className="min-h-screen bg-slate-50 p-16 text-center">
        <section className="max-w-5xl mx-auto bg-white rounded-3xl shadow-sm p-10 text-center">
          <h1 className="text-3xl font-bold text-slate-900">Trip Not Found</h1>
          <p className="text-slate-500 mt-2">The selected itinerary session could not be tracked.</p>
          <button 
            onClick={() => navigate("/trips")} 
            className="mt-6 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
          >
            Back to Trips
          </button>
        </section>
      </main>
    );
  }

  // 🚀 THE DYNAMIC EXTRACTOR: Resolves object structures safely for ANY trip ID
  const destinationName = trip.destination && typeof trip.destination === 'object'
    ? trip.destination.name 
    : (trip.destinationName || trip.destination || "European Destination");

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-16">
      <section className="max-w-5xl mx-auto">
        <button 
          onClick={() => navigate("/trips")} 
          className="mb-6 text-blue-600 font-semibold hover:underline flex items-center gap-1"
        >
          ← Back to Trips
        </button>

        <div className="bg-white rounded-3xl shadow-sm p-8 border border-slate-100">
          <h1 className="text-4xl font-bold text-slate-900">
            {destinationName} Itinerary
          </h1>
          <p className="text-slate-600 mt-3">📅 Timeline: {trip.startDate} to {trip.endDate}</p>

          {/* Quick Stats Metric Bar Dashboard Layout */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-8 mb-8">
            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
              <p className="text-sm text-slate-500 font-medium">Destination</p>
              <p className="font-bold text-slate-900 mt-1 truncate">{destinationName}</p>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
              <p className="text-sm text-slate-500 font-medium">Dates</p>
              <p className="font-bold text-slate-700 mt-1 text-xs md:text-sm">
                {trip.startDate} → {trip.endDate}
              </p>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
              <p className="text-sm text-slate-500 font-medium">Travellers</p>
              <p className="font-bold text-slate-900 mt-1 text-sm">
                {trip.adults || 1} ADL · {trip.children || 0} CHD
              </p>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
              <p className="text-sm text-slate-500 font-medium">Rooms Allocations</p>
              <p className="font-bold text-slate-900 mt-1">{trip.rooms || 1} Unit</p>
            </div>
          </div>

          {/* 🟢 Safely passing clean string representation down to child element */}
          <ItineraryBuilder trip={trip} destinationName={destinationName} />
        </div>
      </section>
    </main>
  );
}

export default ItineraryDetails;