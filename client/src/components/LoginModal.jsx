import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import axios from "axios"; // Added Axios import for API orchestration

function LoginModal({ onClose, onLoginSuccess }) {
  const [isSignup, setIsSignup] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false); // Added to prevent multiple double-clicks

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

  function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setSuccess("");

    // --- Validation Rules Block ---
    if (!formData.email || !formData.password) {
      setError("Please enter your email and password.");
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(formData.email)) {
      setError("Please enter a valid email address.");
      return;
    }

    if (isSignup && !formData.fullName) {
      setError("Please enter your full name.");
      return;
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    if (isSignup && formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    // --- Live Ingestion Integration Phase ---
    setIsSubmitting(true);

    // If logging in, create a generic display name out of the email username fragment
    const derivedName = isSignup 
      ? formData.fullName 
      : formData.email.split("@")[0].charAt(0).toUpperCase() + formData.email.split("@")[0].slice(1);

    // Target your dynamic Spring Boot mapping endpoint
    axios.post("http://localhost:8080/api/users/mock-login", {
      name: derivedName,
      email: formData.email
    })
    .then(response => {
      const sessionData = response.data;
      
      // Store synchronized cloud identities into browser local storage cache
      localStorage.setItem("user_sql_id", sessionData.sqlId);
      localStorage.setItem("user_nosql_id", sessionData.noSqlId);
      localStorage.setItem("user_name", sessionData.name);
      localStorage.setItem("user_email", sessionData.email);

      setSuccess(isSignup ? "Account verified and synced!" : "Login successful! Synced.");
      
      // Delayed execute so user sees the green success alert block before it closes
      setTimeout(() => {
        setIsSubmitting(false);
        if (onLoginSuccess) onLoginSuccess(sessionData); 
        onClose();
      }, 1200);
    })
    .catch(err => {
      console.error("Multi-Cloud connection failed:", err);
      setError("Database connection failed. Is your Cloud SQL instance turned on?");
      setIsSubmitting(false);
    });
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm px-6">
      <div className="relative w-full max-w-4xl bg-white rounded-3xl shadow-2xl overflow-hidden grid md:grid-cols-2">
        <button
          onClick={onClose}
          disabled={isSubmitting}
          className="absolute top-4 right-4 h-10 w-10 rounded-full bg-white shadow flex items-center justify-center text-slate-700 hover:bg-slate-100 disabled:opacity-50"
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
                  disabled={isSubmitting}
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-slate-50"
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
                disabled={isSubmitting}
                className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-slate-50"
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
                  disabled={isSubmitting}
                  className="w-full px-4 py-3 pr-12 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-slate-50"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={isSubmitting}
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
                    disabled={isSubmitting}
                    className="w-full px-4 py-3 pr-12 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-slate-50"
                  />

                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    disabled={isSubmitting}
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
                  <input type="checkbox" disabled={isSubmitting} />
                  Remember me
                </label>

                <button type="button" disabled={isSubmitting} className="text-blue-600 font-semibold disabled:opacity-50">
                  Forgot password?
                </button>
              </div>
            )}

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition disabled:bg-blue-400 flex items-center justify-center"
            >
              {isSubmitting ? "Connecting to Multi-Cloud Clusters..." : (isSignup ? "Create Account" : "Login")}
            </button>
          </form>

          <p className="text-center text-slate-500 mt-8">
            {isSignup ? "Already have an account?" : "Don’t have an account?"}{" "}
            <button
              onClick={() => {
                if (isSubmitting) return;
                setIsSignup(!isSignup);
                setError("");
                setSuccess("");
              }}
              disabled={isSubmitting}
              className="text-blue-600 font-semibold disabled:opacity-50"
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