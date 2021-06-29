package com.mysunnyweather.android.logic

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.liveData
import com.mysunnyweather.android.logic.dao.PlaceDao
import com.mysunnyweather.android.logic.model.Place
import com.mysunnyweather.android.logic.model.Weather
import com.mysunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query:String)=fire(Dispatchers.IO){
        val placeResponse=SunnyWeatherNetwork.searchPlaces(query)
        Log.d("main","123213")

        if(placeResponse.status=="ok"){
            Log.d("main","placeResponse")
            val places=placeResponse.places
            Result.success(places)
        }else{
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng:String, lat:String)=fire(Dispatchers.IO){
        coroutineScope {
            val deferredRealtime=async {
                SunnyWeatherNetwork.getRealtimeWeather(lng,lat)
            }
            val deferredDaily=async {
                SunnyWeatherNetwork.getDailyWeather(lng,lat)
            }
            val realtimeResponse=deferredRealtime.await()
            val dailyResponse=deferredDaily.await()
            if(realtimeResponse.status=="ok" && dailyResponse.status=="ok"){
                val weather =Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                Result.success(weather)
            }else{
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse}"+
                                "daily response status is ${dailyResponse}"
                    )
                )
            }
        }
    }

    private fun<T> fire(context:CoroutineContext, block:suspend ()->Result<T>)=
        liveData<Result<T>>(context) {
            val result=try{
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place:Place)=PlaceDao.savePlace(place)

    fun getSavedPlace()=PlaceDao.getSavedPlace()

    fun isPlaceSaved()=PlaceDao.isPlaceSaved()

}