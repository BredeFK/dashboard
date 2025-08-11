import React from 'react';
import './App.css';
import {Athlete, EnTurDepartureBoard, WeatherForecastData} from "./api/types";
import {fetchPublicTransportDepartureBoard, fetchStravaScoreboard, fetchWeatherForcast} from "./api/client";
import DepartureBoard from "./components/departureboard/DepartureBoard";
import StravaLeaderBoard from "./components/strava-leaderboard/StravaLeaderBoard";
import WeatherForecast from "./components/weather-forecast/WeatherForecast";

function App() {
    const [athletes, setAthletes] = React.useState<Athlete[] | null>(null);
    const [weather, setWeather] = React.useState<WeatherForecastData | null>(null);
    const [departureBoard, setDepartureBoard] = React.useState<EnTurDepartureBoard | null>(null);

    React.useEffect(() => {
        fetchStravaScoreboard().then(data => {
            if (data) setAthletes(data);
        });

        fetchWeatherForcast().then(data => {
            if (data) setWeather(data);
        });

        fetchPublicTransportDepartureBoard().then(data => {
            if (data) setDepartureBoard(data);
        })

    }, []);

    return (
        <div className="App">
            <header className="App-header">
                <WeatherForecast data={weather}/>
                <StravaLeaderBoard data={athletes}/>
                <DepartureBoard data={departureBoard}/>
            </header>
        </div>
    );
}

export default App;
