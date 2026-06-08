const trips = [
  {
    title: "Summer in Bali",
    dates: "12 Jun - 20 Jun 2026",
    guests: "2 Guests",
    image:
      "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=500&q=80",
  },
  {
    title: "Paris Getaway",
    dates: "05 Jul - 12 Jul 2026",
    guests: "2 Guests",
    image:
      "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=500&q=80",
  },
  {
    title: "Swiss Adventure",
    dates: "10 Aug - 18 Aug 2026",
    guests: "3 Guests",
    image:
      "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=500&q=80",
  },
];

function TripCards() {
  return (
    <section className="max-w-7xl mx-auto px-6 pb-16">
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-3xl font-bold text-slate-900">Your Trips</h2>

        <button className="text-blue-600 font-semibold">
          View all trips →
        </button>
      </div>

      <div className="grid md:grid-cols-3 gap-6">
        {trips.map((trip) => (
          <div
            key={trip.title}
            className="bg-white rounded-2xl p-4 shadow-sm hover:shadow-xl transition flex gap-4"
          >
            <img
              src={trip.image}
              alt={trip.title}
              className="w-32 h-32 rounded-xl object-cover"
            />

            <div className="flex flex-col justify-between">
              <div>
                <h3 className="text-lg font-bold text-slate-900">
                  {trip.title}
                </h3>
                <p className="text-sm text-slate-500 mt-2">📅 {trip.dates}</p>
                <p className="text-sm text-slate-500 mt-1">👥 {trip.guests}</p>
              </div>

              <button className="mt-4 border border-blue-600 text-blue-600 px-4 py-2 rounded-xl text-sm font-semibold hover:bg-blue-50">
                View Details
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

export default TripCards;