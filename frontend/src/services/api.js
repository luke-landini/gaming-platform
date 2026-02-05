import axios from 'axios';
import keycloak from './keycloak';

const USER_SERVICE_URL = 'http://localhost:8081/api/v1';
const CATALOG_SERVICE_URL = 'http://localhost:8082/api';

// Create axios instance for User Service
const userApiInstance = axios.create({
  baseURL: USER_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create axios instance for Catalog Service
const catalogApiInstance = axios.create({
  baseURL: CATALOG_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const setupInterceptors = (instance) => {
  // Interceptor to add JWT token to requests
  instance.interceptors.request.use(
    (config) => {
      if (keycloak.token) {
        config.headers.Authorization = `Bearer ${keycloak.token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Interceptor to handle responses and errors
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response?.status === 401) {
        // Token expired, try to refresh
        try {
          await keycloak.updateToken(30);
        } catch (refreshError) {
          keycloak.login();
        }
      }
      return Promise.reject(error);
    }
  );
};

setupInterceptors(userApiInstance);
setupInterceptors(catalogApiInstance);

// User API endpoints
export const userApi = {
  // Get current user profile
  getCurrentUser: async () => {
    const response = await userApiInstance.get('/users/me');
    return response.data;
  },
};

// Catalog API endpoints
export const catalogApi = {
  getGames: async (params) => {
    const response = await catalogApiInstance.get('/games', { params });
    return response.data;
  },
  getGameById: async (id) => {
    const response = await catalogApiInstance.get(`/games/${id}`);
    return response.data;
  },
  searchGames: async (params) => {
    const response = await catalogApiInstance.get('/games/search', { params });
    return response.data;
  },
  createGame: async (gameData) => {
    const response = await catalogApiInstance.post('/games', gameData);
    return response.data;
  },
  updateGame: async (id, gameData) => {
    const response = await catalogApiInstance.put(`/games/${id}`, gameData);
    return response.data;
  },
  deleteGame: async (id) => {
    await catalogApiInstance.delete(`/games/${id}`);
  },
};

export default { userApi, catalogApi };
