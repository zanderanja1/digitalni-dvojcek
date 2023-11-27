import { useEffect, useState } from "react";
import FavouritesCity from "./favourite";
/*
function FavouritesCities() {

    const { user } = useContext(UserContext);

  const [weatherCities, setCity] = useState([]);

  useEffect(function () {
    const getCities = async function () {
      const res = await fetch("http://localhost:3001/cities");
      const data = await res.json();
      setCity(data);
    };
    getCities();
  }, []);

  const filteredCities = weatherCities.filter((city) =>
  city.favouritesBy.includes(user._id)
);

  return (
    <div>
      {filteredCities.map((city) => (
        <FavouritesCity cities={city} key={city._id}></FavouritesCity>
      ))}
    </div>
  );
}

export default FavouritesCities;
*/

function FavouritesCities() {

const [weatherCities, setCity] = useState([]);

useEffect(function () {
  const getCities = async function () {
    const res = await fetch("http://localhost:3001/cities");
    const data = await res.json();
    setCity(data);
  };
  getCities();
}, []);

return (
  <div>
    
      {weatherCities.map(cities => (<FavouritesCity cities={cities} key={cities._id}></FavouritesCity>))}

  </div>
);
}

export default FavouritesCities;