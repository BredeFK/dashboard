import {Athlete, Leaderboard} from "../../api/types";
import React from "react";
import {Flex, Table, Text} from "@radix-ui/themes";
import './StravaLeaderBoard.css'
import {NotFound} from "../not-found/NotFound";
import ModuleTitle from "../ui/module-title/ModuleTitle";


const headers = [null, 'Utøver', 'Distanse', 'Økter', 'Lengste', 'Snittfart', 'HøydeM.']

export default function StravaLeaderBoard({data}: Readonly<{ data: Leaderboard | null }>) {
    if (!data) {
        return <NotFound text='Klarte ikke å finne toppliste..'/>
    } else {

        const timeRange = formatDateRange(data.startDate, data.endDate)

        return (
            <Flex direction='column' gap='2' align='stretch' style={{height: '100%'}}>
                <ModuleTitle titleText='Strava toppliste' subTitleText={timeRange} align='start'/>
                <Flex direction='column' gap='2' className='leaderboard-container'>
                    <Table.Root size='3' variant='surface' className='leaderboard-table'>
                        <Table.Header>
                            <Table.Row>
                                {headers.map(header =>
                                    <Table.ColumnHeaderCell key={header}>
                                        <Text size='4'>{header}</Text>
                                    </Table.ColumnHeaderCell>)}
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {data.athletes.length > 0 ? (
                                    data.athletes.map((athlete, index) => (
                                        <AthleteItem athlete={athlete} rank={index + 1} key={athlete.fullName}/>
                                    )))
                                : (
                                    <Table.Row>
                                        <Table.Cell colSpan={headers.length} className='empty-row'>
                                            <Text color='gray' size='3'>Ingen resultater enda 🫥</Text>
                                        </Table.Cell>
                                    </Table.Row>
                                )}
                        </Table.Body>
                    </Table.Root>
                </Flex>
            </Flex>
        )
    }
}


function AthleteItem({athlete, rank}: Readonly<{ athlete: Athlete, rank: number }>) {
    return (
        <Table.Row className='athlete-row'>
            <Table.RowHeaderCell>{rank}</Table.RowHeaderCell>
            <Table.Cell>{athlete.fullName}</Table.Cell>
            <Table.Cell>{formatDistance(athlete.totalDistanceFormatted)}</Table.Cell>
            <Table.Cell>{athlete.numberOfActivities}</Table.Cell>
            <Table.Cell>{formatDistance(athlete.longestActivityFormatted)}</Table.Cell>
            <Table.Cell>{formatDistance(athlete.averagePacePrKm)}</Table.Cell>
            <Table.Cell><b>{formatElevationGain(athlete.elevationGain)}</b> <span
                className='unit-text'>m</span></Table.Cell>
        </Table.Row>
    )
}

const formatDistance = (distance: string) => {
    let [value, unit] = distance.split(' ');
    if (!value.includes(':')) {
        value = parseFloat(value).toFixed(1);
    }
    return (
        <>
            <b>{value}</b> <span className='unit-text'>{unit}</span>
        </>
    )
}

const formatElevationGain = (elevationGain: number) => {
    return Intl.NumberFormat('nb-NO').format(elevationGain)
}

function formatDateRange(startDate: string, endDate: string): string {
    let start = formatDate(startDate)
    const end = formatDate(endDate)
    if (start.split(' ')[1] === end.split(' ')[1]) {
        start = start.split(' ')[0]
    }
    return `${start} - ${end}`
}

function formatDate(date: string): string {
    return new Date(date).toLocaleDateString("nb-NO", {
        day: 'numeric',
        month: 'short'
    }).replace(/\.$/, "")
}


