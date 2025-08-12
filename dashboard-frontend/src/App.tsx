import React from 'react';
import './App.css';
import Dashboard from "./components/dashboard/Dashboard";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import NotFoundPage from "./components/utils/NotFoundText";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Dashboard/>}/>
                <Route path="*" element={<NotFoundPage/>}/>
            </Routes>
        </BrowserRouter>

    );
}

export default App;
