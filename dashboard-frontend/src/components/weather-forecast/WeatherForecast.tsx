import {WeatherForecastData, WeatherInstance} from "../../api/types";
import React from "react";
import {Flex, Text} from "@radix-ui/themes";
import './WeatherForecast.css'

export default function WeatherForecast({data}: Readonly<{ data: WeatherForecastData | null }>) {
    if (!data) {
        return (
            <div>
                <h2>Værmelding</h2>
                <p>Klarte ikke å finne værmeldingen...</p>
            </div>
        )
    }

    return (
        <div>
            <h2>Værmelding</h2>
            <Flex gap="3" align="center" style={{overflowX: 'auto'}}>
                {data.weatherSeries.slice(0, 5).map((item) => (
                    <WeatherItem item={item} key={item.timestamp}/>
                ))}
            </Flex>
        </div>
    )
}

function WeatherItem({item}: Readonly<{ item: WeatherInstance }>) {

    const hour = new Date(item.timestamp).toLocaleTimeString([], {hour: '2-digit'})
    return (
        <Flex direction='column' gap='1' align='center' className='weather-item'>
            <Text size='2'>{hour}</Text>
            <img src={item.symbolUrl} alt={item.symbolCode} width={32} height={32}/>
            <Text size='2'>{item.temperature.toFixed(0)}°</Text>
            <Text size='2' color='gray'>{item.uvIndexClearSky?.toFixed(0)} UV</Text>
        </Flex>
    )
}
