import axios, {AxiosResponse} from 'axios';
import {Athlete, EnTurDepartureBoard, WeatherForecastData} from "./types";

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

export const fetchWeatherForcast = async (): Promise<WeatherForecastData | null> => {
    try {
        return await apiRequest('/api/weather/forecast', 'GET');
    } catch (error) {
        console.error('Failed to fetch weather from backend', error);
        return null
    }
}

export const fetchStravaScoreboard = async (): Promise<Athlete[] | null> => {
    try {
        return await apiRequest('/api/strava/scoreboard?mock=true', 'GET');
    } catch (error) {
        console.error('Failed to fetch Strava scoreboard from backend', error);
        return null;
    }
}

export const fetchPublicTransportDepartureBoard = async (stopPlaceId: string = 'NSR:StopPlace:6006', quayId: string = 'NSR:Quay:10985'): Promise<EnTurDepartureBoard | null> => {
    try {
        return await apiRequest(`/api/public-transport/departure-board?stopPlaceId=${stopPlaceId}&quayId=${quayId}`, 'GET');
    } catch (error) {
        console.error('Failed to fetch departure board from backend', error);
        return null;
    }
}
