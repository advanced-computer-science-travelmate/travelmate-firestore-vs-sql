import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

function DestinationCards() {
  const navigate = useNavigate();
  const [destinations, setDestinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    // 🚀 CONNECT TO YOUR SPRING BOOT PROXY ENDPOINT
    axios
      .get("http://localhost:8080/api/travel/trips/countries/list")
      .then((res) => {
        setDestinations(res.data || []);
      })
      .catch((err) => {
        console.error("Failed to stream live country list from backend proxy:", err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  // Filter country datasets smoothly based on user search patterns
  const filteredDestinations = destinations.filter((dest) =>
    dest.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) {
    return (
      <section className="max-w-7xl mx-auto px-6 py-16">
        <div className="text-slate-500 font-medium animate-pulse text-lg text-center">
          Streaming live European destinations from backend cloud layer...
        </div>
      </section>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-6 py-16">
      {/* 🚀 HEADER SECTION WITH LIVE SEARCH BAR */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-10 gap-4">
        <div>
          <h1 className="text-4xl font-bold text-slate-900">
            Explore European Destinations
          </h1>
          <p className="text-slate-600 mt-2">
            Browse live destination data from your integrated multi-cloud backend layer.
          </p>
        </div>

        <input
          type="text"
          placeholder="Search country catalog..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="border border-slate-300 rounded-xl px-4 py-2.5 max-w-sm w-full bg-white shadow-xs focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {filteredDestinations.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-3xl border border-slate-100 shadow-sm">
          <p className="text-slate-500 font-medium">No destinations match your search criteria.</p>
        </div>
      ) : (
        /* 🚀 THE DYNAMIC COUNTRY GRID CONTAINER */
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 mt-10">
          {filteredDestinations.map((destination) => (
            <div
              key={destination.id}
              className="bg-white rounded-3xl shadow-sm border border-slate-100 overflow-hidden hover:shadow-md transition flex flex-col justify-between p-4"
            >
              <div>
                <img
                  src={destination.image}
                  alt={destination.name}
                  className="h-40 w-full object-cover rounded-2xl mb-4 border border-slate-100"
                />

                <div className="px-1">
                  <h3 className="text-xl font-bold text-slate-900 truncate">
                    {destination.name}
                  </h3>
                  <p className="text-slate-400 text-xs mt-1.5 font-medium">
                    Code Identifier: <span className="font-mono text-slate-600">{destination.id}</span>
                  </p>
                </div>
              </div>

              {/* 🚀 QUICK ACTION: Forward country name straight into the Trips form */}
              <button
                onClick={() => {
                  navigate("/trips", { state: { autoSelectCountry: destination.name } });
                }}
                className="mt-6 w-full bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold py-2.5 rounded-xl transition text-center shadow-2xs"
              >
                Plan Trip Here
              </button>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

export default DestinationCards;