import { useEffect, useState } from "react";
import WeatherCities from "./weather";

function Weathers() {
  const [weatherCities, setWeather] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredCities, setFilteredCities] = useState([]);

  useEffect(() => {
    const getWeathers = async () => {
      const res = await fetch("http://localhost:3001/cities");
      const data = await res.json();
      setWeather(data);
    };
    getWeathers();
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
        <WeatherCities cities={city} key={city._id} />
      ))}
    </div>
  );
}

export default Weathers;
