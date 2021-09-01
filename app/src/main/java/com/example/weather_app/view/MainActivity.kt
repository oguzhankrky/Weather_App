package com.example.weather_app.view

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    //view Binding uygulandı tema farklı yapıldı .

    private lateinit var viewModel: MainViewModel
    private lateinit var GET:SharedPreferences
    private lateinit var SET:SharedPreferences.Editor
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityMainBinding()

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()


        firstInitialviewModel()
        getLiveData()
        DoRefresh()
        whenPushSearchButton()


    }


    fun firstInitialviewModel(){
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        var cityname=GET.getString("cityName","İzmir")
        binding.editCityName.setText(cityname)
        if(cityname!=null)
            viewModel.refreshData(cityname)
    }


    fun Context.hideKeyboard(view: View) { //extra added when push button , keyboard is closed.
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun whenPushSearchButton()
    {
        binding.searchButton.setOnClickListener{
            var cityname = binding.editCityName.text.toString()
            SET.putString("cityName",cityname)
            SET.apply()
            hideKeyboard(binding.editCityName)  //extra added when push button , keyboard is closed.
            if(cityname!=null)
                viewModel.refreshData(cityname)
            getLiveData()
        }
    }

    private fun DoRefresh(){
        binding.refreshlayout.setOnRefreshListener {
            binding.llData.visibility=View.GONE
            binding.Error.visibility=View.GONE
            binding.Loading.visibility=View.GONE
            var cityname=GET.getString("cityName","İzmir")
            binding.cityName.setText(cityname)
            if(cityname!=null)
                viewModel.refreshData(cityname)
            binding.refreshlayout.isRefreshing=false

        }

    }

    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer { data ->
            data?.let {
                binding.llData.visibility = View.VISIBLE
                binding.cityCode.text = data.sys.country.toString()
                binding.cityName.text = data.name.toString()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(binding.weatherPictures)

                binding.degree.text = data.main.temp.toString() + "°C"

                binding.humidity.text = data.main.humidity.toString() + "%"
                binding.windSpeed.text = data.wind.speed.toString()
                binding.lat.text = data.coord.lat.toString()
                binding.lon.text = data.coord.lon.toString()

            }
        })

        viewModel.weather_loading.observe(this, Observer { load->
            load?.let{
                if(it){
                    binding.Loading.visibility=View.GONE
                    binding.Error.visibility=View.GONE
                    binding.llData.visibility=View.VISIBLE

                }
                else
                {
                    binding.Loading.visibility=View.GONE
                }

            }

        })
        viewModel.weather_error.observe(this,Observer { error ->
          error?.let {
           if(it){
               binding.Error.visibility=View.VISIBLE
               binding.llData.visibility=View.GONE
               binding.Loading.visibility=View.GONE

          }
          else{
               binding.Error.visibility=View.GONE

          }
        }
        })
    }

    private fun ActivityMainBinding()
    {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

}