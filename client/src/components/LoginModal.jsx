function LoginModal({ onClose }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm px-6">
      <div className="relative w-full max-w-4xl bg-white rounded-3xl shadow-2xl overflow-hidden grid md:grid-cols-2">
        
        <button
          onClick={onClose}
          className="absolute top-4 right-4 h-10 w-10 rounded-full bg-white shadow flex items-center justify-center text-slate-700 hover:bg-slate-100"
        >
          ✕
        </button>

        <div className="hidden md:flex flex-col justify-between bg-blue-600 p-10 text-white">
          <div>
            <h1 className="text-3xl font-bold">TravelMate</h1>
            <p className="mt-4 text-blue-100">
              Plan trips, manage itineraries, track expenses, and compare
              Firestore with Cloud SQL.
            </p>
          </div>

          <div>
            <h2 className="text-4xl font-bold leading-tight">
              Start planning your next adventure.
            </h2>
            <p className="mt-4 text-blue-100">
              Organize your trips in one cloud-based platform.
            </p>
          </div>
        </div>

        <div className="p-8 md:p-12">
          <h2 className="text-3xl font-bold text-slate-900">Welcome back</h2>
          <p className="text-slate-500 mt-2">
            Login to continue to your TravelMate dashboard.
          </p>

          <form className="mt-8 space-y-5">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Email Address
              </label>
              <input
                type="email"
                placeholder="you@example.com"
                className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Password
              </label>
              <input
                type="password"
                placeholder="Enter your password"
                className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
            >
              Login
            </button>
          </form>

          <p className="text-center text-slate-500 mt-8">
            Don&apos;t have an account?{" "}
            <button className="text-blue-600 font-semibold">Sign up</button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default LoginModal;