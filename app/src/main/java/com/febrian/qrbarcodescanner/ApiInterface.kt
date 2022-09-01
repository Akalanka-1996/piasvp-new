package com.febrian.qrbarcodescanner

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
    @GET("products/{productId}")
    fun getData(
        @Path("productId") number: Long
    ): Call<QrDataItem>
}