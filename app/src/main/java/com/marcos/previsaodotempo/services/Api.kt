package com.marcos.previsaodotempo.services

import com.marcos.previsaodotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?=q{city name}&appid={API key}
//c49d43bcadaa525509643f4330c34bd3

interface Api {

    @GET("weather")

    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") api_key: String
    ): Call<Main>
}