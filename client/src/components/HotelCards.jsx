import { useEffect, useState } from "react";
import { getHotelsByDestination } from "../Services/hotelService";

function HotelCards({ destination }) {
  const [hotels, setHotels] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function fetchHotels() {
      try {
        const data = await getHotelsByDestination(destination);
        setHotels(data);
      } catch (error) {
        console.error("Failed to fetch hotels:", error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchHotels();
  }, [destination]);

  if (isLoading) {
    return (
      <div className="mt-10">
        <p className="text-slate-500">
          Loading hotels...
        </p>
      </div>
    );
  }

  if (hotels.length === 0) {
    return (
      <div className="mt-10">
        <p className="text-slate-500">
          No hotels available.
        </p>
      </div>
    );
  }

  return (
    <section className="mt-10">
      <h2 className="text-2xl font-bold text-slate-900 mb-6">
        Hotels in {destination}
      </h2>

      <div className="grid md:grid-cols-2 gap-6">
        {hotels.map((hotel) => (
          <div
            key={hotel.id}
            className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm"
          >
            <h3 className="text-xl font-bold text-slate-900">
              {hotel.name}
            </h3>

            <p className="text-slate-600 mt-2">
              ⭐ {hotel.rating}
            </p>

            <p className="text-slate-600 mt-2">
              📍 {hotel.address}
            </p>

            <a
              href={hotel.mapsUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-block mt-5 bg-blue-600 text-white px-5 py-2 rounded-xl font-semibold hover:bg-blue-700 transition"
            >
              View Hotel
            </a>
          </div>
        ))}
      </div>
    </section>
  );
}

export default HotelCards;