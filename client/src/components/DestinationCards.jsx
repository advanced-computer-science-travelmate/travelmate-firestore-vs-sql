import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { destinationService } from "../services/destinationService";

function DestinationCards() {
  const [destinations, setDestinations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDestinations() {
      const data = await destinationService.getEuropeanDestinations();
      setDestinations(data);
      setLoading(false);
    }

    loadDestinations();
  }, []);

  if (loading) {
    return (
      <section className="max-w-7xl mx-auto px-6 py-16">
        <p className="text-slate-600">Loading destinations...</p>
      </section>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-6 py-16">
      <h1 className="text-4xl font-bold text-slate-900">
        Explore European Destinations
      </h1>

      <p className="text-slate-600 mt-3">
        Browse live destination data from the REST Countries API.
      </p>

      <div className="grid md:grid-cols-4 gap-6 mt-10">
        {destinations.map((destination) => (
          <Link
            key={destination.id}
            to={`/destinations/${destination.id}`}
            className="bg-white rounded-3xl shadow-sm overflow-hidden hover:shadow-md transition"
          >
            <img
              src={destination.image}
              alt={destination.name}
              className="h-40 w-full object-cover"
            />

            <div className="p-5">
              <h3 className="text-xl font-bold text-slate-900">
                {destination.name}
              </h3>

              <p className="text-slate-600 text-sm mt-2">
                {destination.description}
              </p>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}

export default DestinationCards;