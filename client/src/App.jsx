import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Dashboard from "./pages/Dashboard";
// import Itineraries from "./pages/Itineraries";
import Trips from "./pages/Trips";
import Destinations from "./pages/Destinations";
import CompareDB from "./pages/CompareDB";
import DestinationDetails from "./pages/DestinationDetails";
import Navbar from "./components/Navbar";
import LoginModal from "./components/LoginModal";

function App() {
  const [currentUser, setCurrentUser] = useState(() => {
    const savedUser = localStorage.getItem("userSession");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const isLoggedIn = !!currentUser;

  // Track whether the authentication modal overlay is open or closed
  const [showLoginModal, setShowLoginModal] = useState(false);

  // Triggered via individual child landing components if needed
  function handleLogin() {
    setShowLoginModal(true);
  }

  function handleLogout() {
    localStorage.removeItem("name");
    localStorage.removeItem("user_email");
    localStorage.removeItem("user_nosql_id");
    localStorage.removeItem("user_sql_id");
    localStorage.removeItem("userSession");
    localStorage.removeItem("trips"); // Clears the local card cache
    
    window.location.href = "/";
  }

  return (
    <BrowserRouter>
    <Navbar 
        isLoggedIn={isLoggedIn} 
        currentUser={currentUser}
        onLoginClick={() => setShowLoginModal(true)} 
        onLogout={handleLogout} 
    />
    
    {showLoginModal && (
        <LoginModal 
      onClose={() => setShowLoginModal(false)} 
      onLoginSuccess={(userData) => {
        // 1. Maintain the session object record mapping
        localStorage.setItem("userSession", JSON.stringify(userData));
        
        // 🚀 CLEAN & SYSTEM-DRIVEN: Strictly bind to the incoming auth fields
        localStorage.setItem("name", userData.name);
        localStorage.setItem("user_email", userData.email || userData.user_email);
        localStorage.setItem("user_nosql_id", userData.noSqlId || userData.user_nosql_id);
        localStorage.setItem("user_sql_id", userData.sqlId || userData.user_sql_id);
       
        // 2. Clear out the display rendering blocks
        setCurrentUser(userData);
        setShowLoginModal(false); 
      }} 
    />
      )}
      <Routes>
        <Route
          path="/"
          element={
            <Home
              isLoggedIn={isLoggedIn} 
              onLogin={() => setShowLoginModal(true)} 
              onLogout={handleLogout}
            />
          }
        />

        <Route
          path="/trips"
          element={
            <Trips
              isLoggedIn={isLoggedIn} 
              onLogin={() => setShowLoginModal(true)} 
              onLogout={handleLogout} 
              />
            }
        />

        <Route path="/dashboard" element={<Dashboard />} />
        {/* <Route path="/itineraries" element={<Itineraries />} /> */}
        <Route path="/destinations" element={<Destinations />} />
        <Route path="/compare-db" element={<CompareDB />} />
        <Route
          path="/destinations/:destinationId"
          element={
            <DestinationDetails 
            isLoggedIn={isLoggedIn} 
            currentUser={currentUser} 
            onLogin={() => setShowLoginModal(true)} 
            onLogout={handleLogout} />
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;