package com.example.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel: ViewModel() {
    val messageForActivity: MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    var listLTC: MutableLiveData<Array<Any>> =MutableLiveData<Array<Any>>()
    val listBTC: MutableLiveData<Array<Any>> =MutableLiveData<Array<Any>>()
}