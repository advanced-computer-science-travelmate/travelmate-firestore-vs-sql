import Navbar from "../components/Navbar";

function Itineraries() {
  const itineraries = [
    {
      id: 1,
      destination: "Berlin Getaway",
      dates: "12 Jun - 15 Jun 2026",
      activities: ["Museum visit", "Food tour", "City walk"],
    },
    {
      id: 2,
      destination: "Paris Weekend",
      dates: "20 Jun - 23 Jun 2026",
      activities: ["Eiffel Tower", "Louvre Museum", "Seine cruise"],
    },
    {
      id: 3,
      destination: "Black Forest Hike",
      dates: "05 Jul - 07 Jul 2026",
      activities: ["Hiking", "Photography", "Local food"],
    },
  ];

  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar isDashboard={true} />

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <p className="text-blue-600 font-semibold">TravelMate</p>
            <h1 className="text-4xl font-bold text-slate-900 mt-2">
              My Itineraries
            </h1>
            <p className="text-slate-500 mt-2">
              Plan destinations, dates, activities, and group travel ideas.
            </p>
          </div>

          <button className="bg-blue-600 text-white px-5 py-3 rounded-xl font-semibold hover:bg-blue-700 transition">
            + Create Itinerary
          </button>
        </div>

        <div className="grid md:grid-cols-3 gap-6">
          {itineraries.map((itinerary) => (
            <div
              key={itinerary.id}
              className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100"
            >
              <h2 className="text-xl font-bold text-slate-900">
                {itinerary.destination}
              </h2>

              <p className="text-slate-500 text-sm mt-2">{itinerary.dates}</p>

              <div className="mt-5">
                <p className="text-sm font-semibold text-slate-700 mb-3">
                  Activities
                </p>

                <div className="space-y-2">
                  {itinerary.activities.map((activity, index) => (
                    <div
                      key={index}
                      className="bg-slate-50 rounded-xl px-4 py-2 text-sm text-slate-600"
                    >
                      {activity}
                    </div>
                  ))}
                </div>
              </div>

              <button className="mt-6 w-full border border-blue-600 text-blue-600 py-2 rounded-xl font-semibold hover:bg-blue-50 transition">
                View Details
              </button>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

export default Itineraries;