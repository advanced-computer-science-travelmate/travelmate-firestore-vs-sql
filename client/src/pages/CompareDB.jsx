import Navbar from "../components/Navbar";
import { useState, useEffect } from "react";
import axios from "axios";

function CompareDB({ isLoggedIn, onLogin, onLogout }) {
  const [sqlLatency, setSqlLatency] = useState(null);
  const [firestoreLatency, setFirestoreLatency] = useState(null);
  const [isComparing, setIsComparing] = useState(true);

  useEffect(() => {
    const runAutomaticBenchmark = async () => {
      try {
        // 1. Measure Cloud SQL (MySQL) Performance
        const sqlStart = performance.now();
        await axios.get("http://localhost:8080/api/travel/itineraries/sql");
        const sqlEnd = performance.now();
        setSqlLatency((sqlEnd - sqlStart).toFixed(2));

        // 2. Measure Cloud Firestore (NoSQL) Performance
        const firestoreStart = performance.now();
        await axios.get("http://localhost:8080/api/travel/itineraries/firestore");
        const firestoreEnd = performance.now();
        setFirestoreLatency((firestoreEnd - firestoreStart).toFixed(2));

        setIsComparing(false);
      } catch (err) {
        console.error("Benchmarking calculations failed:", err);
        setIsComparing(false);
      }
    };

    runAutomaticBenchmark();
  }, []);

  return (
    <>
      {/* Passing session states seamlessly down to your teammate's component */}
      <Navbar 
        isLoggedIn={isLoggedIn} 
        onLoginClick={onLogin} 
        onLogout={onLogout} 
      />

      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-7xl mx-auto">
          <h1 className="text-4xl font-bold text-slate-900">
            Compare Databases
          </h1>
          <p className="text-slate-600 mt-4">
            Compare Firestore and Cloud SQL performance for TravelMate dynamically.
          </p>

          {isComparing ? (
            <div className="mt-10 flex items-center gap-3 text-blue-600 font-semibold text-lg animate-pulse">
              <span>⚡</span> Querying production multi-cloud database engines...
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mt-10">
              {/* Cloud SQL Benchmark Panel */}
              <div className="p-8 bg-white rounded-3xl border border-slate-200 shadow-sm">
                <div className="flex items-center gap-3">
                  <div className="h-3 w-3 rounded-full bg-purple-600"></div>
                  <h3 className="text-xl font-bold text-slate-800">Google Cloud SQL</h3>
                </div>
                <p className="text-slate-500 text-sm mt-1">Relational Database Core (MySQL)</p>
                <div className="mt-6 flex items-baseline gap-2">
                  <span className="text-5xl font-black text-purple-700">{sqlLatency}</span>
                  <span className="text-slate-500 font-semibold text-sm">ms</span>
                </div>
                <p className="text-xs text-slate-400 mt-4">
                  Measured via standard JPA Repository queries routed to active cloud instances.
                </p>
              </div>

              {/* Firestore Benchmark Panel */}
              <div className="p-8 bg-white rounded-3xl border border-slate-200 shadow-sm">
                <div className="flex items-center gap-3">
                  <div className="h-3 w-3 rounded-full bg-orange-500"></div>
                  <h3 className="text-xl font-bold text-slate-800">Google Cloud Firestore</h3>
                </div>
                <p className="text-slate-500 text-sm mt-1">Serverless Document Store (NoSQL)</p>
                <div className="mt-6 flex items-baseline gap-2">
                  <span className="text-5xl font-black text-orange-600">{firestoreLatency}</span>
                  <span className="text-slate-500 font-semibold text-sm">ms</span>
                </div>
                <p className="text-xs text-slate-400 mt-4">
                  Measured via continuous asynchronous document snapshots mapped from cloud collection paths.
                </p>
              </div>
            </div>
          )}
        </section>
      </main>
    </>
  );
}

export default CompareDB;