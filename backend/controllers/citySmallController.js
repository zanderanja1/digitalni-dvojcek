var CitysmallModel = require("../models/citySmallModel.js");

/**
 * citySmallController.js
 *
 * @description :: Server-side logic for managing citySmalls.
 */
module.exports = {
  /**
   * citySmallController.list()
   */
  list: function (req, res) {
    CitysmallModel.find(function (err, citySmalls) {
      if (err) {
        return res.status(500).json({
          message: "Error when getting citySmall.",
          error: err
        });
      }

      return res.json(citySmalls);
    });
  },

  /**
   * citySmallController.show()
   */
  show: function (req, res) {
    var id = req.params.id;

    CitysmallModel.findOne({ _id: id }, function (err, citySmall) {
      if (err) {
        return res.status(500).json({
          message: "Error when getting citySmall.",
          error: err
        });
      }

      if (!citySmall) {
        return res.status(404).json({
          message: "No such citySmall"
        });
      }

      return res.json(citySmall);
    });
  },

  /**
   * citySmallController.create()
   */
  create: function (req, res) {
    var citySmall = new CitysmallModel({
      name_small: req.body.name_small,
      temperature_small: req.body.temperature_small,
      humidity_small: req.body.humidity_small,
      wind_small: req.body.wind_small,
      pressure_small: req.body.pressure_small,
      weatherStatus_small: req.body.weatherStatus_small,
      latitude: req.body.latitude,
      longitude: req.body.longitude
    });

    citySmall.save(function (err, citySmall) {
      if (err) {
        return res.status(500).json({
          message: "Error when creating citySmall",
          error: err
        });
      }

      return res.status(201).json(citySmall);
    });
  },

  /**
   * citySmallController.update()
   */
  update: function (req, res) {
    var id = req.params.id;

    CitysmallModel.findOne({ _id: id }, function (err, citySmall) {
      if (err) {
        return res.status(500).json({
          message: "Error when getting citySmall",
          error: err
        });
      }

      if (!citySmall) {
        return res.status(404).json({
          message: "No such citySmall"
        });
      }

      citySmall.name_small = req.body.name_small ? req.body.name_small : citySmall.name_small;
      citySmall.temperature_small = req.body.temperature_small ? req.body.temperature_small : citySmall.temperature_small;
      citySmall.humidity_small = req.body.humidity_small ? req.body.humidity_small : citySmall.humidity_small;
      citySmall.wind_small = req.body.wind_small ? req.body.wind_small : citySmall.wind_small;
      citySmall.pressure_small = req.body.pressure_small ? req.body.pressure_small : citySmall.pressure_small;
      citySmall.weatherStatus_small = req.body.weatherStatus_small ? req.body.weatherStatus_small : citySmall.weatherStatus_small;
      citySmall.latitude = req.body.latitude ? req.body.latitude : citySmall.latitude;
      citySmall.longitude = req.body.longitude ? req.body.longitude : citySmall.longitude;
      citySmall.save(function (err, citySmall) {
        if (err) {
          return res.status(500).json({
            message: "Error when updating citySmall.",
            error: err
          });
        }

        return res.json(citySmall);
      });
    });
  },

  /**
   * citySmallController.remove()
   */
  remove: function (req, res) {
    var id = req.params.id;

    CitysmallModel.findByIdAndRemove(id, function (err, citySmall) {
      if (err) {
        return res.status(500).json({
          message: "Error when deleting the citySmall.",
          error: err
        });
      }

      return res.status(204).json();
    });
  },

  searchCity: function (req, res) {
    var cityName = req.query.cityName;

    CitysmallModel.find({ name_small: { $regex: new RegExp(cityName, "i") } }).exec(function (err, cities) {
      if (err) {
        return res.status(500).json({
          message: "Error when searching for city.",
          error: err
        });
      }

      return res.json(cities);
    });
  }
};
