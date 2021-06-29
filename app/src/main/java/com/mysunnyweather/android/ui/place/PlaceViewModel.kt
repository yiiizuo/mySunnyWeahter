package com.mysunnyweather.android.ui.place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mysunnyweather.android.logic.Repository
import com.mysunnyweather.android.logic.model.Place

class PlaceViewModel :ViewModel(){
    private val searchLiveData= MutableLiveData<String>()

    val placeList=ArrayList<Place>()

    val placeLiveData=Transformations.switchMap(searchLiveData){query->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query:String){
        searchLiveData.value=query
    }

    fun savePlace(place: Place)=Repository.savePlace(place)

    fun getSavedPlace()=Repository.getSavedPlace()

    fun isPlaceSaved()=Repository.isPlaceSaved()
}