import {Athlete} from "../../api/types";
import React from "react";
import {Table} from "@radix-ui/themes";
import './StravaLeaderBoard.css'
import {NotFound} from "../not-found/NotFound";

export default function StravaLeaderBoard({data}: Readonly<{ data: Athlete[] | null }>) {
    if (!data) {
        return <NotFound text='Klarte ikke å finne toppliste..'/>

    } else if (data.length === 0) {
        return (
            <div>
                <p>Ingen aktiviteter i Strava...</p>
            </div>
        )
    } else {
        const headers = [null, 'Utøver', 'Distanse', 'Økter', 'Lengste', 'Snittfart', 'HøydeM.']
        return (
            <Table.Root size='3' variant={'surface'} className='leaderboard-table'>
                <Table.Header>
                    <Table.Row className='athlete-header'>
                        {headers.map(header =>
                            <Table.ColumnHeaderCell key={header} className='leaderboard-header'>
                                {header}
                            </Table.ColumnHeaderCell>)}
                    </Table.Row>
                </Table.Header>
                <Table.Body>
                    {data.map((athlete, index) => (
                        <AthleteItem athlete={athlete} rank={index + 1} key={athlete.fullName}/>
                    ))}
                </Table.Body>
            </Table.Root>
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
            <Table.Cell><b>{athlete.elevationGain}</b> <span className='unit-text'>m</span></Table.Cell>
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


