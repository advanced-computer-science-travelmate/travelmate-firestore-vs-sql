import React, { useState } from 'react';
import axios from 'axios';
import Navbar from '../components/Navbar';

function CompareDB() {
  const [loading, setLoading] = useState(false);
  const [metrics, setMetrics] = useState(null);

  const runBenchmark = async () => {
    setLoading(true);
    setMetrics(null);

    try {
      // --- BENCHMARK 1: GOOGLE CLOUD SQL (MySQL) ---
      const sqlStart = performance.now();
      const sqlResponse = await axios.get('http://localhost:8080/api/travel/destinations/sql');
      const sqlEnd = performance.now();
      const sqlTime = sqlEnd - sqlStart;

      // --- BENCHMARK 2: GOOGLE CLOUD FIRESTORE (NoSQL) ---
      const noSqlStart = performance.now();
      const noSqlResponse = await axios.get('http://localhost:8080/api/travel/destinations/firestore');
      const noSqlEnd = performance.now();
      const noSqlTime = noSqlEnd - noSqlStart;

      // --- SET VISUAL METRICS STATE ---
      setMetrics({
        sql: {
          time: sqlTime.toFixed(2),
          count: sqlResponse.data.length,
          status: 'SUCCESS'
        },
        firestore: {
          time: noSqlTime.toFixed(2),
          count: noSqlResponse.data.length,
          status: 'SUCCESS'
        },
        winner: sqlTime < noSqlTime ? 'Cloud SQL' : 'Cloud Firestore',
        diff: Math.abs(sqlTime - noSqlTime).toFixed(2)
      });

    } catch (error) {
      console.error("Error executing dual-cloud database benchmarking benchmarks:", error);
      alert("Failed to securely pull records from hybrid backend pathways. Ensure Spring Boot is running.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-5xl mx-auto">
          {/* Header Section */}
          <div className="flex flex-col md:flex-row md:items-center md:justify-between border-b border-slate-200 pb-6">
            <div>
              <h1 className="text-4xl font-bold text-slate-900 tracking-tight">
                Compare Databases
              </h1>
              <p className="text-slate-600 mt-2">
                Evaluate live transactional read latency between serverless document collections and managed relational storage.
              </p>
            </div>
            <button
              onClick={runBenchmark}
              disabled={loading}
              className={`mt-4 md:mt-0 px-6 py-3 font-semibold rounded-lg text-white shadow-sm transition-all ${
                loading 
                  ? 'bg-blue-400 cursor-not-allowed' 
                  : 'bg-blue-600 hover:bg-blue-700 active:scale-95'
              }`}
            >
              {loading ? 'Running Benchmarks...' : '⚡ Run Performance Test'}
            </button>
          </div>

          {/* Loader Grid Placeholder */}
          {loading && (
            <div className="mt-12 text-center py-20 bg-white rounded-xl border border-slate-200 shadow-sm">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
              <p className="text-slate-500 mt-4 font-medium animate-pulse">Polling global hybrid endpoints dynamically...</p>
            </div>
          )}

          {/* Dashboard Metrics Layout */}
          {metrics && !loading && (
            <div className="mt-12 space-y-8 animate-fadeIn">
              
              {/* Verdict Banner */}
              <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-6 flex items-center justify-between shadow-sm">
                <div>
                  <h3 className="text-emerald-900 font-bold text-lg">Performance Insight</h3>
                  <p className="text-emerald-700 text-sm mt-1">
                    Under current workloads, <span className="font-bold uppercase tracking-wider">{metrics.winner}</span> processed the identical dataset <span className="font-bold">{metrics.diff} ms</span> faster.
                  </p>
                </div>
                <span className="text-3xl">🚀</span>
              </div>

              {/* Side-by-Side Comparison Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                
                {/* Firestore Component Visuals */}
                <div className="bg-white p-8 rounded-xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                  <div className="flex items-center justify-between">
                    <h2 className="text-xl font-bold text-slate-800">Google Cloud Firestore</h2>
                    <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-orange-100 text-orange-800">NoSQL Document</span>
                  </div>
                  <div className="mt-6 space-y-4">
                    <div className="flex justify-between border-b border-slate-100 pb-2">
                      <span className="text-slate-500 text-sm">Execution Latency</span>
                      <span className="text-2xl font-black text-orange-600">{metrics.firestore.time} <small className="text-xs font-normal text-slate-400">ms</small></span>
                    </div>
                    <div className="flex justify-between border-b border-slate-100 pb-2">
                      <span className="text-slate-500 text-sm">Records Ingested</span>
                      <span className="font-semibold text-slate-700">{metrics.firestore.count} documents</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500">API Handshake Status</span>
                      <span className="text-emerald-600 font-bold flex items-center">● {metrics.firestore.status}</span>
                    </div>
                  </div>
                </div>

                {/* Cloud SQL Component Visuals */}
                <div className="bg-white p-8 rounded-xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                  <div className="flex items-center justify-between">
                    <h2 className="text-xl font-bold text-slate-800">Google Cloud SQL</h2>
                    <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-indigo-100 text-indigo-800">Relational MySQL</span>
                  </div>
                  <div className="mt-6 space-y-4">
                    <div className="flex justify-between border-b border-slate-100 pb-2">
                      <span className="text-slate-500 text-sm">Execution Latency</span>
                      <span className="text-2xl font-black text-indigo-600">{metrics.sql.time} <small className="text-xs font-normal text-slate-400">ms</small></span>
                    </div>
                    <div className="flex justify-between border-b border-slate-100 pb-2">
                      <span className="text-slate-500 text-sm">Records Ingested</span>
                      <span className="font-semibold text-slate-700">{metrics.sql.count} rows</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500">API Handshake Status</span>
                      <span className="text-emerald-600 font-bold flex items-center">● {metrics.sql.status}</span>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          )}
        </section>
      </main>
    </>
  );
}

export default CompareDB;