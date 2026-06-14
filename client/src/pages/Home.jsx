import { useState } from "react";
import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import DestinationCards from "../components/DestinationCards";
import TripCards from "../components/TripCards";
import DatabaseComparison from "../components/DatabaseComparison";
import LoginModal from "../components/LoginModal";

function Home() {
  const [showLogin, setShowLogin] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar onLoginClick={() => setShowLogin(true)} />
      <Hero />
      <DestinationCards />
      <TripCards />
      <DatabaseComparison />

      {showLogin && <LoginModal onClose={() => setShowLogin(false)} />}
    </div>
  );
}

export default Home;