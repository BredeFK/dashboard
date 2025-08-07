interface WeatherInstance {
    timestamp: string;
    temperature: number;
    humidity: number;
    windSpeed: number;
    symbolCode?: string;
    symbolUrl?: string;
    precipitationAmount?: number;
    uvIndexClearSky?: number;
}

export interface WeatherForecast {
    lastUpdated: string;
    weatherSeries: WeatherInstance[];
}

export interface Athlete {
    fullName: string;
    movingTime: number;
    elevationGain: number;
    totalDistance: number;
    numberOfActivities: number;
    longestActivity: number;
    averagePacePrKm: string;
    totalDistanceFormatted: string;
    longestActivityFormatted: string;
}