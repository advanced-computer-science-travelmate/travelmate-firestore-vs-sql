import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import { authService } from "../services/authService";
import axios from "axios";

function LoginModal({ onClose, onLoginSuccess, onResponseClose }) {

  const [isSignup, setIsSignup] = useState(false);
  const [emailId, setEmailId] = useState("");
  const [password, setPassword] = useState("");
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

  async function handleSubmit(event) {
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

    // ==========================================
    // 🚀 PATH A: USER SIGNUP (Saves Typed Name)
    // ==========================================
    if (isSignup) {
      // You can point this to your backend user creation endpoint if you have one later, 
      // but for now, it simulates a successful signup using their real typed name!
      setTimeout(() => {
        const mockSignupData = {
          email: formData.email,
          name: formData.fullName, // 👤 STORES YOUR REAL TYPED NAME
          sqlId: "18",
          noSqlId: "USER-18"
        };

        localStorage.setItem("userSession", JSON.stringify(mockSignupData));
        localStorage.setItem("user_name", formData.fullName); 
        localStorage.setItem("user_email", formData.email);
        localStorage.setItem("user_nosql_id", "USER-18");
        localStorage.setItem("user_sql_id", "18");

        setSuccess("Account Registered Successfully!");

        if (typeof onLoginSuccess === "function") {
          onLoginSuccess(mockSignupData);
        } else {
          window.location.reload();
        }

        setTimeout(() => {
          if (onClose) onClose();
        }, 1500);
        setLoading(false);
      }, 800);
      
      return; // Stop execution here so signups don't drop into mock-login below
    }

    // ==========================================
    // 🚀 PATH B: USER LOGIN (Mock Path)
    // ==========================================
    axios.post("http://localhost:8080/api/users/mock-login", { 
      email: formData.email,     // 👈 Fix here
      password: formData.password // 👈 Fix here
    })
    .then((response) => {
      const userData = response.data;
      console.log("🔥 THE ACTUAL USERDATA FROM BACKEND IS:", userData);
      
      localStorage.setItem("userSession", JSON.stringify(userData));

      // Dynamic email prefix extraction if mock endpoint returns "Active User"
      const extractedUsername = userData.email ? userData.email.split('@')[0] : "Active User";

      localStorage.setItem("name", userData.name);
      localStorage.setItem("user_email", userData.email || formData.email);
      localStorage.setItem("user_nosql_id", userData.noSqlId);
      localStorage.setItem("user_sql_id", userData.sqlId);

      if (typeof onLoginSuccess === "function") {
        onLoginSuccess(userData);
      } else {
        window.location.reload();
      }

      setTimeout(() => {
        if (onResponseClose) onResponseClose();
        else if (onClose) onClose();
      }, 1500);
    })
    .catch((err) => {
      console.error("Mock login connection failed:", err);
      setError("Mock login failed. Check backend console outputs.");
    })
    .finally(() => {
      setLoading(false);
    });
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
