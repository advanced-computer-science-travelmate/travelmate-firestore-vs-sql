import axios from "axios";

export const destinationService = {
  getEuropeanDestinations: async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/destinations/europe"
      );

      const countries = response.data.data.objects;

      return countries
        .map((country, index) => ({
          id: index + 1,
          name: country.names.common,
          image: country.flag.url_png,
          description: `Explore beautiful destinations in ${country.names.common}.`,
          overview: `Discover the culture, cities, nature, and travel experiences of ${country.names.common}.`,
          famousCities: country.capitals?.map((capital) => capital.name) || [
            "Popular cities",
          ],
          famousPlaces: [
            "Historic landmarks",
            "Local attractions",
            "Nature spots",
          ],
        }))
        .sort((a, b) => a.name.localeCompare(b.name));
    } catch (error) {
      console.error("Failed to load European destinations:", error);
      return [];
    }
  },
};