const destinations = [
  {
    name: "Paris",
    country: "France",
    price: "From €420",
    rating: "4.8",
    image:
      "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=600&q=80",
  },
  {
    name: "Santorini",
    country: "Greece",
    price: "From €560",
    rating: "4.9",
    image:
      "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=600&q=80",
  },
  {
    name: "Bali",
    country: "Indonesia",
    price: "From €650",
    rating: "4.7",
    image:
      "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=600&q=80",
  },
  {
    name: "Swiss Alps",
    country: "Switzerland",
    price: "From €380",
    rating: "4.9",
    image:
      "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=600&q=80",
  },
];

function DestinationCards() {
  return (
    <section className="max-w-7xl mx-auto px-6 py-16">
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-3xl font-bold text-slate-900">
          Popular Destinations
        </h2>

        <button className="text-blue-600 font-semibold">
          View all →
        </button>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {destinations.map((destination) => (
          <div
            key={destination.name}
            className="bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition"
          >
            <img
              src={destination.image}
              alt={destination.name}
              className="h-48 w-full object-cover"
            />

            <div className="p-5">
              <h3 className="text-xl font-bold text-slate-900">
                {destination.name}
              </h3>
              <p className="text-slate-500">{destination.country}</p>

              <div className="flex items-center justify-between mt-5">
                <span className="text-yellow-500 font-semibold">
                  ★ {destination.rating}
                </span>
                <span className="text-slate-700 font-semibold">
                  {destination.price}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

export default DestinationCards;