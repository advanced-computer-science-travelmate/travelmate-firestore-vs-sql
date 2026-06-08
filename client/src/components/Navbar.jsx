import { Link } from "react-router-dom";

function Navbar({ onLoginClick }) {
  return (
    <header className="sticky top-0 z-50 bg-white/90 backdrop-blur-md border-b border-slate-200">
      <nav className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="h-10 w-10 rounded-full bg-blue-600 flex items-center justify-center text-white font-bold">
            T
          </div>
          <h1 className="text-2xl font-bold text-slate-900">
            Travel<span className="text-blue-600">Mate</span>
          </h1>
        </div>

        <div className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-700">
          <a href="#" className="hover:text-blue-600">
            Home
          </a>
          <a href="#" className="hover:text-blue-600">
            Trips
          </a>
          <a href="#" className="hover:text-blue-600">
            Destinations
          </a>
          <a href="#" className="hover:text-blue-600">
            Compare DB
          </a>
        </div>

       <button
  onClick={onLoginClick}
  className="bg-blue-600 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-blue-700 transition"
>
  Login / Sign Up
</button>
      </nav>
    </header>
  );
}

export default Navbar;
