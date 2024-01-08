//import androidx.compose.ui.window.application
import com.google.gson.Gson
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.and
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.roundToInt


data class GeoapifyResponse(val results: List<Result>)
data class Result(val lat: Double, val lon: Double)

fun getCoordinates(cityName: String): Pair<Double, Double>? {
    val client = OkHttpClient().newBuilder()
        .build()
    val request: Request = Request.Builder()
        .url("https://api.geoapify.com/v1/geocode/search?text=$cityName&format=json&apiKey=203e839193a74400be37f2e1067c679c")
        .method("GET", null)
        .build()
    val response = client.newCall(request).execute()
    val responseBody = response.body?.string()

    responseBody?.let {
        val geoResult = Gson().fromJson(it, GeoapifyResponse::class.java)
        if (geoResult.results != null && geoResult.results.isNotEmpty()) {
            val result = geoResult.results[0]
            val lon = result.lon
            val lat = result.lat
            //println("Longitude: $lon, Latitude: $lat")
            return Pair(lat, lon)
        }
    }
    return null
}



//glavni podataki za posamezno mesto
fun getByHourByDay(firstUrl: String, secondUrl: String) {

    val client = OkHttpClient()

    var cityName = ""
    //val time = mutableListOf<String>() //3 dnevi naprej
    val hourTime = mutableListOf<String>() //Vse ure
    val hourTemperature = mutableListOf<String>() //Temperature
    val hourWeatherStatus = mutableListOf<String>() //vreme
    val percipe = mutableListOf<String>() //moznost dezja
    val hourWind = mutableListOf<String>()  //smer/moc vetra
    val humidity = mutableListOf<String>() //vlaznost + oblačnost
    val hourHumidity = mutableListOf<String>() //valznost
    val hourPercipitation = mutableListOf<String>()
    val newHourHumidity = mutableListOf<String>()

    //////////////////////////

    val dayTime = mutableListOf<String>() //10 dni naprej
    val dayTemperature = mutableListOf<String>()
    val dayHumidity = mutableListOf<String>()
    val dayWind = mutableListOf<String>()
    val dayWeatherStatus = mutableListOf<String>()
    val all = mutableListOf<String>()
    var cityTemp = ""
    var cityHumidity = ""
    var cityWind = ""
    var cityWeatherStatus = ""
    val cityAll = mutableListOf<String>()




    skrape(HttpFetcher) {
        request {

            //url = "https://weather.com/sl-SI/vreme/urozauro/l/6effff295cd78d21a1cb877db6a3e4ca01ffed2c9999763b90b56f481dd68e2b"
            url = firstUrl
        }

        response {

            htmlDocument {

                div {
                    withClass = "appWrapper"

                    main {
                        withId = "MainContent"

                        div {
                            withClass = "DaybreakLargeScreen--gridWrapper--3sleb"

                            main {
                                withClass = "region-main"

                                div {
                                    withClass = "removeIfEmpty"

                                    section {
                                        withClass = "card" and "Card--card--2AzRg" and "HourlyForecast--Card--1Xa5k"

                                        h1 {
                                            withClass = "LocationPageTitle--PageHeader--JBu5-"

                                            span {
                                                withClass = "LocationPageTitle--LocationText--5zskE"

                                                span {
                                                    withClass = "LocationPageTitle--PresentationName--1AMA6"

                                                    cityName = findFirst {
                                                        text.substringAfterLast(" ")
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

                details {
                    withClass =
                        "DaypartDetails--DayPartDetail--2XOOV" and "DaypartDetails--ctaShown--3JJj3" and "Disclosure--themeList--1Dz21"

                    summary {
                        withClass =
                            "Disclosure--Summary--3GiL4" and "DaypartDetails--Summary--3Fuya" and "Disclosure--hideBorderOnSummaryOpen--3_ZkO"

                        div {
                            withClass =
                                "DaypartDetails--DetailSummaryContent--1-r0i" and "Disclosure--SummaryDefault--2XBO9"

                            div {
                                withClass = "DetailsSummary--DetailsSummary--1DqhO"

                                h3 {
                                    withClass = "DetailsSummary--daypartName--kbngc"
                                    findAll { eachText.forEach { hourTime.add(it) } }
                                }

                                div {
                                    withClass = "DetailsSummary--temperature--1kVVp"
                                    span {
                                        withClass = "DetailsSummary--tempValue--jEiXE"
                                        findAll {
                                            eachText.forEach {
                                                val fahrenheit = it.replace("°", "").toInt()
                                                val celsius = ((fahrenheit - 32) * 5.0 / 9.0).roundToInt()
                                                val celsiusString = "$celsius°C"
                                                hourTemperature.add(celsiusString.removePrefix("-"))
                                            }
                                        }

                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--condition--2JmHb"

                                    span {
                                        withClass = "DetailsSummary--extendedData--307Ax"
                                        findAll { eachText.forEach { hourWeatherStatus.add(it) } }
                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--precip--1a98O"

                                    span {
                                        findAll { eachText.forEach { percipe.add(it) } }
                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--wind--1tv7t" and "DetailsSummary--extendedData--307Ax"
                                    span {
                                        withClass = "Wind--windWrapper--3Ly7c" and "undefined"

                                        findAll {
                                            eachText.forEach { wind ->
                                                val sloWind = when {
                                                    wind.contains("ENE") -> wind.replace("ENE","SV")
                                                    wind.contains("NE") -> wind.replace("NE","SV")
                                                    wind.contains("SE") -> wind.replace("SE","JZ")
                                                    wind.contains("S") -> wind.replace("S","J")
                                                    wind.contains("NNW") -> wind.replace("NNW","SZ")
                                                    wind.contains("NW") -> wind.replace("NW","SZ")
                                                    wind.contains("NSV") -> wind.replace("NSV","SV")
                                                    else -> wind
                                                }
                                                hourWind.add(sloWind)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ul {
                        withClass = "DetailsTable--DetailsTable--3Bt2T"

                        li {
                            withClass = "DetailsTable--listItem--Z-5Vi"
                            div {
                                withClass = "DetailsTable--field--CPpc_"

                                span {
                                    withClass = "DetailsTable--value--2YD0-"
                                    findAll {
                                        eachText.forEachIndexed { index, it ->
                                            if (it.contains("%")) {
                                                humidity.add(it)
                                                if (index % 2 == 1) {
                                                    hourHumidity.add(it)
                                                }
                                            } else if (it.contains("mm") || it.contains("cm")) {
                                                hourPercipitation.add(it.replace(" ", ""))
                                            }

                                        }

                                    }
                                }
                            }
                        }

                    }

                }

            }
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        request {
            //url = "https://vreme-si.com/slovenija/maribor"
            url = secondUrl

        }

        response {

            htmlDocument {

                div {
                    withClass = "content-box" and "weather-boxes-wrapper"


                    div {
                        withClass = "content-box-body"

                        div {
                            withClass = "weather-boxes-row"

                            div {
                                withClass = "weather-city"

                                div {
                                    withClass = "wc-temp"
                                    h3 {
                                        cityTemp = findFirst {
                                            text
                                        }
                                    }
                                }

                                div {
                                    withClass = "wc-info"

                                    findAll { eachText.forEach { cityAll.add(it) } }

                                }
                            }

                            table {
                                withClass = "weather-day-table"

                                tbody {
                                    tr {
                                        td {
                                            findAll { eachText.forEach { all.add(it) } }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }


                for (element in all) {
                    if (element.contains("°C") && !element.contains("UV") && !element[0].isUpperCase()) {
                        dayTemperature.add(element)
                    } else if (element.contains("km/h")) {
                        dayWind.add(element)
                    } else if (element.contains(".")) {
                        val modifiedElement = element.substringBeforeLast(".") + '.'
                        dayTime.add(modifiedElement)
                    } else if (element.contains("%")) {
                        dayHumidity.add(element)
                    } else if (element[0].isUpperCase()) {
                        var modified = ""
                        modified = if (element.contains("dež")) {
                            element.substringBefore(" ").trim() + " " + element.substringAfter(" ").substringBefore(" ")
                                .trim()
                        } else {
                            element.substringBefore(" ")
                        }
                        dayWeatherStatus.add(modified)
                    }
                }

                for (element in cityAll) {

                    cityWeatherStatus = element.substringBefore(" ")
                    cityHumidity = element.substringAfter("Vlažnost").substringBefore("%").trim() + '%'
                    cityWind = element.substringAfter("mb").substringBefore("Občutek").trim()


                }

                val coordinates = getCoordinates(cityName)
                var latitude = 0.0
                var longitude = 0.0
                if (coordinates != null) {
                    latitude = coordinates.first
                    longitude = coordinates.second
                }

                                val json = """
                                    {
                                        "name": "$cityName",
                                        "temperature": "$cityTemp",
                                        "humidity": "$cityHumidity",
                                        "wind": "$cityWind",
                                        "weatherStatus": "$cityWeatherStatus",
                                        "weather": {
                                            "byHour": [
                                                {
                                                    "hourTime": ${hourTime.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourTemperature": ${hourTemperature.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourHumidity": ${hourPercipitation.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourWind": ${hourWind.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourWeatherStatus": ${hourWeatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }}
                                                }
                                                    ],
                                            "byDay": [
                                                {
                                            "dayTime": ${dayTime.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayTemperature": ${dayTemperature.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayHumidity": ${dayHumidity.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayWind": ${dayWind.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayWeatherStatus": ${dayWeatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }}
                                                }
                                            ]
                                        },
                                        "favouritesBy": [],
                                        "latitude": "$latitude",
                                        "longitude": "$longitude"
                                }
                                """.trimIndent()


                                val requestBody = json.toRequestBody("application/json".toMediaType())

                                // create a POST request with the request body
                                val request = Request.Builder().url("http://localhost:3001/cities").post(requestBody).build()

                                // execute the request
                                val response = client.newCall(request).execute()


                                val responseBody = response.body?.string()

                                println(responseBody)

            }
        }


    }
}


//updatanje getCitySmall funkcije
fun putCitySmall(id: String) {

    val client = OkHttpClient()

    skrape(HttpFetcher) {
        request {
            url = "https://vreme-si.com/slovenija/"
        }

        response {
            println("http status code: ${status { code }}")
            println("http status message: ${status { message }}")
            println("Content-Type: $contentType")

            htmlDocument {
                val all = mutableListOf<String>()
                val names = mutableListOf<String?>()
                val temps = mutableListOf<String?>()
                val humidity = mutableListOf<String?>()
                val wind = mutableListOf<String?>()
                val weatherStatus = mutableListOf<String?>()
                val pressure = mutableListOf<String?>()


                section {
                    withClass = "weather-main"

                    div {
                        withClass = "content-box" and "weather-boxes-wrapper"

                        div {
                            withClass = "content-box-body"

                            div {
                                withClass = "weather-boxes-row"

                                table {
                                    tbody {
                                        tr {
                                            td {

                                                findAll {

                                                    eachText.forEachIndexed { index, value ->
                                                        if (value.isNotEmpty()) {
                                                            all.add(value)
                                                        } else {
                                                            all.add("null")
                                                        }
                                                    }
                                                }


                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }

                for (i in all.indices) {

                    if (i > 113) {
                        break
                    }
                    if (i % 6 == 0) {
                        val text = all[i]
                        names.add(text)

                    } else if (i % 6 == 1) {
                        val text = all[i]
                        weatherStatus.add(text)
                    } else if (i % 6 == 2) {
                        val text = all[i]
                        temps.add(text)
                    } else if (i % 6 == 3) {
                        val text = all[i]
                        humidity.add(text)
                    } else if (i % 6 == 4) {
                        val text = all[i]
                        pressure.add(text)
                    } else {
                        val text = all[i]
                        wind.add(text)
                    }


                }

                val json = """
                    {
                        "name_small": ${names.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "temperature_small": ${temps.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "humidity_small": ${humidity.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "wind_small": ${wind.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "pressure_small": ${pressure.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "weatherStatus_small": ${weatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }}
                    }
                """.trimIndent()


                val requestBody = json.toRequestBody("application/json".toMediaType())

                // create a POST request with the request body
                val request = Request.Builder().url("http://localhost:3001/citiesSmall/$id").put(requestBody).build()

                // execute the request
                val response = client.newCall(request).execute()

                // print the response body
                println(response.body?.string())

            }


        }

    }


}


fun putByHourByDay(id:String,firstUrl: String, secondUrl: String) {

    val client = OkHttpClient()

    var cityName = ""
    //val time = mutableListOf<String>() //3 dnevi naprej
    val hourTime = mutableListOf<String>() //Vse ure
    val hourTemperature = mutableListOf<String>() //Temperature
    val hourWeatherStatus = mutableListOf<String>() //vreme
    val percipe = mutableListOf<String>() //moznost dezja
    val hourWind = mutableListOf<String>()  //smer/moc vetra
    val humidity = mutableListOf<String>() //vlaznost + oblačnost
    val hourHumidity = mutableListOf<String>() //valznost
    val hourPercipitation = mutableListOf<String>()


    //////////////////////////

    val dayTime = mutableListOf<String>() //10 dni naprej
    val dayTemperature = mutableListOf<String>()
    val dayHumidity = mutableListOf<String>()
    val dayWind = mutableListOf<String>()
    val dayWeatherStatus = mutableListOf<String>()
    val all = mutableListOf<String>()
    var cityTemp = ""
    var cityHumidity = ""
    var cityWind = ""
    var cityWeatherStatus = ""
    val cityAll = mutableListOf<String>()




    skrape(HttpFetcher) {
        request {

            //url = "https://weather.com/sl-SI/vreme/urozauro/l/6effff295cd78d21a1cb877db6a3e4ca01ffed2c9999763b90b56f481dd68e2b"
            url = firstUrl
        }

        response {

            htmlDocument {

                div {
                    withClass = "appWrapper"

                    main {
                        withId = "MainContent"

                        div {
                            withClass = "DaybreakLargeScreen--gridWrapper--3sleb"

                            main {
                                withClass = "region-main"

                                div {
                                    withClass = "removeIfEmpty"

                                    section {
                                        withClass = "card" and "Card--card--2AzRg" and "HourlyForecast--Card--1Xa5k"

                                        h1 {
                                            withClass = "LocationPageTitle--PageHeader--JBu5-"

                                            span {
                                                withClass = "LocationPageTitle--LocationText--5zskE"

                                                span {
                                                    withClass = "LocationPageTitle--PresentationName--1AMA6"

                                                    cityName = findFirst {
                                                        text.substringAfterLast(" ")
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }



                details {
                    withClass =
                        "DaypartDetails--DayPartDetail--2XOOV" and "DaypartDetails--ctaShown--3JJj3" and "Disclosure--themeList--1Dz21"

                    summary {
                        withClass =
                            "Disclosure--Summary--3GiL4" and "DaypartDetails--Summary--3Fuya" and "Disclosure--hideBorderOnSummaryOpen--3_ZkO"

                        div {
                            withClass =
                                "DaypartDetails--DetailSummaryContent--1-r0i" and "Disclosure--SummaryDefault--2XBO9"

                            div {
                                withClass = "DetailsSummary--DetailsSummary--1DqhO"

                                h3 {
                                    withClass = "DetailsSummary--daypartName--kbngc"
                                    findAll { eachText.forEach { hourTime.add(it) } }
                                }

                                div {
                                    withClass = "DetailsSummary--temperature--1kVVp"
                                    span {
                                        withClass = "DetailsSummary--tempValue--jEiXE"
                                        findAll {
                                            eachText.forEach {
                                                val fahrenheit = it.replace("°", "").toInt()
                                                val celsius = ((fahrenheit - 32) * 5.0 / 9.0).roundToInt()
                                                val celsiusString = "$celsius°C"
                                                hourTemperature.add(celsiusString.removePrefix("-"))
                                            }
                                        }

                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--condition--2JmHb"

                                    span {
                                        withClass = "DetailsSummary--extendedData--307Ax"
                                        findAll { eachText.forEach { hourWeatherStatus.add(it) } }
                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--precip--1a98O"

                                    span {
                                        findAll { eachText.forEach { percipe.add(it) } }
                                    }
                                }

                                div {
                                    withClass = "DetailsSummary--wind--1tv7t" and "DetailsSummary--extendedData--307Ax"
                                    span {
                                        withClass = "Wind--windWrapper--3Ly7c" and "undefined"

                                        findAll {
                                            eachText.forEach { wind ->
                                                val sloWind = when {
                                                    wind.contains("ENE") -> wind.replace("ENE","SV")
                                                    wind.contains("NE") -> wind.replace("NE","SV")
                                                    wind.contains("SE") -> wind.replace("SE","JZ")
                                                    wind.contains("S") -> wind.replace("S","J")
                                                    wind.contains("NNW") -> wind.replace("NNW","SZ")
                                                    wind.contains("NW") -> wind.replace("NW","SZ")
                                                    wind.contains("NSV") -> wind.replace("NSV","SV")
                                                    else -> wind
                                                }
                                                hourWind.add(sloWind)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ul {
                        withClass = "DetailsTable--DetailsTable--3Bt2T"

                        li {
                            withClass = "DetailsTable--listItem--Z-5Vi"

                            div {
                                withClass = "DetailsTable--field--CPpc_"

                                span {
                                    withClass = "DetailsTable--value--2YD0-"
                                    findAll {
                                        eachText.forEach {
                                             if (it.contains("mm") || it.contains("cm")) {
                                                hourPercipitation.add(it)
                                            }

                                        }

                                    }
                                }
                            }
                        }

                    }

                }

            }
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        request {
            //url = "https://vreme-si.com/slovenija/maribor"
            url = secondUrl

        }

        response {

            htmlDocument {

                div {
                    withClass = "content-box" and "weather-boxes-wrapper"


                    div {
                        withClass = "content-box-body"

                        div {
                            withClass = "weather-boxes-row"

                            div {
                                withClass = "weather-city"

                                div {
                                    withClass = "wc-temp"
                                    h3 {
                                        cityTemp = findFirst {
                                            text
                                        }
                                    }
                                }

                                div {
                                    withClass = "wc-info"

                                    findAll { eachText.forEach { cityAll.add(it) } }

                                }
                            }

                            table {
                                withClass = "weather-day-table"

                                tbody {
                                    tr {
                                        td {
                                            findAll { eachText.forEach { all.add(it) } }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }


                for (element in all) {
                    if (element.contains("°C") && !element.contains("UV") && !element[0].isUpperCase()) {
                        dayTemperature.add(element)
                    } else if (element.contains("km/h")) {
                        dayWind.add(element)
                    } else if (element.contains(".")) {
                        val modifiedElement = element.substringBeforeLast(".") + '.'
                        dayTime.add(modifiedElement)
                    } else if (element.contains("%")) {
                        dayHumidity.add(element)
                    } else if (element[0].isUpperCase()) {
                        var modified = ""
                        modified = if (element.contains("dež")) {
                            element.substringBefore(" ").trim() + " " + element.substringAfter(" ").substringBefore(" ")
                                .trim()
                        } else {
                            element.substringBefore(" ")
                        }
                        dayWeatherStatus.add(modified)
                    }
                }

                for (element in cityAll) {

                    cityWeatherStatus = element.substringBefore(" ")
                    cityHumidity = element.substringAfter("Vlažnost").substringBefore("%").trim() + '%'
                    cityWind = element.substringAfter("mb").substringBefore("Občutek").trim()


                }

                            val json = """
                                    {
                                        "name": "$cityName",
                                        "temperature": "$cityTemp",
                                        "humidity": "$cityHumidity",
                                        "wind": "$cityWind",
                                        "weatherStatus": "$cityWeatherStatus",
                                        "weather": {
                                            "byHour": [
                                                {
                                                    "hourTime": ${hourTime.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourTemperature": ${hourTemperature.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourHumidity": ${hourHumidity.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourWind": ${hourWind.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                                    "hourHumidity": ${hourPercipitation.map { "\"$it\"" }},
                                                    "hourWeatherStatus": ${hourWeatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }}
                                                }
                                                    ],
                                            "byDay": [
                                                {
                                            "dayTime": ${dayTime.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayTemperature": ${dayTemperature.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayHumidity": ${dayHumidity.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayWind": ${dayWind.map { "\"${it.replace("\"", "\\\"")}\"" }},
                                            "dayWeatherStatus": ${dayWeatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }}
                                                }
                                            ]
                                        }
                                }
                                """.trimIndent()


                val requestBody = json.toRequestBody("application/json".toMediaType())

                // create a POST request with the request body
                val request = Request.Builder().url("http://localhost:3001/cities/$id").put(requestBody).build()

                // execute the request
                val response = client.newCall(request).execute()


                val responseBody = response.body?.string()

                println(responseBody)


            }
        }


    }
}

//vsa mesta osnovno
fun getCitySmall() {
    val _id = ""
    var isFirstRun = false
    val client = OkHttpClient()

    skrape(HttpFetcher) {
        request {
            url = "https://vreme-si.com/slovenija/"
        }

        response {
            println("http status code: ${status { code }}")
            println("http status message: ${status { message }}")
            println("Content-Type: $contentType")

            htmlDocument {
                val all = mutableListOf<String>()
                val names = mutableListOf<String?>()
                val temps = mutableListOf<String?>()
                val humidity = mutableListOf<String?>()
                val wind = mutableListOf<String?>()
                val weatherStatus = mutableListOf<String?>()
                val pressure = mutableListOf<String?>()
                val latitude = mutableListOf<Double>()
                val longitude = mutableListOf<Double>()


                section {
                    withClass = "weather-main"

                    div {
                        withClass = "content-box" and "weather-boxes-wrapper"

                        div {
                            withClass = "content-box-body"

                            div {
                                withClass = "weather-boxes-row"

                                table {
                                    tbody {
                                        tr {
                                            td {

                                                findAll {

                                                    eachText.forEachIndexed { index, value ->
                                                        if (value.isNotEmpty()) {
                                                            all.add(value)
                                                        } else {
                                                            all.add("null")
                                                        }
                                                    }
                                                }


                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }

                for (i in all.indices) {

                    if (i > 113) {
                        break
                    }
                    if (i % 6 == 0) {
                        val text = all[i]
                        names.add(text)

                    } else if (i % 6 == 1) {
                        val text = all[i]
                        weatherStatus.add(text)
                    } else if (i % 6 == 2) {
                        val text = all[i]
                        temps.add(text)
                    } else if (i % 6 == 3) {
                        val text = all[i]
                        humidity.add(text)
                    } else if (i % 6 == 4) {
                        val text = all[i]
                        pressure.add(text)
                    } else {
                        val text = all[i]
                        wind.add(text)
                    }


                }

                for(name in names) {
                    val coordinates = getCoordinates(name!!)
                    if (coordinates != null) {
                        latitude.add(coordinates.first)
                        longitude.add(coordinates.second)
                    } else {
                        latitude.add(0.0)
                        longitude.add(0.0)
                    }

                }

                val json = """
                    {
                        "name_small": ${names.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "temperature_small": ${temps.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "humidity_small": ${humidity.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "wind_small": ${wind.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "pressure_small": ${pressure.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "weatherStatus_small": ${weatherStatus.map { "\"${it?.replace("\"", "\\\"")}\"" }},
                        "latitude": ${latitude.map { "\"$it\"" }},
                        "longitude": ${longitude.map { "\"$it\"" }}
                    }
                """.trimIndent()


                val requestBody = json.toRequestBody("application/json".toMediaType())

                // create a POST request with the request body
                val request = Request.Builder().url("http://localhost:3001/citiesSmall").post(requestBody).build()

                // execute the request
                val response = client.newCall(request).execute()


                val responseBody = response.body?.string()

                println(responseBody)

                /*val gson = Gson()
                val jsonResponse = gson.fromJson(responseBody, JsonElement::class.java)
                _id = jsonResponse.asJsonObject["_id"].asString

                 */


            }


        }

    }

}


fun main(){

    val map: MutableMap<String, Pair<String, String>> = mutableMapOf()

    map["659bf9766c8d8883e0f2c51d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/6effff295cd78d21a1cb877db6a3e4ca01ffed2c9999763b90b56f481dd68e2b","https://vreme-si.com/slovenija/maribor") //Maribor
    map["659bf9776c8d8883e0f2c521"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/f54b0191fd7be8763d2579fea3bb89e969220183cd4bfeef6141ff9d7b1143f4","https://vreme-si.com/slovenija/ljubljana") //Ljubljana
    map["659bf9786c8d8883e0f2c525"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/84ba8d3df448605f61e4e12f9d8436b9470004c53b8c50c6e5f5fb719e9fb0fc","https://vreme-si.com/slovenija/ptuj") //Ptuj
    map["659bf97c6c8d8883e0f2c529"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/1c59ed2f51f58f5d99e8595146bab6cbbb725594fc9e0df461601ac307237965","https://vreme-si.com/slovenija/koper") //Koper
    map["659bf97e6c8d8883e0f2c52d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/dc3e7a724bc4c2e5c2f8dacbde6f24917891b8e85f39baf8dc0b9ede0622e9a0","https://vreme-si.com/slovenija/celje") //Celje
    map["659bf97f6c8d8883e0f2c531"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/fd1b12362c04486a8a92153a2bfa58de19e4890a0dfc9f803fea0732896a0493","https://vreme-si.com/slovenija/kranj") //Kranj
    map["659bf9826c8d8883e0f2c535"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/ef1a4d543dd7ae383ff4d22dcc1f4576eba9eaa07772cf1186606a788e530d34","https://vreme-si.com/slovenija/kamnik") //Kamnik
    map["659bf9856c8d8883e0f2c539"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/e7759966f76b821e20a1507d945874eb137dd4803578c66d2f73fe1844ef507d","https://vreme-si.com/slovenija/ajdovscina") //Ajdovščina

    map["659bf9876c8d8883e0f2c53d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/cf357e3c439ae69c87bcce8fea7536f4420e950d972dbcafb925267613027605","https://vreme-si.com/slovenija/bled")
    map["659bf9886c8d8883e0f2c541"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/1eef3b264101239e6fecafaa33e1bb0b797667ab4f30cf427c2be09021a3374f","https://vreme-si.com/slovenija/crnomelj")
    map["659bf98b6c8d8883e0f2c545"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/f87b31177478e58ccf5f133bb8c0d3ce078f39e0fbc9b2444befddef123a51c1","https://vreme-si.com/slovenija/idrija")
    map["659bf98c6c8d8883e0f2c549"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/b572691eb04ecb6ec764a253ed34d65c0c0dc0fb3efc1cf44a4009ca9728b2af","https://vreme-si.com/slovenija/izola")

    map["659bf98d6c8d8883e0f2c54d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/53451b46ca052568fb422badb9c8ebf81cdcd75443d78643fd1e73015856828b","https://vreme-si.com/slovenija/velenje")
    map["659bf98f6c8d8883e0f2c551"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/94976a7883747e1f149aac64268ca7ff77e41eceba589193d73b8c1bde1b0483","https://vreme-si.com/slovenija/jesenice")
    map["659bf9906c8d8883e0f2c555"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/2271ba69261a6d325b9c33ed69dbd591ef13f964c8753b67f033f8c57561b89e","https://vreme-si.com/slovenija/kocevje")
    map["659bf9916c8d8883e0f2c559"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/bdd02e7a155405bf8220e2d35a94f7e2ba189b6c8172e9f3c2bc921a9c703517","https://vreme-si.com/slovenija/lendava")
    map["659bf9936c8d8883e0f2c55d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/709347608a5d5ea0f2890b90997c316cf1c9745ebbd3c8911de0ff26b247d060","https://vreme-si.com/slovenija/ormoz")
    map["659bf9966c8d8883e0f2c561"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/fe71c3edc65b147a0c5ca5f17c11044b9f2a30e82d79feba76203f3b00cf5bd2","https://vreme-si.com/slovenija/piran")
    map["659bf9976c8d8883e0f2c565"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/108ad8103bb5aab753d42fee3620069bccbf426334f61f3dde02a37fa7ceee0e","https://vreme-si.com/slovenija/portoroz")
    map["659bf9986c8d8883e0f2c569"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/059f0f207827621a1dacd467a595c4051ca2ee7b040ec13abf203df36c30c2cb","https://vreme-si.com/slovenija/postojna")
    map["659bf99a6c8d8883e0f2c56d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/d007e506f7c8950b788f2c772f4c39a96adceedcca45dccb1b052ad029b3d99e","https://vreme-si.com/slovenija/sevnica")
    map["659bf99b6c8d8883e0f2c571"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/49df28b589c1e9ba1529f4ed81ccd6a78cea42554570f5917014ef84412e6ff2","https://vreme-si.com/slovenija/tolmin")
    map["659bf99c6c8d8883e0f2c575"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/7a1a9b0b5d3b1c7955e98b35cd5d308e9f0bde39b022e9b8d3b699afac147c0a","https://vreme-si.com/slovenija/trbovlje")
    map["659bf99d6c8d8883e0f2c579"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/53451b46ca052568fb422badb9c8ebf81cdcd75443d78643fd1e73015856828b","https://vreme-si.com/slovenija/velenje")
    map["659bf99e6c8d8883e0f2c57d"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/434fb85ad782ae587daf865371bd1aa8bcb81ba773e72082be75047de6086543","https://vreme-si.com/slovenija/vrhnika")
    map["659bf99f6c8d8883e0f2c581"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/9f28e80fab42e6cca8044f937df836a826274e76fac1eaaa27be72d822ee17a7","https://vreme-si.com/slovenija/ankaran")
    map["659bf9a16c8d8883e0f2c585"] = Pair("https://weather.com/sl-SI/vreme/urozauro/l/4d72b666f6fe7e62aa3f37bc9381f4ada9ea4d856c0ae6d1b2900206706167b1","https://vreme-si.com/slovenija/bohinj")

    for ((id, urls) in map) {
        val url1 = urls.first
        val url2 = urls.second
/*
        println("ID: $id")
        println("URL 1: $url1")
        println("URL 2: $url2")
        */
        putByHourByDay(id,url1,url2)
        //getByHourByDay(url1, url2)
    }

   // getByHourByDay(map["65646d32f4820f2cc262106b"]!!.first, map["65646d32f4820f2cc262106b"]!!.second)

    //getCitySmall()


    putCitySmall("659bf51c3890356b371534a3")

    //getByHourByDay("https://weather.com/sl-SI/vreme/urozauro/l/4d72b666f6fe7e62aa3f37bc9381f4ada9ea4d856c0ae6d1b2900206706167b1","https://vreme-si.com/slovenija/bohinj")
    //putByHourByDay("6470e9c2003f5838aea6fdd9","https://weather.com/sl-SI/vreme/urozauro/l/6effff295cd78d21a1cb877db6a3e4ca01ffed2c9999763b90b56f481dd68e2b","https://vreme-si.com/slovenija/maribor")

    //putCitySmall("6467abd1a982872301c5ba8d")


    //update citySmall vsako uro
    /*
    val executorService = Executors.newScheduledThreadPool(1)

    val task = Runnable {
        putCitySmall("6463857fa95e8024a40f183b")
    }

    executorService.scheduleAtFixedRate(task ,0,1,TimeUnit.HOURS)

    while (true){

    }

 */

    //getByHourByDay("https://weather.com/sl-SI/vreme/urozauro/l/6effff295cd78d21a1cb877db6a3e4ca01ffed2c9999763b90b56f481dd68e2b","https://vreme-si.com/slovenija/maribor")
}
