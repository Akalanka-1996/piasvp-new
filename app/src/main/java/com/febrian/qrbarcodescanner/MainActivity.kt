package com.febrian.qrbarcodescanner

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.febrian.qrbarcodescanner.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.properties.Delegates

const val BASE_URL = "https://androidbarcodereader.herokuapp.com/api/"

class MainActivity : AppCompatActivity() {

    companion object {
        const val RESULT = "RESULT"
    }

    private lateinit var binding: ActivityMainBinding
    var myVariable by Delegates.notNull<Long>()

    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnTextScan.setOnClickListener{
            var detectText = "Click top button to take a photograph"

            tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                if(it == TextToSpeech.SUCCESS) {
                    tts.language = Locale.US
                    tts.setSpeechRate(1.0f)
                    tts.speak(detectText.toString(), TextToSpeech.QUEUE_ADD, null)
                }
            })
            var Intent = Intent(this, TextDetect::class.java)
            startActivity(Intent)
        }

        binding.btnScan.setOnClickListener {

            val intent = Intent(applicationContext, ScanActivity::class.java)
            startActivity(intent)
        }

        val result = intent.getStringExtra(RESULT)

        if (result != null) {
            if (result.contains("https://") || result.contains("http://")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                startActivity(intent)
            } else {
                binding.result.text = result.toString()
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                myVariable = result.toLong()

            }
        }

        if (result != null) {
            getMyData()
        }

    }

    override fun onStart() {
        super.onStart()
        var startText = "Select first button to scan barcode, or second button to detect text"

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(1.0f)
                tts.speak(startText.toString(), TextToSpeech.QUEUE_ADD, null)
            }
        })
    }

//    override fun onRestart() {
//        super.onRestart()
//        Toast.makeText(this, "abcd", Toast.LENGTH_SHORT).show()
//    }


    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData(myVariable)

        retrofitData.enqueue(object : Callback<QrDataItem?> {
            override fun onResponse(call: Call<QrDataItem?>, response: Response<QrDataItem?>) {
                if(response.isSuccessful) {
                    val productId = response.body()?.productId
                    val idTxt = productId.toString()
                    val productName = response.body()?.name.toString()
                    val description = response.body()?.description.toString()

                    val intent = Intent(applicationContext, TextOutput::class.java)
                    intent.putExtra("pid", idTxt)
                    intent.putExtra("pname", productName)
                    intent.putExtra("pdes", description)
                    startActivity(intent)
//
//                    binding.name.text = productName.toString()
//                    binding.description.text = description.toString()



//                    val productText = "The scanned product is,"
//                    val productDescription = "The Scanned product description is,"
//                    val pName =  productName.toString()
//                    val pDescription = description.toString()
//                    val combinedName = "$productText $pName"
//                    val combinedDescritption = "$productDescription $pDescription"
//                    val finalText = "$combinedName $combinedDescritption"
//
//                    tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
//                        if(it == TextToSpeech.SUCCESS) {
//                            tts.language = Locale.US
//                            tts.setSpeechRate(1.0f)
//                            tts.speak(finalText, TextToSpeech.QUEUE_ADD, null)
//                        }
//                    })
                }
            }

            override fun onFailure(call: Call<QrDataItem?>, t: Throwable) {

            }
        })

    }



}