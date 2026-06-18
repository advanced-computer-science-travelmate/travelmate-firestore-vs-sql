import axios from "axios";

export const destinationService = {
  getEuropeanDestinations: async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/travel/destinations/europe"
      );

      const countries = response.data;

      if (!Array.isArray(countries)) {
        console.error("Expected an array from live API endpoint, got:", countries);
        return [];
      }

      // 🚀 THE LIVE PARSING MATRIX: Map down to the exact REST Countries API keys
      return countries
        .map((country, index) => {
          const countryName = country.name?.common || "Unknown Country";
          const flagImage = country.flags?.png || "https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=500";

          return {
            id: index + 1, // Generate stable IDs for React keys
            name: countryName,
            image: flagImage,
            description: `Explore beautiful destinations in ${countryName}.`,
            overview: `Discover the culture, cities, nature, and travel experiences of ${countryName}.`,
            // .capital is returned by the API as an array of plain strings (e.g., ["Paris"])
            famousCities: country.capital && country.capital.length > 0 
              ? country.capital 
              : ["Popular cities"],
            famousPlaces: [
              "Historic landmarks",
              "Local attractions",
              "Nature spots"
            ],
          };
        })
        .sort((a, b) => a.name.localeCompare(b.name)); // Keep it alphabetical
    } catch (error) {
      console.error("Failed to load live European destinations:", error);
      return [];
    }
  },
};