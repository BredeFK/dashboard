import {EnTurDepartureBoard, EstimatedCall} from "../../api/types";
import './DepartureBoard.css'
import React from "react";
import {Card, Flex, Text} from '@radix-ui/themes'

export default function DepartureBoard({data}: Readonly<{ data: EnTurDepartureBoard | null }>) {
    if (!data) {
        return (
            <div>
                <h2>Avganger</h2>
                <p>Klarte ikke å finne avganger...</p>
            </div>
        )
    }
    return (
        <div>
            <h2>Avganger fra <i>{data.name}</i></h2>
            <p>{data.estimatedCalls[0].boardingLocation}</p>
            <ul>
                {data.estimatedCalls
                    .slice(0, 5)
                    .sort((a, b) =>
                        new Date(a.expectedDepartureTime).getTime() - new Date(b.expectedDepartureTime).getTime())
                    .map((item) => (
                        <EstimatedCallItem estimatedCall={item} key={`${item.frontText}-${item.aimedDepartureTime}`}/>
                    ))}
            </ul>
        </div>
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
            <span className='transport-line-number'>{estimatedCall.lineNumber}</span>
        </div>
    )
}

const getIsDelayed = (expectedTime: Date, aimedTime: Date) =>
    floorToMinute(expectedTime).getTime() > floorToMinute(aimedTime).getTime()

const floorToMinute = (date: Date) =>
    new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());

const formatTime = (date: Date) =>
    date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})

