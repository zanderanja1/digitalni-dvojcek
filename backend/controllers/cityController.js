var CityModel = require('../models/cityModel.js');

/**
 * cityController.js
 *
 * @description :: Server-side logic for managing citys.
 */
module.exports = {

    /**
     * cityController.list()
     */
    list: function (req, res) {
        CityModel.find(function (err, citys) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            return res.json(citys);
        });
    },

    /**
     * cityController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        CityModel.findOne({ _id: id }, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'No such city'
                });
            }

            return res.json(city);
        });
    },

    /**
     * cityController.create()
     */
    create: function (req, res) {

        var city = new CityModel({

            name: req.body.name,
            temperature: req.body.temperature,
            humidity: req.body.humidity,
            wind: req.body.wind,
            weatherStatus: req.body.weatherStatus,

            weather: {

                byHour: req.body.weather.byHour.map(hourlyData => ({
                    hourTime: hourlyData.hourTime,
                    hourTemperature: hourlyData.hourTemperature,
                    hourHumidity: hourlyData.hourHumidity,
                    hourWind: hourlyData.hourWind,
                    hourWeatherStatus: hourlyData.hourWeatherStatus
                })),

                byDay: req.body.weather.byDay.map(dailyData => ({
                    dayTime: dailyData.dayTime,
                    dayTemperature: dailyData.dayTemperature,
                    dayHumidity: dailyData.dayHumidity,
                    dayWind: dailyData.dayWind,
                    dayWeatherStatus: dailyData.dayWeatherStatus
                }))
            },

            favouritesBy: req.body.favouritesBy
        });

        city.save(function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating city',
                    error: err
                });
            }

            return res.status(201).json(city);
        });
    },

    /**
     * cityController.update()
     */
    update: function (req, res) {
        var cityId = req.params.id;

        CityModel.findById(cityId, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when finding city',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'City not found'
                });
            }

            console.log(req.body.weather.byHour);

            city.name = req.body.name;
            city.temperature = req.body.temperature;
            city.humidity = req.body.humidity;
            city.wind = req.body.wind;
            city.weatherStatus = req.body.weatherStatus;

            city.weather.byHour = req.body.weather.byHour.map(hourlyData => ({
                hourTime: hourlyData.hourTime,
                hourTemperature: hourlyData.hourTemperature,
                hourHumidity: hourlyData.hourHumidity,
                hourWind: hourlyData.hourWind,
                hourWeatherStatus: hourlyData.hourWeatherStatus
            }));

            city.weather.byDay = req.body.weather.byDay.map(dailyData => ({
                dayTime: dailyData.dayTime,
                dayTemperature: dailyData.dayTemperature,
                dayHumidity: dailyData.dayHumidity,
                dayWind: dailyData.dayWind,
                dayWeatherStatus: dailyData.dayWeatherStatus
            }));

            city.save(function (err, updatedCity) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating city',
                        error: err
                    });
                }

                return res.status(200).json(updatedCity);
            });
        });
    },



    /**
     * cityController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        CityModel.findByIdAndRemove(id, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the city.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },

    listThree: function (req, res) {
        var cityId = req.params.id;

        CityModel.findById(cityId, 'weather.byHour', function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'No such city'
                });
            }

            return res.json(city.weather.byHour);
        });
    },

    listTen: function (req, res) {
        var cityId = req.params.id;

        CityModel.findById(cityId, 'weather.byDay', function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting city.',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'No such city'
                });
            }

            return res.json(city.weather.byDay);
        });
    },


    updateFav: function (req, res) {
        var id = req.params.id;
        var currUser = req.body.userId;

        console.log(currUser);

        CityModel.findById(id, function (err, city) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when finding city.',
                    error: err
                });
            }

            if (!city) {
                return res.status(404).json({
                    message: 'City not found.'
                });
            }

            // Check if user is already in favouritesBy array
            if (city.favouritesBy.includes(currUser)) {
                return res.status(200).json({
                    message: 'User is already in favouritesBy array.',
                    city: city
                });
            }

            // Add user to favouritesBy array
            city.favouritesBy.push(currUser);

            city.save(function (err) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating city.',
                        error: err
                    });
                }

                return res.status(204).send();
            });
        });
    },


    removeFav: function (req, res) {
        var id = req.params.id;
        var currUser = req.body.userId;
        
        console.log(currUser);
        
        CityModel.findById(id, function (err, city) {
          if (err) {
            return res.status(500).json({
              message: 'Error when finding city.',
              error: err
            });
          }
        
          if (!city) {
            return res.status(404).json({
              message: 'City not found.'
            });
          }
        
          // Check if user is already in favouritesBy array
          if (!city.favouritesBy.includes(currUser)) {
            return res.status(200).json({
              message: 'User is not in favouritesBy array.',
              city: city
            });
          }
        
          // Remove user from favouritesBy array
          var index = city.favouritesBy.indexOf(currUser);
          city.favouritesBy.splice(index, 1);
        
          city.save(function (err) {
            if (err) {
              return res.status(500).json({
                message: 'Error when updating city.',
                error: err
              });
            }
        
            return res.status(204).send();
          });
        });
        
      },
    
};
