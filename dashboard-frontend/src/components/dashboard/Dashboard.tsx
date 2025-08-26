import './Dashboard.css'
import WeatherForecast from "../weather-forecast/WeatherForecast"
import StravaLeaderBoard from "../strava-leaderboard/StravaLeaderBoard"
import DepartureBoard from "../departureboard/DepartureBoard"
import React from "react"
import {Coordinates, EnTurDepartureBoard, Leaderboard, WeatherForecastData} from "../../api/types"
import {Flex} from "@radix-ui/themes"
import {fetchPublicTransportDepartureBoard, fetchStravaLeaderboard, fetchWeatherForcast} from "../../api/client";

export default function Dashboard() {
    const [leaderboard, setLeaderboard] = React.useState<Leaderboard | null>(null)
    const [loadingLeaderboard, setLoadingLeaderboard] = React.useState<boolean>(true)
    const [weather, setWeather] = React.useState<WeatherForecastData | null>(null)
    const [loadingWeather, setLoadingWeather] = React.useState<boolean>(true)
    const [departureBoard, setDepartureBoard] = React.useState<EnTurDepartureBoard | null>(null)
    const [loadingDepartureBoard, setLoadingDepartureBoard] = React.useState<boolean>(true)
    const [userLocation, setUserLocation] = React.useState<Coordinates | null>(null)
    const stravaLeaderBoardIsMocked = process.env.REACT_APP_STRAVA_LEADERBOARD_MOCKED === 'true'

    React.useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                setUserLocation({
                    latitude: parseFloat(position.coords.latitude.toFixed(3)),
                    longitude: parseFloat(position.coords.longitude.toFixed(3))
                })
            })
        } else {
            console.error("Geolocation is not supported by this browser.")
        }
    }, [])

    React.useEffect(() => {
        const fetchAthletes = () =>
            fetchStravaLeaderboard(stravaLeaderBoardIsMocked)
                .then(data => data && setLeaderboard(data))
                .finally(() => setLoadingLeaderboard(false))

        const fetchDepartures = () =>
            fetchPublicTransportDepartureBoard()
                .then(data => data && setDepartureBoard(data))
                .finally(() => setLoadingDepartureBoard(false))


        fetchAthletes()
        fetchDepartures()

        const athletesInterval = setInterval(fetchAthletes, 5 * 60_000)
        const departuresInterval = setInterval(fetchDepartures, 60_000)

        return () => {
            clearInterval(athletesInterval)
            clearInterval(departuresInterval)
        }
    }, [])

    React.useEffect(() => {
        const fetchWeather = () =>
            fetchWeatherForcast(userLocation ?? undefined)
                .then(data => data && setWeather(data))
                .finally(() => setLoadingWeather(false))

        fetchWeather()
        const weatherInterval = setInterval(fetchWeather, 5 * 60_000)

        return () => clearInterval(weatherInterval)
    }, [userLocation])

    return (
        <div className='dashboard'>
            <Flex className='module weather' align='center'>
                <WeatherForecast data={weather} numberOfHours={7} loading={loadingWeather}/>
            </Flex>
            <Flex className='module departure-board'>
                <DepartureBoard data={departureBoard} numberOfDepartures={6} loading={loadingDepartureBoard}/>
            </Flex>
            <Flex className='module leaderboard' align='center'>
                <StravaLeaderBoard data={leaderboard} loading={loadingLeaderboard}/>
            </Flex>
        </div>
    )
}
