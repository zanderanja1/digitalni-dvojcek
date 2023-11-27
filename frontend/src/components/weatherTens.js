import { useEffect, useState } from "react";
import WeatherCitiesTen from "./weatherten";

function WeatherTens() {
  const [weatherCities, setWeatherTen] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredCities, setFilteredCities] = useState([]);

  useEffect(() => {
    const getWeathersTens = async () => {
      const res = await fetch("http://localhost:3001/cities");
      const data = await res.json();
      setWeatherTen(data);
    };
    getWeathersTens();
  }, []);

  useEffect(() => {
    if (searchTerm === "") {
      setFilteredCities(weatherCities);
    } else {
      const regex = new RegExp(searchTerm, "i");
      const filtered = weatherCities.filter(city => regex.test(city.name));
      setFilteredCities(filtered);
    }
  }, [searchTerm, weatherCities]);

  const handleSearch = event => {
    setSearchTerm(event.target.value);
  };

  const handleClearInput = () => {
    setSearchTerm("");
  };

  return (
    <div>
      <div className="searchBar">
        <input
          className="inputBar"
          type="text"
          placeholder="Search by City Name"
          value={searchTerm}
          onChange={handleSearch}
        />
        {searchTerm && (
          <button className="clearButton" onClick={handleClearInput}>
            Clear
          </button>
        )}
      </div>
      {filteredCities.map(city => (
        <WeatherCitiesTen cities={city} key={city._id} />
      ))}
    </div>
  );
}

export default WeatherTens;
