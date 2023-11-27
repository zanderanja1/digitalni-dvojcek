var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var stationSchema = new Schema({
	'name_station' : Array,
	'lat' : Array,
	'lon' : Array
});

module.exports = mongoose.model('station', stationSchema);
