package com.febrian.qrbarcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.properties.Delegates

class TextOutput : AppCompatActivity() {

    var myVariable by Delegates.notNull<Long>()
    lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_output)

        val result = intent.getStringExtra(MainActivity.RESULT)

        if (result != null) {
            if (result.contains("https://") || result.contains("http://")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                startActivity(intent)
            } else {
//                binding.result.text = result.toString()
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                myVariable = result.toLong()


            }
        }

        if (result != null) {
            getMyData()
        }

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData(myVariable)

        retrofitData.enqueue(object : Callback<QrDataItem?> {
            override fun onResponse(call: Call<QrDataItem?>, response: Response<QrDataItem?>) {
                if (response.isSuccessful) {
                    val productId = response.body()?.productId
                    val idTxt = productId.toString()
                    val productName = response.body()?.name.toString()
                    val description = response.body()?.description.toString()

                    val result = findViewById<TextView>(R.id.result)
                    val pName = findViewById<TextView>(R.id.name)
                    val pdes = findViewById<TextView>(R.id.description)

                    result.text = idTxt.toString()
                    pName.text = productName.toString()
                    pdes.text = description.toString()


                    val productText = "The scanned product is,"
                    val productDescription = "The Scanned product description is,"
                    val combinedName = "$productText $productName"
                    val combinedDescritption = "$productDescription $description"
                    val finalText = "$combinedName $combinedDescritption"

                    tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                        if(it == TextToSpeech.SUCCESS) {
                            tts.language = Locale.US
                            tts.setSpeechRate(1.0f)
                            tts.speak(finalText, TextToSpeech.QUEUE_ADD, null)
                        }
                    })

                    Handler().postDelayed(Runnable { /* Create an Intent that will start the Menu-Activity. */
                        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                            if(it == TextToSpeech.SUCCESS) {
                                tts.language = Locale.US
                                tts.setSpeechRate(1.0f)
                                tts.speak("Press back button to go back to main screen", TextToSpeech.QUEUE_ADD, null)
                            }
                        })
                    }, 5000)
                }
            }

            override fun onFailure(call: Call<QrDataItem?>, t: Throwable) {

            }
        })

    }
}