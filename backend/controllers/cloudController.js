const { spawn } = require("child_process");

var CloudModel = require("../models/cloudModel.js");

/**
 * cloudController.js
 *
 * @description :: Server-side logic for managing clouds.
 */
module.exports = {
  /**
   * cloudController.list()
   */
  list: function (req, res) {
    CloudModel.find(function (err, clouds) {
      if (err) {
        return res.status(500).json({
          message: "Error when getting cloud.",
          error: err
        });
      }

      return res.json(clouds);
    });
  },

  /**
   * cloudController.show()
   */
  show: function (req, res) {
    var id = req.params.id;

    CloudModel.findOne({ _id: id }, function (err, cloud) {
      if (err) {
        return res.status(500).json({
          message: "Error when getting cloud.",
          error: err
        });
      }

      if (!cloud) {
        return res.status(404).json({
          message: "No such cloud"
        });
      }

      return res.json(cloud);
    });
  },

  /**
   * cloudController.create()
   */
  create: function (req, res) {
    let image = req.body.image;

    const python = spawn("python", ["./path_to_your_script.py"]);

    python.stdin.write(image);
    python.stdin.end();

    python.stdout.on("data", (data) => {
      // Get ratio from python script
      let ratio = parseFloat(data.toString());
      let weather = "Sunny";

      var cloud = new CloudModel({
        ratio: ratio,
        weather: weather,
        location: {
          latitude: req.body.latitude,
          longitude: req.body.longitude
        },
        date: Date.now(),
        postedBy: req.body.postedBy
      });

      cloud.save(function (err, cloud) {
        if (err) {
          return res.status(500).json({
            message: "Error when creating cloud",
            error: err
          });
        }

        return res.status(201).json(cloud);
      });
    });
  },

  remove: function (req, res) {
    var id = req.params.id;

    CloudModel.findByIdAndRemove(id, function (err, cloud) {
      if (err) {
        return res.status(500).json({
          message: "Error when deleting the cloud.",
          error: err
        });
      }

      return res.status(204).json();
    });
  }
};
