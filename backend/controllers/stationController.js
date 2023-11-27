var StationModel = require('../models/stationModel.js');

/**
 * stationController.js
 *
 * @description :: Server-side logic for managing stations.
 */
module.exports = {

    /**
     * stationController.list()
     */
    list: function (req, res) {
        StationModel.find(function (err, stations) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station.',
                    error: err
                });
            }

            return res.json(stations);
        });
    },

    /**
     * stationController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        StationModel.findOne({_id: id}, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station.',
                    error: err
                });
            }

            if (!station) {
                return res.status(404).json({
                    message: 'No such station'
                });
            }

            return res.json(station);
        });
    },

    /**
     * stationController.create()
     */
    create: function (req, res) {
        var station = new StationModel({
			name_station : req.body.name_station,
			lat : req.body.lat,
			lon : req.body.lon
        });

        station.save(function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating station',
                    error: err
                });
            }

            return res.status(201).json(station);
        });
    },

    /**
     * stationController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        StationModel.findOne({_id: id}, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting station',
                    error: err
                });
            }

            if (!station) {
                return res.status(404).json({
                    message: 'No such station'
                });
            }

            station.name_station = req.body.name_station ? req.body.name_station : station.name_station;
			station.lat = req.body.lat ? req.body.lat : station.lat;
			station.lon = req.body.lon ? req.body.lon : station.lon;
			
            station.save(function (err, station) {
                if (err) {
                    return res.status(500).json({
                        message: 'Error when updating station.',
                        error: err
                    });
                }

                return res.json(station);
            });
        });
    },

    /**
     * stationController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        StationModel.findByIdAndRemove(id, function (err, station) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the station.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    }
};
