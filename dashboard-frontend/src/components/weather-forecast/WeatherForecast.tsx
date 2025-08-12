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
                {data.weatherSeries.slice(0, 6).map((item) => (
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
            {UvBadge(item.uvIndexClearSky ?? null)}
        </Flex>
    )
}

function uvIndexScale(uvIndex: number): UvIndexInfo {
    switch (true) {
        case uvIndex === 0:
            return {colour: '#36373A', description: 'Ingen UV-stråling'};
        case uvIndex >= 1 && uvIndex <= 2:
            return {colour: '#0C551F', description: 'Lav'};
        case uvIndex >= 3 && uvIndex <= 5:
            return {colour: '#746401', description: 'Moderat'};
        case uvIndex >= 6 && uvIndex <= 7:
            return {colour: '#774801', description: 'Sterk'};
        case uvIndex >= 8 && uvIndex <= 10:
            return {colour: '#7B0A02', description: 'Svært sterk'};
        case uvIndex > 10:
            return {colour: '#491580', description: 'Ekstrem'};
        default:
            return {colour: '#ffffff', description: 'Ukjent UV grad'};
    }
}


function UvBadge(uvIndex: number | null) {
    if (uvIndex === null) {
        return null;
    }
    const uvIndexRounded = parseInt(uvIndex.toFixed(0))
    const uvBadgeInfo = uvIndexScale(uvIndexRounded);
    return (
        <div title={uvBadgeInfo.description}>
            <div className='uv-badge' style={{backgroundColor: uvBadgeInfo.colour}}>
                <Text size='2'>{uvIndexRounded}</Text>
            </div>
        </div>
    )
}

type UvIndexInfo = {
    colour: string,
    description: string,
}
