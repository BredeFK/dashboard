import {EnTurDepartureBoard, EstimatedCall} from "../../api/types";
import './DepartureBoard.css'
import React from "react";
import {Card, Flex, Text} from '@radix-ui/themes'
import {NotFound} from "../not-found/NotFound";
import ModuleTitle from "../ui/module-title/ModuleTitle";
import Loading from "../loading/Loading";

export default function DepartureBoard({data, numberOfDepartures, loading}: Readonly<{
    data: EnTurDepartureBoard | null,
    numberOfDepartures: number,
    loading: boolean
}>) {

    if (loading) {
        return <Loading text='Laster avganger'/>
    }

    if (!data) {
        return <NotFound text='Klarte ikke å finne avganger...'/>
    }

    return (
        <>
            <ModuleTitle titleText={data.name} subTitleText={data.description} align='center'/>
            <Flex direction='column' gap='2' align='center' className='estimated-call-list'>
                {data.estimatedCalls
                    .slice(0, numberOfDepartures)
                    .sort((a, b) =>
                        new Date(a.expectedDepartureTime).getTime() - new Date(b.expectedDepartureTime).getTime())
                    .map((item) => (
                        <EstimatedCallItem estimatedCall={item} key={`${item.frontText}-${item.aimedDepartureTime}`}/>
                    ))}
            </Flex>
        </>

    )
}

function EstimatedCallItem({estimatedCall}: Readonly<{ estimatedCall: EstimatedCall }>) {
    const aimedTime = new Date(estimatedCall.aimedDepartureTime)
    const expectedTime = new Date(estimatedCall.expectedDepartureTime)
    const isDelayed = estimatedCall.realtime && getIsDelayed(expectedTime, aimedTime)

    return (
        <Card className='estimated-call-item'>
            <Flex direction="column" gap="1">
                <Flex gap="3" align="center">
                    <TransportBadge estimatedCall={estimatedCall}/>
                    <Text className='front-text' as="div" size="6" weight="bold">{estimatedCall.frontText}</Text>
                </Flex>
                <Flex gap='2' align='center'>
                    {isDelayed ? (
                        <>
                            <Text as='div' size='2' color='red' weight='bold'>
                                {formatTime(expectedTime)}
                            </Text>
                            <Text
                                as='div'
                                size='2'
                                color='gray'
                                className='delayed-text'>
                                {formatTime(aimedTime)}
                            </Text>
                        </>) : (
                        <Text as='div' size='2' color='gray' weight='bold'>
                            {formatTime(aimedTime)}
                        </Text>
                    )}
                </Flex>
            </Flex>
        </Card>
    )
}

function TransportBadge({estimatedCall}: Readonly<{ estimatedCall: EstimatedCall }>) {
    return (
        <div className='transport-badge' style={{backgroundColor: estimatedCall.presentation?.colour}}>
            <img src={`icons/${estimatedCall.transportMode}.svg`} alt="bus"/>
            <span className='transport-line-number'>{estimatedCall.lineNumber.padStart(3, '')}</span>
        </div>
    )
}

const getIsDelayed = (expectedTime: Date, aimedTime: Date) =>
    floorToMinute(expectedTime).getTime() > floorToMinute(aimedTime).getTime()

const floorToMinute = (date: Date) =>
    new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());

const formatTime = (date: Date) =>
    date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})

