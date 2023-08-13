package com.marcos.previsaodotempo.model

import com.google.gson.JsonObject

data class Main(
    val main: JsonObject,
    val sys: JsonObject,
    val weather: List<Weather>,
    val name: String
)
