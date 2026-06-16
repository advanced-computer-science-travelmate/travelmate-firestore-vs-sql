import { useState } from "react";
import Navbar from "../components/Navbar";
import Hero from "../components/Hero";
import DestinationCards from "../components/DestinationCards";
import DatabaseComparison from "../components/DatabaseComparison";
import LoginModal from "../components/LoginModal";

function Home({ isLoggedIn, onLogin, onLogout }) {
  const [showLogin, setShowLogin] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50">
      <Hero isLoggedIn={isLoggedIn} onLoginClick={() => setShowLogin(true)} />
      <DestinationCards />

      <DatabaseComparison />

      {showLogin && <LoginModal onClose={() => setShowLogin(false)} />}
    </div>
  );
}

export default Home;
