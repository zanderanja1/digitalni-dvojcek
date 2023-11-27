var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var citySmallSchema = new Schema({
	'name_small' : Array,
	'temperature_small' : Array,
	'humidity_small' : Array,
	'wind_small' : Array,
	'pressure_small' : Array,
	'weatherStatus_small':Array,
});

module.exports = mongoose.model('citySmall', citySmallSchema);
