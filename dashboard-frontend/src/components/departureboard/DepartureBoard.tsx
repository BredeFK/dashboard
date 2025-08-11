import {EnTurDepartureBoard, EstimatedCall} from "../../api/types";
import './DepartureBoard.css'
import React from "react";

export default function DepartureBoard({data}: Readonly<{ data: EnTurDepartureBoard | null }>) {
    if (!data) {
        return (
            <div>
                <h2>Departure Board</h2>
                <p>Could not find any departures...</p>
            </div>
        )
    }
    return (
        <div>
            <h2>Departure Board for <i>{data.name}</i></h2>
            <p>{data.estimatedCalls[0].boardingLocation}</p>
            <ul>
                {data.estimatedCalls.slice(0, 5).map((item) => (
                    <EstimatedCallItem estimatedCall={item}/>
                ))}
            </ul>
        </div>
    )
}

function EstimatedCallItem({estimatedCall}: Readonly<{ estimatedCall: EstimatedCall }>) {
    return (
        <li key={estimatedCall.aimedArrivalTime.toString()} className="departure-item">
            <TransportBadge estimatedCall={estimatedCall}/>
            <span>{estimatedCall.frontText} {estimatedCall.aimedArrivalTime.toString()}</span>
        </li>
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
