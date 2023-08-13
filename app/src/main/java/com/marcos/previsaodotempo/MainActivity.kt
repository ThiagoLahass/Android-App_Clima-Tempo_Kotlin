package com.marcos.previsaodotempo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marcos.previsaodotempo.constantes.Const
import com.marcos.previsaodotempo.databinding.ActivityMainBinding
import com.marcos.previsaodotempo.model.Main
import com.marcos.previsaodotempo.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#396BCB")

        binding.trocarTema.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){ //Tema Escuro - Dark Mode
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#000000"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_escuro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#000000"))
                window.statusBarColor = Color.parseColor("#000000")
            }else{ //Tema Claro
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#396BCB"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_claro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#FFFFFF"))
                window.statusBarColor = Color.parseColor("#396BCB")
            }
        }

        binding.btBuscar.setOnClickListener {

            val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView = currentFocus
            if (focusedView != null) {
                inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }


            val cidade = binding.editBuscarCidade.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()
                .create(Api::class.java)

            retrofit.weatherMap(cidade,Const.API_KEY).enqueue(object : Callback<Main>{
                override fun onResponse(call: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful){
                        respostaServidor(response)
                    }else{
                        Toast.makeText(applicationContext,"Cidade inválida!",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Main>, t: Throwable) {
                    Toast.makeText(applicationContext,"Erro fatal de servidor!",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

            })
        }


    }

    override fun onResume() {
        super.onResume()

        binding.progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(Api::class.java)

        retrofit.weatherMap("São Paulo",Const.API_KEY).enqueue(object : Callback<Main>{
            override fun onResponse(call: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful){
                    respostaServidor(response)
                }else{
                    Toast.makeText(applicationContext,"Cidade inválida!",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Main>, t: Throwable) {
                Toast.makeText(applicationContext,"Erro fatal de servidor!",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }

        })


    }

    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>){

        val main = response.body()!!.main
        val temp = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        val sys = response.body()!!.sys
        val country = sys.get("country").asString
        var pais = ""

        val weather = response.body()!!.weather
        val main_weather = weather[0].main
        val description = weather[0].description

        val name = response.body()!!.name

        //Converter Kelvin em Graus Celsius - Fórmula: 298 - 273,15 = 26,85C
        val temp_c = (temp.toDouble() - 273.15)
        val tempMin_c = (tempMin.toDouble() - 273.15)
        val tempMax_c = (tempMax.toDouble() - 273.15)
        val decimalFormat = DecimalFormat("00")

        if (country.equals("BR")){
           pais = "Brasil"
        }else if (country.equals("US")){
           pais = "Estados Unidos"
        }

        if (main_weather.equals("Clouds") && description.equals("few clouds")){
           binding.imgClima.setBackgroundResource(R.drawable.flewclouds)
        }else if (main_weather.equals("Clouds") && description.equals("scattered clouds")){
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        }else if (main_weather.equals("Clouds") && description.equals("broken clouds")){
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        }else if (main_weather.equals("Clouds") && description.equals("overcast clouds")){
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        }else if (main_weather.equals("Clear") && description.equals("clear sky")){
            binding.imgClima.setBackgroundResource(R.drawable.clearsky)
        }else if (main_weather.equals("Snow")){
            binding.imgClima.setBackgroundResource(R.drawable.snow)
        }else if (main_weather.equals("Rain")){
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        }else if (main_weather.equals("Drizzle")){
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Thunderstorm")){
            binding.imgClima.setBackgroundResource(R.drawable.trunderstorm)
        }
        else{
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        }

        val descricaoClima = when(description){
            "clear sky" -> {
                "Céu limpo"
            }
            "few clouds" -> {
                "Poucas nuvens"
            }
            "scattered clouds" -> {
                "Nuvens dispersas"
            }
            "broken clouds" -> {
                "Nuvens quebradas"
            }
            "shower rain" -> {
                "chuva de banho"
            }
            "rain" -> {
                "Chuva"
            }
            "thunderstorm" -> {
                "Tempestade"
            }
            "snow" -> {
                "Neve"
            }
            else -> {
                "Névoa"
            }
        }

        binding.txtTemperatura.setText("${decimalFormat.format(temp_c)}°C")
        binding.txtPaisCidade.setText("$pais - $name")

        binding.txtInformacoes1.setText("Clima \n $descricaoClima \n\n Umidade \n $humidity%")
        binding.txtInformacoes2.setText("Temp.Min \n ${decimalFormat.format(tempMin_c)}°C \n\n Temp.Max \n ${decimalFormat.format(tempMax_c)}°C")

        binding.progressBar.visibility = View.GONE
    }
}