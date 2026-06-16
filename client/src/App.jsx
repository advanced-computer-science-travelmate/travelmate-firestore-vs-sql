import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Dashboard from "./pages/Dashboard";
import Itineraries from "./pages/Itineraries";
import Trips from "./pages/Trips";
import Destinations from "./pages/Destinations";
import CompareDB from "./pages/CompareDB";
import DestinationDetails from "./pages/DestinationDetails";


function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(
    localStorage.getItem("isLoggedIn") === "true"
  );

  function handleLogin() {
    localStorage.setItem("isLoggedIn", "true");
    setIsLoggedIn(true);
  }

  function handleLogout() {
    localStorage.setItem("isLoggedIn", "false");
    setIsLoggedIn(false);
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <Home
              isLoggedIn={isLoggedIn}
              onLogin={handleLogin}
              onLogout={handleLogout}
            />
          }
        />

        <Route
          path="/trips"
          element={
            <Trips
              isLoggedIn={isLoggedIn}
              onLogin={handleLogin}
              onLogout={handleLogout}
            />
          }
        />

        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/itineraries" element={<Itineraries />} />
        <Route path="/destinations" element={<Destinations />} />
        <Route path="/compare-db" element={<CompareDB />} />
 <Route
  path="/destinations/:destinationId"
  element={
    <DestinationDetails
      isLoggedIn={isLoggedIn}
      onLogin={handleLogin}
      onLogout={handleLogout}
    />
  }
/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;