import axios, {AxiosResponse} from 'axios';
import {Athlete, WeatherForecast} from "./types";

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    }
});

const apiRequest = async <T>(url: string, method: 'GET' | 'POST' | 'PUT' | 'DELETE', data?: any): Promise<T> => {
    const response: AxiosResponse<T> = await apiClient({
        method,
        url,
        data,
    });

    return response.data;
};

export const fetchWeatherForcast = async (): Promise<WeatherForecast | null> => {
    try {
        return await apiRequest('/api/weather/forecast', 'GET');
    } catch (error) {
        console.error('Failed to fetch weather from backend', error);
        return null
    }
}

export const fetchStravaScoreboard = async (): Promise<Athlete[] | null> => {
    try {
        return await apiRequest('/api/strava/scoreboard', 'GET');
    } catch (error) {
        console.error('Failed to fetch Strava scoreboard from backend', error);
        return null;
    }
}