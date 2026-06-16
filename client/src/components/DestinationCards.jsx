import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getEuropeanDestinations } from "../services/destinationService";

function DestinationCards() {
  const [destinations, setDestinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAll, setShowAll] = useState(false);

  const navigate = useNavigate();

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

  const visibleDestinations = showAll
    ? destinations
    : destinations.slice(0, 4);

  if (loading) {
    return (
      <section className="max-w-7xl mx-auto px-6 py-14">
        <p className="text-slate-600">Loading destinations...</p>
      </section>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-6 py-14">
      <div className="flex items-center justify-between mb-8">
        <div>
          <p className="text-xs font-bold text-blue-600 uppercase tracking-[0.2em]">
            Popular places
          </p>

          <h2 className="text-3xl font-bold text-slate-900 mt-2">
            Trending destinations
          </h2>

          <p className="text-slate-500 mt-2 text-sm">
            Pick a destination and start planning your next trip.
          </p>
        </div>

        {destinations.length > 4 && (
          <button
            onClick={() => setShowAll(!showAll)}
            className="hidden md:inline-flex border border-blue-600 text-blue-600 px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-50 transition"
          >
            {showAll ? "Show less" : "Explore more destinations"}
          </button>
        )}
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {visibleDestinations.map((destination) => (
          <article
            key={destination.id}
            onClick={() => navigate(`/destinations/${destination.id}`)}
            className="group bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-lg transition cursor-pointer"
          >
            <div className="relative h-44 overflow-hidden">
              <img
                src={destination.image}
                alt={destination.name}
                className="h-full w-full object-cover group-hover:scale-105 transition duration-500"
              />

              <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>

              <div className="absolute bottom-4 left-4 right-4">
                <h3 className="text-xl font-bold text-white">
                  {destination.name}
                </h3>

                <p className="text-xs text-white/85 mt-1">
                  {destination.city}
                </p>
              </div>
            </div>

            <div className="p-4">
              <p className="text-sm text-slate-600 leading-relaxed min-h-[60px]">
                {destination.description}
              </p>

              <button
                onClick={(e) => e.stopPropagation()}
                className="mt-4 w-full bg-blue-600 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition"
              >
                Add to Trip
              </button>
            </div>
          </article>
        ))}
      </div>

      {destinations.length > 4 && (
        <button
          onClick={() => setShowAll(!showAll)}
          className="md:hidden mt-8 w-full border border-blue-600 text-blue-600 px-5 py-3 rounded-xl font-semibold hover:bg-blue-50 transition"
        >
          {showAll ? "Show less" : "Explore more destinations"}
        </button>
      )}
    </section>
  );
}

export default DestinationCards;