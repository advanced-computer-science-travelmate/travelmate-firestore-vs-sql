function DatabaseComparison({ onCompareClick, isTesting }) {
  return (
    <section className="max-w-7xl mx-auto px-6 py-16">
      <div className="bg-gradient-to-br from-blue-50 to-slate-100 rounded-3xl p-8 md:p-12">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6 mb-10">
          <div>
            <h2 className="text-3xl font-bold text-slate-900">
              Firestore vs Cloud SQL
            </h2>
            <p className="text-slate-600 mt-3 max-w-2xl">
              TravelMate compares two Google Cloud databases by storing and
              retrieving travel data such as trips, itineraries, expenses,
              comments, and notifications.
            </p>
          </div>

          <button
            onClick={onCompareClick}
            disabled={isTesting}
            className="bg-blue-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition disabled:opacity-50 flex items-center gap-2"
          >
            {isTesting ? (
              <>
                <span className="animate-spin inline-block w-4 h-4 border-2 border-white border-t-transparent rounded-full"></span>
                Running Live Benchmarks...
              </>
            ) : (
              "Compare Now"
            )}
          </button>
        </div>

        <div className="grid md:grid-cols-3 gap-6 items-center">
          <div className="bg-white rounded-2xl p-6 shadow-sm">
            <div className="text-4xl mb-4">🔥</div>
            <h3 className="text-xl font-bold text-slate-900">
              Cloud Firestore
            </h3>
            <ul className="mt-4 space-y-2 text-slate-600">
              <li>✓ NoSQL document database</li>
              <li>✓ Real-time synchronization</li>
              <li>✓ Flexible data structure</li>
              <li>✓ Best for comments and notifications</li>
            </ul>
          </div>

          <div className="flex justify-center">
            <div className="h-16 w-16 rounded-full bg-white shadow-md flex items-center justify-center font-bold text-slate-900">
              VS
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm">
            <div className="text-4xl mb-4">🗄️</div>
            <h3 className="text-xl font-bold text-slate-900">
              Cloud SQL
            </h3>
            <ul className="mt-4 space-y-2 text-slate-600">
              <li>✓ Relational SQL database</li>
              <li>✓ Structured tables</li>
              <li>✓ Complex queries and reports</li>
              <li>✓ Best for trips, itineraries, and expenses</li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}

export default DatabaseComparison;