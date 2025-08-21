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
    const stravaLeaderBoardIsMocked = process.env.REACT_APP_STRAVA_LEADERBOARD_MOCKED === 'true';

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
            fetchStravaLeaderboard(stravaLeaderBoardIsMocked).then(data => data && setLeaderboard(data));
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
