import React from 'react';
import './App.css';
import {Athlete, DepartureBoard, EstimatedCall, WeatherForecast} from "./api/types";
import {fetchStravaScoreboard, fetchWeatherForcast, fetchPublicTransportDepartureBoard} from "./api/client";

function App() {
    const [athletes, setAthletes] = React.useState<Athlete[] | null>(null);
    const [weather, setWeather] = React.useState<WeatherForecast | null>(null);
    const [departureBoard, setDepartureBoard] = React.useState<DepartureBoard | null>(null);

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

    // TODO : https://www.radix-ui.com/primitives

    function getPublicTransportType(estimatedCall: EstimatedCall) {
        return (
            <div style={{backgroundColor: estimatedCall.presentation?.colour}}>
                <img src={`icons/${estimatedCall.transportMode}.svg`} alt="bus" height={30} width={30}/>
            </div>
        )

    }

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

                {departureBoard && (
                    <div>
                        <h2>Departure Board for <u>{departureBoard.name}</u></h2>
                        <p>{departureBoard.estimatedCalls[0].boardingLocation}</p>
                        <ul>
                            {departureBoard.estimatedCalls.slice(0, 5).map((item) => (
                                <li key={item.aimedArrivalTime.toString()}>
                                    <div>
                                        {getPublicTransportType(item)}
                                        {item.lineNumber} {item.frontText} - {item.aimedArrivalTime.toString()}
                                    </div>
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
