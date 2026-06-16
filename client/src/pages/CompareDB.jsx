import Navbar from "../components/Navbar";

function CompareDB() {
  return (
    <>
      <Navbar />

      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <section className="max-w-7xl mx-auto">
          <h1 className="text-4xl font-bold text-slate-900">
            Compare Databases
          </h1>
          <p className="text-slate-600 mt-4">
            Compare Firestore and Cloud SQL performance for TravelMate.
          </p>
        </section>
      </main>
    </>
  );
}

export default CompareDB;