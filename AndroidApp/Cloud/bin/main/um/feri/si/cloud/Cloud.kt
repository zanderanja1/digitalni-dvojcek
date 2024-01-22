package um.feri.si.cloud

import io.github.serpro69.kfaker.Faker

data class Cloud(
    var ratio: Double,
    var weather: String,
    var latitude: String,
    var longitude: String,
    var date: String,
    var postedBy: String,
    var id: String,
) {

}