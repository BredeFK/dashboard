import './Dashboard.css'
import WeatherForecast from "../weather-forecast/WeatherForecast";
import StravaLeaderBoard from "../strava-leaderboard/StravaLeaderBoard";
import DepartureBoard from "../departureboard/DepartureBoard";
import React from "react";
import {Athlete, EnTurDepartureBoard, WeatherForecastData} from "../../api/types";
import {fetchPublicTransportDepartureBoard, fetchStravaScoreboard, fetchWeatherForcast} from "../../api/client";
import {Flex} from "@radix-ui/themes";

export default function Dashboard() {
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
