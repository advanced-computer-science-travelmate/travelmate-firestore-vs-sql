import Navbar from "../components/Navbar";

function Dashboard() {
  return (
    <div className="min-h-screen bg-slate-50">
          <Navbar isDashboard={true} />
      <div className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <p className="text-blue-600 font-semibold">TravelMate Dashboard</p>
          <h1 className="text-4xl font-bold text-slate-900 mt-2">
            Welcome back
          </h1>
          <p className="text-slate-500 mt-2">
            Manage your trips, budget, bookings, and group travel proposals.
          </p>
        </div>

        <div className="grid md:grid-cols-4 gap-6 mb-10">
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <p className="text-slate-500">My Trips</p>
            <h2 className="text-3xl font-bold text-slate-900 mt-2">4</h2>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <p className="text-slate-500">Budget Used</p>
            <h2 className="text-3xl font-bold text-slate-900 mt-2">€850</h2>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <p className="text-slate-500">Bookings</p>
            <h2 className="text-3xl font-bold text-slate-900 mt-2">6</h2>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <p className="text-slate-500">Pending Votes</p>
            <h2 className="text-3xl font-bold text-slate-900 mt-2">2</h2>
          </div>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 bg-white rounded-3xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-slate-900">
                Recent Itineraries
              </h2>

              <button className="bg-blue-600 text-white px-5 py-2 rounded-xl font-semibold hover:bg-blue-700 transition">
                Create Itinerary
              </button>
            </div>

            <div className="space-y-4">
              <div className="p-4 rounded-2xl border border-slate-100 flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-slate-900">Berlin Getaway</h3>
                  <p className="text-slate-500 text-sm">
                    Museum visit, food tour, city walk
                  </p>
                </div>
                <span className="text-sm font-semibold text-blue-600">
                  View
                </span>
              </div>

              <div className="p-4 rounded-2xl border border-slate-100 flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-slate-900">Paris Weekend</h3>
                  <p className="text-slate-500 text-sm">
                    Eiffel Tower, Louvre, Seine cruise
                  </p>
                </div>
                <span className="text-sm font-semibold text-blue-600">
                  View
                </span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100">
            <h2 className="text-2xl font-bold text-slate-900 mb-6">
              Quick Actions
            </h2>

            <div className="space-y-3">
              <button className="w-full text-left p-4 rounded-2xl bg-blue-50 text-blue-700 font-semibold">
                + Add Budget
              </button>
              <button className="w-full text-left p-4 rounded-2xl bg-green-50 text-green-700 font-semibold">
                + Add Booking
              </button>
              <button className="w-full text-left p-4 rounded-2xl bg-purple-50 text-purple-700 font-semibold">
                + Create Proposal
              </button>
              <button className="w-full text-left p-4 rounded-2xl bg-orange-50 text-orange-700 font-semibold">
                View Votes
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;