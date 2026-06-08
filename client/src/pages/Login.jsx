function Login() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-sky-100 flex items-center justify-center px-6">
      <div className="w-full max-w-5xl bg-white rounded-3xl shadow-xl overflow-hidden grid md:grid-cols-2">
        
        <div className="hidden md:flex flex-col justify-between bg-blue-600 p-10 text-white">
          <div>
            <h1 className="text-3xl font-bold">
              Travel<span className="text-blue-200">Mate</span>
            </h1>
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
              Create your account and organize all your travel plans in one
              cloud-based platform.
            </p>
          </div>
        </div>

        <div className="p-8 md:p-12">
          <h2 className="text-3xl font-bold text-slate-900">
            Welcome back
          </h2>
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

            <div className="flex items-center justify-between text-sm">
              <label className="flex items-center gap-2 text-slate-600">
                <input type="checkbox" />
                Remember me
              </label>

              <button type="button" className="text-blue-600 font-semibold">
                Forgot password?
              </button>
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
            <button className="text-blue-600 font-semibold">
              Sign up
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;