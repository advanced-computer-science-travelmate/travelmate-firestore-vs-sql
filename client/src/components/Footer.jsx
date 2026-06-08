function Footer() {
  return (
    <footer className="bg-slate-900 text-white mt-20">
      <div className="max-w-7xl mx-auto px-6 py-16">

        <div className="grid md:grid-cols-4 gap-10">

          <div>
            <h2 className="text-2xl font-bold text-blue-400">
              TravelMate
            </h2>

            <p className="text-slate-400 mt-4">
              Your all-in-one travel companion for planning trips,
              comparing databases, and creating unforgettable journeys.
            </p>
          </div>

          <div>
            <h3 className="font-semibold mb-4">
              Company
            </h3>

            <ul className="space-y-2 text-slate-400">
              <li>About Us</li>
              <li>Careers</li>
              <li>Blog</li>
              <li>Contact</li>
            </ul>
          </div>

          <div>
            <h3 className="font-semibold mb-4">
              Support
            </h3>

            <ul className="space-y-2 text-slate-400">
              <li>Help Center</li>
              <li>FAQs</li>
              <li>Privacy Policy</li>
              <li>Terms of Service</li>
            </ul>
          </div>

          <div>
            <h3 className="font-semibold mb-4">
              Newsletter
            </h3>

            <div className="flex">
              <input
                type="email"
                placeholder="Enter your email"
                className="flex-1 px-4 py-3 rounded-l-xl text-black"
              />

              <button className="bg-blue-600 px-4 rounded-r-xl hover:bg-blue-700">
                Subscribe
              </button>
            </div>
          </div>

        </div>

        <div className="border-t border-slate-700 mt-10 pt-6 text-center text-slate-500">
          © 2026 TravelMate. All rights reserved.
        </div>

      </div>
    </footer>
  )
}

export default Footer