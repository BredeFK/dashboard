import React from 'react';
import logo from './logo.svg';
import './App.css';
import {Athlete, WeatherForecast} from "./api/types";
import {fetchStravaScoreboard, fetchWeatherForcast} from "./api/client";

function App() {
    const [athletes, setAthletes] = React.useState<Athlete[] | null>(null);
    const [weather, setWeather] = React.useState<WeatherForecast | null>(null);

    React.useEffect(() => {
        fetchStravaScoreboard().then(data => {
            if (data) setAthletes(data);
        });

        fetchWeatherForcast().then(data => {
            if (data) setWeather(data);
        });

    }, []);

    return (
        <div className="App">
            <header className="App-header">
                {weather && (
                    <div>
                        <h2>Weather</h2>
                        <p>Last updated: {weather.lastUpdated}</p>
                        <ul>
                            {weather.weatherSeries.slice(0, 5).map((item) => (
                                <li key={item.timestamp}>
                                    {new Date(item.timestamp).toLocaleTimeString()}: {item.temperature}°C, {item.uvIndexClearSky} UV
                                    Index
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                {athletes != null && athletes.length > 0 && (
                    <div>
                        <h2>Strava Scoreboard</h2>
                        <ul>
                            {athletes.map((athlete) => (
                                <li key={athlete.fullName}>
                                    {athlete.fullName}: {athlete.totalDistanceFormatted} ({athlete.numberOfActivities} activities)
                                </li>
                            ))}
                        </ul>
                    </div>
                )}
            </header>
        </div>
    );
}

export default App;
