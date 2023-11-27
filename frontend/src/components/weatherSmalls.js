import { useState, useEffect } from 'react';
import WeatherSmall from './weatherSmall';

function WeatherSmalls() {
    const [weatherSmall, setWeather] = useState([]);

    useEffect(function () {
        const getWeatherSmalls = async function () {
            const res = await fetch("http://localhost:3001/citiesSmall");
            const data = await res.json();
            setWeather(data);
        }
        getWeatherSmalls();
    }, []);


    return (
        <div>

            {weatherSmall.map(weather => (<WeatherSmall weather={weather} key={weather._id}></WeatherSmall>))}

        </div>
    );
}

export default WeatherSmalls;
