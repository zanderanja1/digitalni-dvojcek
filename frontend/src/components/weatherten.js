import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCloud, faSun, faCloudSun, faCloudRain, faCloudShowersHeavy, faEye, faEyeSlash, faStar } from '@fortawesome/free-solid-svg-icons'
import Chart from "chart.js/auto";
import { UserContext } from "../userContext";
import { useContext } from "react";

function WeatherCitiesTen(props) {

    const { user } = useContext(UserContext);

    const [isHidden, setIsHidden] = useState(true);
    
    const [isHiddenTempChart, setHiddenTemp] = useState(true);

    const [isHiddenHumidity, setHiddenHumidity] = useState(false);


    const chartRef = useRef(null);
    const chartRef2 = useRef(null);
    const chartRef3 = useRef(null);
    const [hasVoted, setHasVoted] = useState(user && props.cities.favouritesBy.includes(user._id));

    const handleToggle = () => {
        setIsHidden(!isHidden);
        
    };

    const handleToggleTemp = () => {
        setHiddenTemp(true);
        setHiddenHumidity(false);
    };

    const handleToggleHumidity = () => {
        setHiddenHumidity(true);
        setHiddenTemp(false);
    };



    function handlefav() {
        setHasVoted(true);
        const body = {
            userId: user._id
        };
        fetch(`http://localhost:3001/cities/${props.cities._id}/fav`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        }).then(response => console.log(response));
    }


    const getWeatherIcon = (status) => {
        switch (status) {
            case "jasno":
                return faSun;
            case "oblačno":
                return faCloud;
            case "delno oblačno":
                return faCloudSun;
            case "pretežno oblačno":
                return faCloud;
            case "zmerno oblačno":
                return faCloud;
            case "v oblakih":
                return faCloud;
            case "pretežno jasno":
                return faSun;
            case "dež":
                return faCloudRain;
            case "rahel dež":
                return faCloudRain;
            case "Plohe":
                return faCloudShowersHeavy;
            case "Nekaj ploh":
                return faCloudShowersHeavy;
            case "Nevihte z grmenjem":
                return faCloudShowersHeavy;
            case "pretežno sončno":
                return faSun;
            case "Sončno":
                return faSun;
            case "Jasno":
                return faSun;
            case "Pretežno jasno":
                return faSun;
            case "Delno oblačno":
                return faCloud;
            case "Pretežno sončno":
                return faSun;
            case "Pretežno oblačno":
                return faCloud;
            case "Razpršene nevihte z grmenjem":
                return faCloudShowersHeavy;
            case "Dež":
                return faCloudRain;
            case "Rahel dež":
                return faCloudRain;
            case "Oblačno":
                return faCloud;
            case "Nevihte":
                return faCloudShowersHeavy;    
            default:
                return faSun;
            // Default icon (change as needed)
        }
    };



    useEffect(() => {
        if (!isHidden && isHiddenTempChart && chartRef.current) {
            const ctx = chartRef.current.getContext("2d");

            if (chartRef.current.chart) {
                chartRef.current.chart.destroy();
            }

            const dayLabels = props.cities.weather.byDay.flatMap((day) => day.dayTime);
            const datasets = props.cities.weather.byDay.map((day, index) => ({
                label: `Temperatura`,
                data: day.dayTemperature.map((temp) => {
                    const temperatures = temp.split(' do ');
                    const minTemp = parseInt(temperatures[0]);
                    const maxTemp = parseInt(temperatures[1].split(' ')[0]);
                    const average = (minTemp + maxTemp) / 2;
                    return average;
                }),
                borderColor: `rgba(75, 192, 192, ${1 - index * 0.1})`,
                backgroundColor: `rgba(75, 192, 192, ${0.2 - index * 0.02})`,
                tension: 0.6,
                pointRadius: 0,
            }));
            const chartInstance = new Chart(ctx, {
                type: "line",
                data: {
                    labels: dayLabels,
                    datasets: datasets,
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: false,
                        },
                    },
                },
            });

            chartRef.current.chart = chartInstance;
        }
    }, [isHidden, isHiddenTempChart, props.cities.weather.byDay]);


    useEffect(() => {
        if (isHidden && chartRef2.current) {
            const ctx = chartRef2.current.getContext("2d");

            if (chartRef2.current.windChart) {
                chartRef2.current.windChart.destroy();
            }

            const dayLabels = props.cities.weather.byDay.flatMap((day) => day.dayTime);
            const datasets = props.cities.weather.byDay.map((day, index) => ({
                label: `Veter`,
                data: day.dayWind.map((wind) => {
                    const windSpeed = wind.split(' - ');
                    const minWind = parseInt(windSpeed[0]);
                    const maxWind = parseInt(windSpeed[1].split(' ')[0]);
                    const average = (minWind + maxWind);
                    return average;
                }),
                borderColor: `rgba(75, 192, 192, ${1 - index * 0.1})`,
                backgroundColor: `rgba(75, 192, 192, ${0.2 - index * 0.02})`,
                tension: 0.6,
                pointRadius: 0,
            }));
            const chartInstance = new Chart(ctx, {
                type: "line",
                data: {
                    labels: dayLabels,
                    datasets: datasets,
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: false,
                        },
                    },
                },
            });

            chartRef2.current.windChart = chartInstance;
        }
    }, [isHidden, props.cities.weather.byDay]);


    useEffect(() => {
        if (!isHidden && isHiddenHumidity && chartRef3.current) {
            const ctx = chartRef3.current.getContext("2d");

            if (chartRef3.current.chart) {
                chartRef3.current.chart.destroy();
            }

            const dayLabels = props.cities.weather.byDay.flatMap((day) => day.dayTime);
            const datasets = props.cities.weather.byDay.map((day, index) => ({
                label: `Vlažnost`,
                data: day.dayHumidity.map((temp) => {
                    const temperatures = temp.split(' ');
                    const minTemp = parseInt(temperatures[0]);
                    return minTemp;
                }),
                borderColor: `rgba(75, 192, 192, ${1 - index * 0.1})`,
                backgroundColor: `rgba(75, 192, 192, ${0.2 - index * 0.02})`,
                tension: 0.6,
                pointRadius: 0,
            }));
            const chartInstance = new Chart(ctx, {
                type: "line",
                data: {
                    labels: dayLabels,
                    datasets: datasets,
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: false,
                        },
                    },
                },
            });

            chartRef3.current.chart = chartInstance;
        }
    }, [isHidden, isHiddenHumidity, props.cities.weather.byDay]);








    return (



        <div className="allHour">


            <div className="all">



                <div className="namesDiv">



                    <p className="citiesName">{props.cities.name}
                        <div className="hideShow">
                            <button className="hideShowBtn" onClick={handleToggle}>
                                {isHidden ? <FontAwesomeIcon icon={faEyeSlash} /> : <FontAwesomeIcon icon={faEye} />}
                                {isHidden ? 'Pokaži' : 'Skrij'}
                            </button>
                            {user && (
                                <div>
                                    {user && !hasVoted ? (
                                        <button className="addBtn" onClick={handlefav}><i className="fa fa-star" style={{ color: '#24252A' }}></i></button>
                                    ) : (<p style={{ fontSize: '13px' }}><FontAwesomeIcon icon={faStar} style={{ paddingLeft: '7px', paddingTop: '10px', paddingRight: '5px', fontSize: '18px' }} /></p>)}
                                </div>
                            )}
                        </div>
                    </p>

                    <div className="citiesData">

                        <p className="citiesCurr">TRENUTNI PODATKI:</p>
                        <p className="citiesTmp">Temperatura: {props.cities.temperature}</p>
                        <p className="citiesHumidity">Vlažnost: {props.cities.humidity}</p>
                        <p className="citiesWind">Veter: {props.cities.wind}</p>
                        <p className="citiesWeatherStatus">Vreme: {props.cities.weatherStatus}</p>


                    </div>

                </div>
                {isHidden && (

                    <div className="test">
                        <div className="eightDay">
                            <p>Veter naslednjih 8 dni</p>
                        </div>
                        <canvas ref={chartRef2}></canvas>
                    </div>

                )}


            </div>


            {!isHidden && (

                <div className="something">

                    <div style={{ 'display': 'flex', flexDirection: 'row', marginTop: '-50px' }}>

                        <div className="dayContainer">




                            <div className="hourInfo" >

                                <div className="hourName">
                                    <p>{props.cities.name}</p>
                                </div>




                                <div className="dayWeatherMap">

                                    <div className="hourTimeDiv">
                                        <h4>URE</h4>
                                        {props.cities.weather.byDay.map((hour, index) => (
                                            <div key={index} className="hourTime">
                                                {hour.dayTime

                                                    .map((time, subIndex) => (
                                                        <p key={subIndex}>{time}</p>
                                                    ))}
                                            </div>
                                        ))}
                                    </div>


                                    <div className="hourTempDiv">
                                        <h4 onClick={handleToggleTemp} className="onClickChart">TEMPERATURA</h4>
                                        {props.cities.weather.byDay.map((hour, index) => (
                                            <div key={index} className="hourTemp">
                                                {hour.dayTemperature

                                                    .map((temp, subIndex) => (
                                                        <p key={subIndex}>{temp}</p>
                                                    ))}
                                            </div>
                                        ))}
                                    </div>


                                    <div className="hourWindDiv">
                                        <h4>VETER</h4>
                                        {props.cities.weather.byDay.map((hour, index) => (
                                            <div key={index} className="hourWind">
                                                {hour.dayWind

                                                    .map((wind, subIndex) => (
                                                        <p key={subIndex}>{wind}</p>
                                                    ))}
                                            </div>
                                        ))}
                                    </div>


                                    <div className="dayHumidityDiv">
                                        <h4 onClick={handleToggleHumidity} className="onClickChart">VLAŽNOST</h4>
                                        {props.cities.weather.byDay.map((hour, index) => (
                                            <div key={index} className="dayHumidity">
                                                {hour.dayHumidity

                                                    .map((precip, subIndex) => (
                                                        <p key={subIndex}>{precip}</p>
                                                    ))}
                                            </div>
                                        ))}
                                    </div>

                                    <div className="hourWeatherDiv">
                                        <h4>VREME</h4>
                                        {props.cities.weather.byDay.map((hour, index) => (
                                            <div key={index} className="hourWeatherStatus">
                                                {hour.dayWeatherStatus

                                                    .map((status, subIndex) => (
                                                        status === 'null' ? (
                                                            <p key={subIndex}>&nbsp;</p>
                                                        ) : (
                                                            <p key={subIndex}>
                                                                {status}
                                                                <FontAwesomeIcon icon={getWeatherIcon(status)} style={{ marginLeft: '5px' }} />

                                                            </p>
                                                        )
                                                    ))}
                                            </div>
                                        ))}
                                    </div>



                                </div>



                            </div>

                        </div>

                        {isHiddenTempChart ? (
                            <div className="dayTempGraph">
                                <div className="eightDay2">
                                    <p>temperatura za naslednjih 8 dni</p>
                                </div>
                                <canvas ref={chartRef} />
                            </div>
                        ) : (
                            <div className="dayTempGraph">
                                <div className="eightDay2">
                                    <p>vlažnost za naslednjih 8 dni</p>
                                </div>
                                <canvas ref={chartRef3} />
                            </div>
                        )}

                    </div>

                </div>



            )}

        </div>

    );



}

export default WeatherCitiesTen;
