import './Dashboard.css';
import WeatherForecast from "../weather-forecast/WeatherForecast";
import StravaLeaderBoard from "../strava-leaderboard/StravaLeaderBoard";
import DepartureBoard from "../departureboard/DepartureBoard";
import React from "react";
import {Coordinates, EnTurDepartureBoard, Leaderboard, WeatherForecastData} from "../../api/types";
import {fetchPublicTransportDepartureBoard, fetchStravaLeaderboard, fetchWeatherForcast} from "../../api/client";
import {Flex} from "@radix-ui/themes";

export default function Dashboard() {
    const [leaderboard, setLeaderboard] = React.useState<Leaderboard | null>(null);
    const [weather, setWeather] = React.useState<WeatherForecastData | null>(null);
    const [departureBoard, setDepartureBoard] = React.useState<EnTurDepartureBoard | null>(null);
    const [userLocation, setUserLocation] = React.useState<Coordinates | null>(null);

    const places: Coordinates[] = [
        {latitude: 59.913, longitude: 10.738}, // 0 Oslo
        {latitude: 60.394, longitude: 5.325}, // 1 Bergen
        {latitude: 59.807, longitude: 10.021}, // 2 Krokstadelva
        {latitude: 27.958, longitude: -15.606}, // 3 Gran Canaria
        {latitude: 63.744, longitude: 11.299}, // 4 Levanger
        {latitude: 58.969, longitude: 5.731}, // 5 Stavanger
        {latitude: 59.696, longitude: 10.175}, // 6 Drammen
        {latitude: 59.941, longitude: 10.829}, // 7 Bjerke
    ]

    React.useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                setUserLocation({
                    latitude: parseFloat(position.coords.latitude.toFixed(3)),
                    longitude: parseFloat(position.coords.longitude.toFixed(3))
                });
            });
        } else {
            console.error("Geolocation is not supported by this browser.");
        }
    }, []);

    React.useEffect(() => {
        const fetchAthletes = () =>
            fetchStravaLeaderboard().then(data => data && setLeaderboard(data));
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
                <StravaLeaderBoard data={leaderboard}/>
            </Flex>
        </div>
    )
}
