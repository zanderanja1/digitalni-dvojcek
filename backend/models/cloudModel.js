var mongoose = require("mongoose");
var Schema = mongoose.Schema;

var cloudSchema = new Schema({
  ratio: Number,
  weather: String,
  location: {
    latitude: String,
    longitude: String
  },
  date: Date,
  postedBy: String
});

module.exports = mongoose.model("clouds", cloudSchema);
