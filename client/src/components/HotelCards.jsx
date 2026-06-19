import React, { useState, useEffect } from "react";
import { getHotelsByDestination } from "../services/hotelService"; 

function HotelCards({ destination }) {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!destination) return;

    setLoading(true);
    setError(null);

    getHotelsByDestination(destination)
      .then((data) => {
        setHotels(data);
      })
      .catch((err) => {
        console.error("Google Places API engine failure:", err);
        setError("Unable to process live hotel rates at this time.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [destination]);

  if (loading) {
    return (
      <div className="p-4 text-center text-sm font-semibold text-slate-500 animate-pulse">
        Polling Google Places API for accommodations in {destination}...
      </div>
    );
  }

  if (error) {
    return <div className="p-4 text-center text-xs font-medium text-red-500">{error}</div>;
  }

  if (hotels.length === 0) {
    return (
      <div className="p-4 text-center text-xs font-medium text-slate-500">
        No active lodging listings returned for {destination}.
      </div>
    );
  }

  return (
    <div className="space-y-3">
      <h5 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
        Verified Accommodations in {destination}
      </h5>
      
      <div className="grid grid-cols-1 gap-3 max-h-64 overflow-y-auto pr-1">
        {hotels.map((hotel) => (
          <div 
            key={hotel.id} 
            className="bg-slate-50 border border-slate-200 p-3.5 rounded-xl flex justify-between items-center hover:border-blue-300 transition shadow-2xs"
          >
            <div className="flex-1 min-w-0 pr-3">
              <p className="font-bold text-slate-900 text-sm truncate">{hotel.name}</p>
              <p className="text-xs text-slate-500 mt-0.5 truncate">{hotel.address}</p>
              
              <div className="flex items-center gap-3 mt-1.5">
                {hotel.rating && hotel.rating !== "No rating" ? (
                  <span className="text-[11px] text-amber-600 font-bold flex items-center gap-0.5">
                    ⭐ {hotel.rating}
                  </span>
                ) : (
                  <span className="text-[10px] text-slate-400 italic">No ratings yet</span>
                )}
                
                {hotel.mapsUrl && (
                  <a 
                    href={hotel.mapsUrl} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="text-[11px] text-blue-600 font-medium hover:underline"
                  >
                    🗺️ View on Maps
                  </a>
                )}
              </div>
            </div>
            
            <div className="text-right flex-shrink-0">
              <span className="text-sm font-black text-slate-900 block">${hotel.price}</span>
              <span className="text-[9px] text-slate-400 block font-medium">per night</span>
              <button 
                type="button"
                className="mt-2 bg-blue-600 hover:bg-blue-700 text-white text-[10px] font-bold px-3 py-1.5 rounded-xl transition shadow-2xs"
                onClick={() => alert(`Securing booking reference token for: ${hotel.name}`)}
              >
                Book Stay
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default HotelCards;