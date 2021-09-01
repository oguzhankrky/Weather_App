package com.example.weather_app.service

import com.example.weather_app.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q=İzmir&appid=159fdd5b2702c6fce36d677339ab1c40
interface WeatherAPI {

    @GET("data/2.5/weather?&appid=159fdd5b2702c6fce36d677339ab1c40")
    fun getData(
        @Query("q") cityName:String
    ): Single<WeatherModel>
}