import { Link, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";

function Navbar({ onLoginClick, onLogout, isLoggedIn, currentUser }) {

  const location = useLocation();
  const [hasSession, setHasSession] = useState(false);

  // Sync state with local storage on render/route changes
  useEffect(() => {
    const session = localStorage.getItem("userSession");
    setHasSession(!!session || isLoggedIn);
  }, [location, isLoggedIn]);

  return (
    <header className="sticky top-0 z-50 bg-white/90 backdrop-blur-md border-b border-slate-200">
      <nav className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <div className="h-10 w-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-bold">
            T
          </div>

          <h1 className="text-2xl font-bold text-slate-900">
            Travel<span className="text-blue-600">Mate</span>
          </h1>
        </Link>

        <div className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-700">
          <Link to="/" className="hover:text-blue-600">
            Home
          </Link>

          <Link to="/trips" className="hover:text-blue-600">
            Trips
          </Link>

          <Link to="/destinations" className="hover:text-blue-600">
            Destinations
          </Link>

          <Link to="/compare-db" className="hover:text-blue-600">
            Compare DB
          </Link>
        </div>

        {/* Wrapper to group the Name and the Auth Button together side-by-side */}
        <div className="flex items-center gap-4">
          
          {/* It checks hasSession first, then safely reads the name string */}
          {hasSession && currentUser && (
            <span className="text-sm font-semibold text-slate-700 bg-slate-100 border border-slate-200 px-3 py-1.5 rounded-xl flex items-center gap-1.5 shadow-sm">
              👤 {currentUser.name || "Active User"}
            </span>
          )}

          {hasSession ? (
            <button
              onClick={onLogout}
              className="bg-red-500 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-red-600 transition"
            >
              Logout
            </button>
          ) : (
            <button
              onClick={onLoginClick}
              className="bg-blue-600 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-blue-700 transition"
            >
              Login / Sign Up
            </button>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Navbar;