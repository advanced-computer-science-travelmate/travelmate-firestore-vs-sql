import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/travel/destinations';

export const destinationService = {
    // FIX 1: Update the method feeding the Home, Destinations, and Trips dropdowns
    getEuropeanDestinations: async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/europe`);
            return response.data; // Now safely receives famousCities and famousPlaces
        } catch (error) {
            console.error("Error fetching European destinations:", error);
            throw error;
        }
    },

    // FIX 2: Update the method feeding the "Compare DB" page
    getFirestoreDestinations: async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/firestore`);
            return response.data;
        } catch (error) {
            console.error("Error fetching Firestore destinations:", error);
            throw error;
        }
    },

    // FIX 3: Add the hotel booking sub-type lookup method
    getHotelsByLocation: async (location) => {
        try {
            const response = await axios.get(`${API_BASE_URL}/hotels`, {
                params: { location }
            });
            return response.data;
        } catch (error) {
            console.error(`Error fetching hotels for ${location}:`, error);
            throw error;
        }
    }
};