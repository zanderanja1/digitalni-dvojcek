var mongoose = require('mongoose');
var Schema   = mongoose.Schema;

var citySchema = new Schema({

	'name' : String,
	'temperature' : String,
	'humidity' : String,
	'wind' : String,
	'weatherStatus': String,
	'weather': {
		'byHour': [{
			'hourTime':Array,
			'hourTemperature':Array,
			'hourHumidity':Array,
			'hourWind':Array,
			'hourWeatherStatus':Array
		}],
		'byDay': [{
			'dayTime':Array,
			'dayTemperature':Array,
			'dayHumidity':Array,
			'dayWind':Array,
			'dayWeatherStatus':Array
		}],
	},
		
	'favouritesBy': {
		type: [Schema.Types.ObjectId],
		ref: 'user',
	}

});

module.exports = mongoose.model('city', citySchema);
