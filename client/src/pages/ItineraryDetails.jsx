import { useParams, useNavigate } from "react-router-dom";
import ItineraryBuilder from "../components/ItineraryBuilder";

function ItineraryDetails() {
  const { tripId } = useParams();
  const navigate = useNavigate();

  const savedTrips = localStorage.getItem("trips");
  const trips = savedTrips ? JSON.parse(savedTrips) : [];

  const trip = trips.find((item) => String(item.id) === String(tripId));

  if (!trip) {
    return (
      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-5xl mx-auto bg-white rounded-3xl shadow-sm p-10">
          <h1 className="text-3xl font-bold text-slate-900">
            Trip not found
          </h1>

          <button
            onClick={() => navigate("/trips")}
            className="mt-6 bg-blue-600 text-white px-6 py-3 rounded-xl font-semibold"
          >
            Back to Trips
          </button>
        </section>
      </main>
    );
  }

  const destination = trip.destination || trip.destinationName;

  return (
    <main className="min-h-screen bg-slate-50 px-6 py-16">
      <section className="max-w-5xl mx-auto">
        <button
          onClick={() => navigate("/trips")}
          className="mb-6 text-blue-600 font-semibold hover:underline"
        >
          ← Back to Trips
        </button>

        <div className="bg-white rounded-3xl shadow-sm p-8">
          <h1 className="text-4xl font-bold text-slate-900">
            {destination} Itinerary
          </h1>

          <p className="text-slate-600 mt-3">
  📅 {trip.startDate} to {trip.endDate}
</p>

<div className="grid md:grid-cols-4 gap-4 mt-8 mb-8">
  <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
    <p className="text-sm text-slate-500">Destination</p>
    <p className="font-bold text-slate-900 mt-1">{destination}</p>
  </div>

  <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
    <p className="text-sm text-slate-500">Dates</p>
    <p className="font-bold text-slate-900 mt-1">
      {trip.startDate} → {trip.endDate}
    </p>
  </div>

  <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
    <p className="text-sm text-slate-500">Travellers</p>
    <p className="font-bold text-slate-900 mt-1">
      {trip.adults || 1} adults · {trip.children || 0} children
    </p>
  </div>

  <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5">
    <p className="text-sm text-slate-500">Rooms</p>
    <p className="font-bold text-slate-900 mt-1">
      {trip.rooms || 1}
    </p>
  </div>
</div>

<ItineraryBuilder trip={trip} />
        </div>
      </section>
    </main>
  );
}

export default ItineraryDetails;