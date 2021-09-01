package com.example.weather_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.model.WeatherModel
import com.example.weather_app.service.WeatherAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel:ViewModel() {
    private val weatherApiService = WeatherAPIService()
    private val disposable = CompositeDisposable()

    val weather_data = MutableLiveData<WeatherModel>()
    val weather_error = MutableLiveData<Boolean>()
    val weather_loading = MutableLiveData<Boolean>()

    fun refreshData(cityName:String) {
        getDataFromAPI(cityName)
    }

    private fun getDataFromAPI(cityName:String) {

        weather_loading.value = true
        disposable.add(
            weatherApiService.getDataService(cityName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {

                    override fun onSuccess(wmData: WeatherModel) {
                        weather_data.value = wmData
                        weather_error.value = false
                        weather_loading.value = true  //can be change .

                    }

                    override fun onError(e: Throwable) {
                        weather_error.value = true
                        weather_loading.value = false

                    }

                })
        )

    }
}