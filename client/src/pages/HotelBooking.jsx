import { useParams, useNavigate } from "react-router-dom";
import HotelCards from "../components/HotelCards";

function HotelBooking() {
  const { tripId } = useParams();
  const navigate = useNavigate();

  const savedTrips = localStorage.getItem("trips");
  const trips = savedTrips ? JSON.parse(savedTrips) : [];

  // Match the dynamic trip ID from the URL path
  const trip = trips.find((item) => String(item.id) === String(tripId));

  if (!trip) {
    return (
      <main className="min-h-screen bg-slate-50 p-16 text-center">
        <h1 className="text-3xl font-bold text-slate-900">Trip Session Missing</h1>
        <button onClick={() => navigate("/trips")} className="mt-4 bg-blue-600 text-white px-4 py-2 rounded-xl">
          Back to Trips
        </button>
      </main>
    );
  }

  // Extract the destination string safely
  const destinationName = trip.destination && Array.isArray(trip.destination) && trip.destination.length > 0
    ? trip.destination[0].name
    : (typeof trip.destination === 'object' && trip.destination !== null 
        ? trip.destination.name 
        : (trip.destinationName || trip.destination || "Destination"));

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
          <div className="border-b border-slate-100 pb-6 mb-6">
            <h1 className="text-4xl font-bold text-slate-900">Accommodations Hub</h1>
            <p className="text-slate-500 mt-2">
              Discover verified stays in <span className="font-semibold text-slate-800">{destinationName}</span> matching your timeline: {trip.startDate} to {trip.endDate}
            </p>
          </div>

          {/* Mount the hotel component inside a full workspace layout */}
          <div className="mt-4">
            <HotelCards destination={destinationName} />
          </div>
        </div>
      </section>
    </main>
  );
}

export default HotelBooking;