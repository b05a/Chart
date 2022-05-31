package com.example.chart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.chart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var arrayListCrypto= mutableMapOf<String, Array<Any>>()
    var minRequest=60
    private val dataModel:DataModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (dataModel.listLTC.value!=null){
            arrayListCrypto["LTC"] = dataModel.listLTC.value!!
            arrayListCrypto["BTC"] = dataModel.listBTC.value!!
            chartsOpen("LTC",arrayListCrypto["LTC"]!!)
        } else{
            startChart()
        }

        binding.button.setOnClickListener {
            try {
                chartsOpen("LTC", arrayListCrypto["LTC"]!!)
            }catch (e:Exception){Toast.makeText(applicationContext,"Обновите данные",Toast.LENGTH_SHORT).show()}
        }

        binding.button2.setOnClickListener {
            try {
                chartsOpen("BTC", arrayListCrypto["BTC"]!!)
            }catch (e:Exception){Toast.makeText(applicationContext,"Обновите данные",Toast.LENGTH_SHORT).show()}
        }

        binding.button3.setOnClickListener {
            var arrayBTCLTC= arrayOf<Any>()
            var a:Double=0.0
            var b:Double=0.0
            try{
                for (i in 0..minRequest){
                    a=arrayListCrypto["LTC"]!![i].toString().toDouble()
                    b =arrayListCrypto["BTC"]!![i].toString().toDouble()
                    arrayBTCLTC+=a/b
                }
                chartsOpen("LB", arrayBTCLTC)
            }catch (e:Exception){Toast.makeText(applicationContext,"Обновите данные",Toast.LENGTH_SHORT).show()}

        }

        binding.refresh.setOnClickListener {
            Toast.makeText(applicationContext,"Refresh",Toast.LENGTH_SHORT).show()
            requestCrypto("LTC")
            requestCrypto("BTC")
        }

    }

    override fun onDestroy() {
        println(dataModel.listLTC.value!![0])
        super.onDestroy()
    }

    private fun startChart() {
        val aaChartView : AAChartView = binding.aaChartView
        var aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("")
            .backgroundColor("#e6f2d3")
            .dataLabelsEnabled(false)
            .yAxisTitle("USD")
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun requestCrypto(name:String){
        CoroutineScope(Dispatchers.IO).async {
            var array=arrayOf<Any>()
            var jsonRequest=""
            try {
                jsonRequest = URL("https://min-api.cryptocompare.com/data/v2/histominute?fsym=$name&tsym=USD&limit=60").readText()
            }catch (e:Exception){Toast.makeText(applicationContext, "Нет интернета", Toast.LENGTH_SHORT).show()}

            var d = JSONObject(jsonRequest).getJSONObject("Data").getJSONArray("Data")
            var i =0
            var price=0.0
            while (i<minRequest+1){
                price=d.getJSONObject(i).getDouble("close")
                array+=price
                i++
            }
            arrayListCrypto[name] = array

            if (name=="LTC"){
                runOnUiThread {
                    chartsOpen(name, array)
                    if(name=="LTC"){dataModel.listLTC.value=array}
                }
            }
            if(name=="BTC"){runOnUiThread { dataModel.listBTC.value=array }}
        }
    }

    private fun chartsOpen(name:String, array: Array<Any>) {
        val aaChartView : AAChartView = binding.aaChartView
        var aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title(name)
            .backgroundColor("#e6f2d3")
            .dataLabelsEnabled(false)
            .yAxisTitle("USD")
            aaChartModel.series(arrayOf(
                AASeriesElement()
                    .name("Price $name")
                    .data(array)
                    .color("#4c7fc2")
            )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}