import axios from "axios";

export const destinationService = {
  getEuropeanDestinations: async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/travel/destinations/europe"
      );

      const countries = response.data;
      console.log("➡️ RAW BACKEND DATA ARRIVED:", countries); // 🔍 DEBUG LOG 1

      if (!Array.isArray(countries)) {
        console.error("Expected an array from live API endpoint, got:", countries);
        return [];
      }

      const flagMap = {
        "austria": "at",
        "belgium": "be",
        "france": "fr",
        "germany": "de",
        "italy": "it",
        "netherlands": "nl",
        "portugal": "pt",
        "spain": "es",
        "sweden": "se",
        "switzerland": "ch"
      };

      const mappedData = countries.map((country, index) => {
        // Fallback checks for string variants or nested object structures
        let countryName = country.name?.common || country.name || "Unknown Country";
        
        // 🧼 SANITIZE STRING: Remove any accidental trailing spaces or hidden characters
        countryName = String(countryName).trim();
        const normalizedName = countryName.toLowerCase();

        // Check if the lookup table contains the sanitized string
        let countryCode = flagMap[normalizedName];

        // 🧠 FUZZY MATCHING FALLBACK: If an exact string match fails, check for partial inclusions
        if (!countryCode) {
          const matchedKey = Object.keys(flagMap).find(key => normalizedName.includes(key) || key.includes(normalizedName));
          if (matchedKey) countryCode = flagMap[matchedKey];
        }

        // Final code resolution or default to European Union placeholder flag code
        countryCode = countryCode || (country.countryCode || country.cca2 || "eu").toLowerCase();

        const flagImage = `https://flagcdn.com/w320/${countryCode}.png`;
        
        console.log(`Mapping -> Name: "${countryName}" | Matched Code: "${countryCode}" | Image: ${flagImage}`); // 🔍 DEBUG LOG 2

        return {
          id: country.id || index + 1,
          name: countryName,
          image: flagImage,
          countryCode: countryCode.toUpperCase(),
          description: `Explore beautiful destinations in ${countryName}.`,
          overview: `Discover the culture, cities, nature, and travel experiences of ${countryName}.`,
          famousCities: country.capital && country.capital.length > 0 
            ? (Array.isArray(country.capital) ? country.capital : [country.capital]) 
            : ["Popular cities"],
          famousPlaces: [
            "Historic landmarks",
            "Local attractions",
            "Nature spots"
          ],
        };
      });

      return mappedData.sort((a, b) => a.name.localeCompare(b.name));
    } catch (error) {
      console.error("Failed to load live European destinations:", error);
      return [];
    }
  },
};