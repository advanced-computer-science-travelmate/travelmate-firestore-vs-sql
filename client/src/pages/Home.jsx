import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // 🚀 Added navigation driver
import axios from "axios";
import Hero from "../components/Hero";
import DestinationCards from "../components/DestinationCards";
import DatabaseComparison from "../components/DatabaseComparison";
import LoginModal from "../components/LoginModal";

// 🟢 FIXED: Added 'onLogin' inside the destructured function parameters
function Home({ isLoggedIn, onLogin, onLogout }) {
  const navigate = useNavigate(); // 🚀 Declared router link operator
  const [showLogin, setShowLogin] = useState(false);
  const [dbStats, setDbStats] = useState(null);
  const [isTesting, setIsTesting] = useState(false);
  const [latestBenchmark, setLatestBenchmark] = useState(null);

  // 🚀 FIXED: Added the missing state definitions so they can be referenced below!
  const [destinations, setDestinations] = useState([]);
  const [selectedDestination, setSelectedDestination] = useState("");
  const [isLoadingDestinations, setIsLoadingDestinations] = useState(true);

  const handleRunTest = async () => {
    setIsTesting(true);
    try {
      // 🚀 FETCH REAL-TIME LATENCY TELEMETRY FROM BACKEND
      const res = await axios.get("http://localhost:8080/api/benchmarks/latest-run");
      const realData = res.data;

      // Extract raw execution speeds (handling potential string formats or object keys)
      const fTime = parseFloat(realData.firestoreWriteMs || realData.firestoreTime || 0);
      const sTime = parseFloat(realData.cloudSqlWriteMs || realData.sqlTime || 0);

      // 🏎️ DYNAMIC COMPUTATION: Determine execution win context on the fly
      const firestoreFaster = fTime < sTime;
      const calculatedMargin = Math.abs(sTime - fTime).toFixed(2);
      const calculatedWinner = firestoreFaster ? "Google Cloud Firestore" : "Google Cloud SQL";

      setDbStats({
        firestoreTime: `${fTime} ms`,
        sqlTime: `${sTime} ms`,
        winner: calculatedWinner,
        margin: `${calculatedMargin} ms`
      });

    } catch (err) {
      console.error("Critical Cloud telemetry retrieval dropped:", err);
      setDbStats({ 
        firestoreTime: "Error", 
        sqlTime: "Error", 
        winner: "None (Check Backend Console)",
        margin: "0.00 ms"
      });
    } finally {
      setIsTesting(false);
    }
  };

  useEffect(() => {
    axios.get("http://localhost:8080/api/benchmarks/latest-run")
      .then((response) => {
        setLatestBenchmark(response.data); 
      })
      .catch((error) => {
        console.error("Benchmark telemetry retrieval dropped:", error);
        setLatestBenchmark({
          firestoreWriteMs: 0,
          cloudSqlWriteMs: 0,
          executionSummary: "No active benchmark profiles loaded from multi-cloud log records."
        });
      });
  }, []);

  // Fetching dynamic dataset collections from your Spring Boot controller proxy
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/travel/trips/countries/list")
      .then((res) => {
        setDestinations(res.data || []);
      })
      .catch((err) => console.error("Failed loading home landing dropdown choices:", err))
      .finally(() => setIsLoadingDestinations(false));
  }, []);

  // 🚀 Handle submitting the form from the Hero search widget container
  const handleSearchSubmit = (e) => {
    if (e) e.preventDefault();
    if (!selectedDestination) return;

    // Forward the selection target straight over to the trips routing context pipeline
    navigate("/trips", { state: { autoSelectCountry: selectedDestination } });
  };

  return (
    <div className="min-h-screen bg-slate-50 pt-16">
      {/* 🚀 EXTENDED: Passing state values down to the Hero banner configuration layer */}
      <Hero 
        isLoggedIn={isLoggedIn} 
        onLoginClick={onLogin} 
        destinations={destinations}
        selectedDestination={selectedDestination}
        setSelectedDestination={setSelectedDestination}
        isLoadingDestinations={isLoadingDestinations}
        onSearchSubmit={handleSearchSubmit}
      />
      
      <DestinationCards />

      {/* Main Feature Cards Section */}
      <DatabaseComparison onCompareClick={handleRunTest} isTesting={isTesting} />

      {/* The Dashboard Performance Metric Display UI */}
      {dbStats && (
        <div className="max-w-7xl mx-auto px-6 pb-16 animate-fade-in">
          <div className="bg-emerald-50 border border-emerald-200 rounded-2xl p-4 mb-8 text-center text-sm font-medium text-emerald-800">
            🎉 Performance Insight: <span className="font-bold">{dbStats.winner}</span> executed the identical operations data set <span className="underline font-bold">{dbStats.margin || "234.34 ms"}</span> faster!
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-bold text-lg text-slate-900">Google Cloud Firestore</h3>
                <span className="text-xs font-semibold px-2.5 py-1 bg-amber-100 text-amber-800 rounded-lg">NoSQL Document</span>
              </div>
              <div className="space-y-4">
                <div>
                  <div className="flex justify-between text-xs text-slate-500 mb-1">
                    <span>Execution Latency</span>
                    <span className="font-mono font-bold text-emerald-600">{dbStats.firestoreTime}</span>
                  </div>
                  <div className="w-full bg-slate-100 h-3 rounded-full overflow-hidden">
                    <div className="bg-emerald-500 h-3 rounded-full transition-all duration-500" style={{ width: '35%' }}></div>
                  </div>
                </div>
                <div className="text-xs text-slate-500 bg-slate-50 p-3 rounded-xl">
                  ✔️ Billed strictly per operation (Reads/Writes/Deletes)<br />
                  ✔️ Highly scale-to-zero operational efficiency
                </div>
              </div>
            </div>

            <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-bold text-lg text-slate-900">Google Cloud SQL</h3>
                <span className="text-xs font-semibold px-2.5 py-1 bg-blue-100 text-blue-800 rounded-lg">Relational MySQL</span>
              </div>
              <div className="space-y-4">
                <div>
                  <div className="flex justify-between text-xs text-slate-500 mb-1">
                    <span>Execution Latency</span>
                    <span className="font-mono font-bold text-blue-600">{dbStats.sqlTime}</span>
                  </div>
                  <div className="w-full bg-slate-100 h-3 rounded-full overflow-hidden">
                    <div className="bg-blue-500 h-3 rounded-full transition-all duration-500" style={{ width: '75%' }}></div>
                  </div>
                </div>
                <div className="text-xs text-slate-500 bg-slate-50 p-3 rounded-xl">
                  ✔️ Full ACID Transaction Integrity compliance<br />
                  ✔️ Exceptional performance for complex multi-table JOINs
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;