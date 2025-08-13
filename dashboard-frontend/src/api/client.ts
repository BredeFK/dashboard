import axios, {AxiosResponse} from 'axios';
import {Athlete, Coordinates, EnTurDepartureBoard, WeatherForecastData} from "./types";

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_BACKEND_BASE_URL || 'http://localhost:8080',
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

export const fetchWeatherForcast = async (
    coordinates: Coordinates = {latitude: 59.913, longitude: 10.738}, // Oslo
): Promise<WeatherForecastData | null> => {
    try {
        return await apiRequest(
            `/api/weather/forecast?latitude=${coordinates.latitude}&longitude=${coordinates.longitude}`,
            'GET'
        );
    } catch (error) {
        console.error('Failed to fetch weather from backend', error);
        return null
    }
}

export const fetchStravaScoreboard = async (mock: boolean = false): Promise<Athlete[] | null> => {
    try {
        return await apiRequest(`/api/strava/scoreboard?mock=${mock}`, 'GET');
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
