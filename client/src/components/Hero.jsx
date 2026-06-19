import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Hero({ 
  isLoggedIn, 
  onLoginClick, 
  destinations, 
  selectedDestination, 
  setSelectedDestination, 
  isLoadingDestinations, 
  onSearchSubmit 
}) {
  const navigate = useNavigate();

  // Keep only the states that Hero actually owns locally
  const [travelDate, setTravelDate] = useState("");
  const [travelers, setTravelers] = useState("");

  return (
    <section className="relative overflow-hidden rounded-b-[32px] bg-gradient-to-br from-sky-100 via-blue-50 to-white">
      <div className="max-w-7xl mx-auto px-6 py-20 text-center">
        <p className="text-blue-600 font-semibold mb-3">
          Plan. Explore. Compare.
        </p>

        <h1 className="text-5xl md:text-6xl font-extrabold text-slate-900 leading-tight">
          Explore the world. <br />
          Your adventure awaits.
        </h1>

        <p className="mt-6 text-lg text-slate-600 max-w-2xl mx-auto mb-12">
          Create travel plans, manage itineraries, track expenses, and compare
          Firestore with Cloud SQL using a real full-stack application.
        </p>

        {/* 🚀 HORIZONTAL SEARCH WIDGET COHESIVE CONTAINMENT BAR */}
        <div className="max-w-4xl mx-auto bg-white p-4 rounded-3xl shadow-md border border-slate-100 flex flex-col md:flex-row gap-4 items-center">
          
          {/* Destination Dropdown */}
          <div className="flex-1 w-full text-left bg-slate-50 p-3 rounded-2xl border border-slate-100">
            <label className="block text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">
              Destination
            </label>
            <select
              value={selectedDestination}
              onChange={(e) => setSelectedDestination(e.target.value)}
              disabled={isLoadingDestinations}
              className="w-full bg-transparent text-sm font-semibold text-slate-800 focus:outline-none appearance-none cursor-pointer"
            >
              <option value="">{isLoadingDestinations ? "Loading countries..." : "Where are you going?"}</option>
              {destinations.map((dest) => (
                <option key={dest.id} value={dest.name} className="text-slate-900">
                  {dest.name}
                </option>
              ))}
            </select>
          </div>

          {/* Travel Date */}
          <div className="flex-1 w-full text-left bg-slate-50 p-3 rounded-2xl border border-slate-100">
            <label className="block text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">
              Dates
            </label>
            <input
              type="date"
              value={travelDate}
              onChange={(e) => setTravelDate(e.target.value)}
              className="w-full bg-transparent text-sm font-semibold text-slate-800 focus:outline-none mt-0.5"
            />
          </div>

          {/* Travelers Count */}
          <div className="flex-1 w-full text-left bg-slate-50 p-3 rounded-2xl border border-slate-100">
            <label className="block text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">
              Travelers
            </label>
            <input
              type="number"
              min="1"
              value={travelers}
              onChange={(e) => setTravelers(e.target.value)}
              placeholder="2 guests"
              className="w-full bg-transparent text-sm font-semibold text-slate-800 focus:outline-none mt-0.5"
            />
          </div>

          {/* Submission Trigger Action Button */}
          <button
            onClick={onSearchSubmit}
            className="w-full md:w-auto bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-4 rounded-2xl transition shadow-sm whitespace-nowrap self-stretch flex items-center justify-center"
          >
            Find Trips →
          </button>
        </div>

      </div>
    </section>
  );
}

export default Hero;