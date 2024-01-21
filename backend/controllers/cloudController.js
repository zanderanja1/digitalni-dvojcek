const { spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

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
    try {
      let imagePath = path.resolve(__dirname, "..", "public", "images", req.file.filename);
      let imageFile = fs.readFileSync(imagePath);
      let base64Image = Buffer.from(imageFile).toString("base64");
      const python = spawn("python", ["../Recognizer/script.py"]);

      python.stdin.write(base64Image, () => {
        python.stdin.end();
      });

      python.stdout.on("data", (data) => {
        // Get ratio from python script
        let ratio = parseFloat(data.toString());
        let weather = "";
        if (ratio < 0.25) weather = "Sunny";
        else if (ratio < 0.5) weather = "Partly cloudy";
        else if (ratio < 0.75) weather = "Mostly cloudy";
        else weather = "Cloudy";
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
    } catch (e) {
      console.log(e);
    }
  },

  createSimulated: function (req, res) {
    console.log("Here");
    try {
      let ratio = req.body.ratio;
      if (ratio < 0.25) weather = "Sunny";
      else if (ratio < 0.5) weather = "Partly cloudy";
      else if (ratio < 0.75) weather = "Mostly cloudy";
      else weather = "Cloudy";
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
    } catch (e) {
      console.log(e);
    }
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
