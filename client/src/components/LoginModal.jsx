import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import { authService } from "../services/authService";

function LoginModal({ onClose, onLoginSuccess, onResponseClose }) {

  const [isSignup, setIsSignup] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState("");
  const [isError, setIsError] = useState(false);

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  }

  async function handleSubmit(event) { // Added missing async keyword
    event.preventDefault();

    setLoading(true);
    setError(""); // Reset error banners on form submit
    setSuccess(""); // Reset success metrics
    setStatusMessage("");
    setIsError(false);

    // --- Validation Rules using formData ---
    if (!formData.email || !formData.password) {
      setError("Please enter your email and password.");
      setLoading(false);
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(formData.email)) {
      setError("Please enter a valid email address.");
      setLoading(false);
      return;
    }

    if (isSignup && !formData.fullName) {
      setError("Please enter your full name.");
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters.");
      setLoading(false);
      return;
    }

    if (isSignup && formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      setLoading(false);
      return;
    }

    try {
      const submissionName = formData.fullName || "Active User";
      const data = await authService.login(submissionName, formData.email);
      
      setIsError(false);
      // Update your visual success text dynamically with actual database tracker keys
      setSuccess(`Database Sync Complete! Account Mapped: (SQL ID: ${data.sqlId} | NoSQL ID: ${data.noSqlId})`);
      
      localStorage.setItem("userSession", JSON.stringify(data));
      
      // Execute pass-down success lifecycle triggers
      if (onLoginSuccess) onLoginSuccess(data);

      setTimeout(() => {
        if (onResponseClose) onResponseClose();
        else if (onClose) onClose();
      }, 1500);

    } catch (error) {
      console.error("Authentication integration mismatch:", error);
      setError("Cross-Engine connection failed. Ensure Spring Boot is running on port 8080.");
    } finally {
      setLoading(false);
    }
  }

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
              {isSignup
                ? "Create your travel planning account."
                : "Start planning your next adventure."}
            </h2>
            <p className="mt-4 text-blue-100">
              Organize destinations, budgets, and itineraries in one clean
              cloud-based platform.
            </p>
          </div>
        </div>

        <div className="p-8 md:p-12">
          <h2 className="text-3xl font-bold text-slate-900">
            {isSignup ? "Create account" : "Welcome back"}
          </h2>

          <p className="text-slate-500 mt-2">
            {isSignup
              ? "Sign up to start planning your trips with TravelMate."
              : "Login to continue to your TravelMate dashboard."}
          </p>

          {error && (
            <div className="mt-5 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-red-600 text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="mt-5 rounded-xl bg-green-50 border border-green-200 px-4 py-3 text-green-600 text-sm">
              {success}
            </div>
          )}

          {statusMessage && (
          <div className={`p-3 rounded-xl font-medium text-sm border ${
            isError 
              ? "bg-red-50 text-red-700 border-red-200" 
              : "bg-emerald-50 text-emerald-700 border-emerald-200"
          }`}>
            {statusMessage}
          </div>
          )}

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            {isSignup && (
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Full Name
                </label>
                <input
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                  type="text"
                  placeholder="Enter your name"
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            )}

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Email Address
              </label>
              <input
                name="email"
                value={formData.email}
                onChange={handleChange}
                type="email"
                placeholder="you@example.com"
                className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                Password
              </label>

              <div className="relative">
                <input
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter your password"
                  className="w-full px-4 py-3 pr-12 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500"
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            {isSignup && (
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Confirm Password
                </label>

                <div className="relative">
                  <input
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    type={showConfirmPassword ? "text" : "password"}
                    placeholder="Confirm your password"
                    className="w-full px-4 py-3 pr-12 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />

                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500"
                  >
                    {showConfirmPassword ? (
                      <EyeOff size={20} />
                    ) : (
                      <Eye size={20} />
                    )}
                  </button>
                </div>
              </div>
            )}

            {!isSignup && (
              <div className="flex items-center justify-between text-sm">
                <label className="flex items-center gap-2 text-slate-600">
                  <input type="checkbox" />
                  Remember me
                </label>

                <button type="button" className="text-blue-600 font-semibold">
                  Forgot password?
                </button>
              </div>
            )}

            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition"
            >
              {isSignup ? "Create Account" : "Login"}
            </button>
          </form>

          <p className="text-center text-slate-500 mt-8">
            {isSignup ? "Already have an account?" : "Don’t have an account?"}{" "}
            <button
              onClick={() => {
                setIsSignup(!isSignup);
                setError("");
                setSuccess("");
              }}
              className="text-blue-600 font-semibold"
            >
              {isSignup ? "Login" : "Sign up"}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

export default LoginModal;
