import './Dashboard.css';
import WeatherForecast from "../weather-forecast/WeatherForecast";
import StravaLeaderBoard from "../strava-leaderboard/StravaLeaderBoard";
import DepartureBoard from "../departureboard/DepartureBoard";
import React from "react";
import {Athlete, Coordinates, EnTurDepartureBoard, WeatherForecastData} from "../../api/types";
import {fetchPublicTransportDepartureBoard, fetchStravaScoreboard, fetchWeatherForcast} from "../../api/client";
import {Flex} from "@radix-ui/themes";

export default function Dashboard() {
    const [athletes, setAthletes] = React.useState<Athlete[] | null>(null);
    const [weather, setWeather] = React.useState<WeatherForecastData | null>(null);
    const [departureBoard, setDepartureBoard] = React.useState<EnTurDepartureBoard | null>(null);
    const [userLocation, setUserLocation] = React.useState<Coordinates | null>(null);

    // Fetch user location once
    React.useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                setUserLocation({
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude
                });
            });
        } else {
            console.error("Geolocation is not supported by this browser.");
        }
    }, []);

    // Fetch Strava and departure board on mount and set intervals
    React.useEffect(() => {
        const fetchAthletes = () =>
            fetchStravaScoreboard().then(data => data && setAthletes(data));
        const fetchDepartures = () =>
            fetchPublicTransportDepartureBoard().then(data => data && setDepartureBoard(data));

        fetchAthletes();
        fetchDepartures();

        const athletesInterval = setInterval(fetchAthletes, 5 * 60_000);
        const departuresInterval = setInterval(fetchDepartures, 60_000);

        return () => {
            clearInterval(athletesInterval);
            clearInterval(departuresInterval);
        };
    }, []);

    // Fetch weather immediately when userLocation changes
    React.useEffect(() => {
        const fetchWeather = () =>
            fetchWeatherForcast(userLocation ?? undefined).then(data => data && setWeather(data));

        fetchWeather();
        const weatherInterval = setInterval(fetchWeather, 5 * 60_000);

        return () => clearInterval(weatherInterval);
    }, [userLocation]);

    return (
        <div className='dashboard'>
            <Flex className='module weather' align='center'>
                <WeatherForecast data={weather} numberOfHours={7}/>
            </Flex>
            <Flex className='module departure-board'>
                <DepartureBoard data={departureBoard} numberOfDepartures={6}/>
            </Flex>
            <Flex className='module leaderboard' align='center'>
                <StravaLeaderBoard data={athletes}/>
            </Flex>
        </div>
    )
}
