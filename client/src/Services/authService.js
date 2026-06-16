import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/users';

export const authService = {
  login: async (name, email) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/mock-login`, { name, email });
      // Returns: { sqlId, noSqlId, name, email }
      return response.data;
    } catch (error) {
      console.error("Authentication handshake failed:", error);
      throw error;
    }
  }
};