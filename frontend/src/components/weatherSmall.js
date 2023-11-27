import React, { useState } from "react";
import 'mapbox-gl/dist/mapbox-gl.css';
import ReactMapGL, { Source, Layer, FullscreenControl, GeolocateControl, Map, Marker, NavigationControl } from "react-map-gl";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCloud, faSun, faCloudSun, faCloudRain, faCloudShowersHeavy } from '@fortawesome/free-solid-svg-icons'

function WeatherSmall(props) {

  const [lng] = useState(15.647);
  const [lat] = useState(46.555);

  const [lng2] = useState(15.68);
  const [lat2] = useState(46.48);

  const coordinates = [
    { longitude: 13.633, latitude: 45.892, temp: props.weather.temperature_small[0], weatherS: props.weather.weatherStatus_small[0] },
    { longitude: 15.262, latitude: 46.233, temp: props.weather.temperature_small[1], weatherS: props.weather.weatherStatus_small[1] },
    { longitude: 15.190, latitude: 45.569, temp: props.weather.temperature_small[2], weatherS: props.weather.weatherStatus_small[2] },
    { longitude: 14.370, latitude: 46.0936, temp: props.weather.temperature_small[3], weatherS: props.weather.weatherStatus_small[3] },
    { longitude: 14.852, latitude: 45.638, temp: props.weather.temperature_small[4], weatherS: props.weather.weatherStatus_small[4] },
    { longitude: 13.850, latitude: 46.381, temp: props.weather.temperature_small[5], weatherS: props.weather.weatherStatus_small[5] },
    { longitude: 14.487, latitude: 46.252, temp: props.weather.temperature_small[6], weatherS: props.weather.weatherStatus_small[6] },
    { longitude: 15.646, latitude: 46.554, temp: props.weather.temperature_small[7], weatherS: props.weather.weatherStatus_small[7] },
    { longitude: 14.158, latitude: 46.361, temp: props.weather.temperature_small[9], weatherS: props.weather.weatherStatus_small[9] },
    { longitude: 13.590, latitude: 45.514, temp: props.weather.temperature_small[10], weatherS: props.weather.weatherStatus_small[10] },
    { longitude: 15.283, latitude: 46.073, temp: props.weather.temperature_small[11], weatherS: props.weather.weatherStatus_small[11] },
    { longitude: 14.504, latitude: 46.056, temp: props.weather.temperature_small[12], weatherS: props.weather.weatherStatus_small[12] },
    { longitude: 16.157, latitude: 46.656, temp: props.weather.temperature_small[13], weatherS: props.weather.weatherStatus_small[13] },
    { longitude: 15.167, latitude: 45.806, temp: props.weather.temperature_small[14], weatherS: props.weather.weatherStatus_small[14] },
    { longitude: 14.210, latitude: 45.775, temp: props.weather.temperature_small[15], weatherS: props.weather.weatherStatus_small[15] },
    { longitude: 13.730, latitude: 46.493, temp: props.weather.temperature_small[16], weatherS: props.weather.weatherStatus_small[16] },
    { longitude: 15.075, latitude: 46.507, temp: props.weather.temperature_small[17], weatherS: props.weather.weatherStatus_small[17] },
  ];



  const weatherStations = [

    { longitude: 15.686, latitude: 46.48, name: 'MARIBOR' },
    { longitude: 15.117, latitude: 46.483, name: 'SLOVENJ GRADEC' },
    { longitude: 16.2, latitude: 46.65, name: 'MURSKA SOBOTA' },
    { longitude: 15.233, latitude: 46.233, name: 'CELJE' },
    { longitude: 14.51, latitude: 46.06, name: 'LJUBLJANA' },
    { longitude: 14.2, latitude: 45.767, name: 'POSTOJNA' },
    { longitude: 14.183, latitude: 46.367, name: 'LESCE' },
    { longitude: 14.85, latitude: 45.65, name: 'KOCEVJE' },
    { longitude: 13.833, latitude: 46.267, name: 'VOGEL' },
    { longitude: 15.183, latitude: 45.8, name: 'NOVO MESTO' },
    { longitude: 15.15, latitude: 45.567, name: 'CRNOMELJ' },
    { longitude: 15.53, latitude: 45.9, name: 'CERKLJE' },
    { longitude: 15.283, latitude: 46.067, name: 'LISCA' },
    { longitude: 14.030, latitude: 45.99, name: 'IDRIJA' },
    { longitude: 14.236, latitude: 45.55, name: 'IL. BISTRICA' },
    { longitude: 15.86, latitude: 46.42, name: 'PTUJ' },
    { longitude: 14.81, latitude: 46.296, name: 'GORNJI GRAD' }
  ];



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
      default:
        return faSun;
    }
  };

  return (

    <div>

      <div className="small_Container">
        <div>

          <div className="poSloveniji">
            <p>VREME PO SLOVENIJI DANES</p>
          </div>

          <div className="wetherMap">


            <div className="name_small">
              <h4>MESTO</h4>
              {props.weather.name_small.map((name, index) => (
                <p key={index}>{name}</p>
              ))}
            </div>

            <div className="temp_small">
              <h4>TEMPERATURA</h4>
              {props.weather.temperature_small.map((temp, index) => (
                <p key={index}>{temp === 'null' ? '\u00A0' : temp}</p>
              ))}

            </div>

            <div className="wind_small">
              <h4>VETER</h4>
              {props.weather.wind_small.map((wind, index) => (
                <p key={index}>{wind === 'null' ? '\u00A0' : wind}</p>
              ))}
            </div>

            <div className="humidity_small">
              <h4>VLAŽNOST</h4>
              {props.weather.humidity_small.map((humidity, index) => (
                <p key={index}>{humidity === 'null' ? '\u00A0' : humidity}</p>
              ))}
            </div>


            <div className="pressure_small">
              <h4>ZR. TLAK</h4>
              {props.weather.pressure_small.map((press, index) => (
                <p key={index}>{press === 'null' ? '\u00A0' : press}</p>
              ))}

            </div>

            <div className="weatherStatus">
              <h4>VREME</h4>
              {props.weather.weatherStatus_small.map((status, index) => (
                status === 'null' ? (
                  <p key={index}>&nbsp;</p>
                ) : (
                  <p key={index}>
                    {status}
                    <FontAwesomeIcon icon={getWeatherIcon(status)} style={{ marginLeft: '5px' }} />

                  </p>
                )
              ))}
            </div>



          </div>

        </div>

        <div className="smallMap">

          <Map
            mapboxAccessToken="pk.eyJ1Ijoib3Jpb241NDU1IiwiYSI6ImNsaTF3czVyMjIyMDYzcW50MzJyeGRycHUifQ.3fQtdNSM2NquIkTAH_1DVA"
            style={{
              width: '1000px',
              height: '700px',
              borderRadius: '10px',
              border: '1px solid red',
            }}
            initialViewState={{
              longitude: lng,
              latitude: lat,
              zoom: 8
            }}
            mapStyle="mapbox://styles/mapbox/streets-v12"
          >
            {coordinates.map((coord, index) => (
              <div key={index}>
                <Marker longitude={coord.longitude} latitude={coord.latitude} />

                <Marker key={index} longitude={coord.longitude} latitude={coord.latitude}>
                  <div className="marker">
                    <p className="tempWeatherMap">
                      {coord.temp} {coord.weatherS !== 'null' && `/ ${coord.weatherS}`}
                      {coord.weatherS !== 'null' && (
                        <FontAwesomeIcon
                          icon={getWeatherIcon(coord.weatherS)}
                          style={{ marginLeft: '5px' }}
                        />
                      )}
                    </p>
                  </div>


                </Marker>
              </div>
            ))}

            <NavigationControl />
            <GeolocateControl />
            <FullscreenControl />
          </Map>


        </div>





      </div>

      <div className="stationDiv">
        <hr />

        <div>
          <p className="stationsP">Meteorološka postaje po sloveniji</p>
        </div>

        <div className="mapContainer">
          <ReactMapGL
            mapboxAccessToken="pk.eyJ1Ijoib3Jpb241NDU1IiwiYSI6ImNsaTF3czVyMjIyMDYzcW50MzJyeGRycHUifQ.3fQtdNSM2NquIkTAH_1DVA"
            style={{
              width: '1800px',
              height: '900px',
              borderRadius: '10px',
              marginBottom: '70px',
              marginLeft: '130px'
            }}
            initialViewState={{
              longitude: lng2,
              latitude: lat2,
              zoom: 8
            }}
            mapStyle="mapbox://styles/mapbox/streets-v12"
          >
            <Source
              id="circle-source"
              type="geojson"
              data={{
                type: 'FeatureCollection',
                features: weatherStations.map((coord, index) => ({
                  type: 'Feature',
                  geometry: {
                    type: 'Point',
                    coordinates: [coord.longitude, coord.latitude]
                  }
                }))
              }}
            >
              <Layer
                id="circle-layer"
                type="circle"
                paint={{
                  'circle-radius': 20,
                  'circle-color': 'grey',
                  'circle-opacity': 0.7,
                }}
              />
            </Source>

            <NavigationControl />
            <GeolocateControl />
            <FullscreenControl />
          </ReactMapGL>


        </div>

      </div>


    </div>

  );
}

export default WeatherSmall;






