import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { destinations } from "../Data/destinations";
import axios from "axios";

function DestinationCards() {
  const [showAll, setShowAll] = useState(false);
  const [itineraries, setItineraries] = useState([]); 
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    // GetMapping
    axios.get("http://localhost:8080/api/travel/itineraries/sql")
      .then(response => {
        setItineraries(response.data);
        setLoading(false);
      })
      .catch(err => {
        console.error("Cloud SQL read error:", err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="text-center py-10">Fetching cloud itineraries...</div>;

  const visibleItineraries = showAll ? itineraries : itineraries.slice(0, 4);

  return (
    <section className="max-w-7xl mx-auto px-6 py-14">
      <div className="flex items-center justify-between mb-8">
        <div>
          <p className="text-xs font-bold text-blue-600 uppercase tracking-[0.2em]">Popular places</p>
          <h2 className="text-3xl font-bold text-slate-900 mt-2">Trending destinations</h2>
        </div>

        <button
          onClick={() => setShowAll(!showAll)}
          className="hidden md:inline-flex border border-blue-600 text-blue-600 px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-50 transition"
        >
          {showAll ? "Show less" : "Explore more destinations"}
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {visibleItineraries.map((itin) => (
          <article
          key={itin.id} // Maps to your ItinerarySQL primary key ID
          onClick={() => navigate(`/destinations/${itin.id}`)} // Redirects dynamically
          className="group bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-lg transition cursor-pointer"
          >
          <div className="relative h-44 overflow-hidden">
            <img
              src="https://images.unsplash.com/photo-1469854523086-cc02fe5d8800"
              alt={itin.destination}
              className="h-full w-full object-cover group-hover:scale-105 transition duration-500"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
            <div className="absolute bottom-4 left-4 right-4">
              <h3 className="text-xl font-bold text-white">{itin.destination}</h3>
            </div>
          </div>

          <div className="p-4">
            <p className="text-sm text-slate-600 leading-relaxed min-h-[60px]">
              Activities: {itin.activities ? itin.activities.join(", ") : "Sightseeing, Excursions"}
            </p>
            <button
              onClick={(e) => {
                e.stopPropagation(); // Prevents double routing triggers
                navigate(`/destinations/${itin.id}`);
              }}
              className="mt-4 w-full bg-blue-600 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition"
            >
              View Details
            </button>
          </div>
        </article>
        ))}
      </div>
    </section>
  );
}

export default DestinationCards;