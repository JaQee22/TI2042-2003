package com.example.myfirebaseexample.api

import com.example.myfirebaseexample.api.response.PostResponse
import com.example.myfirebaseexample.api.response.RopaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body

class FirebaseApiAdapter {
    private var URL_BASE = "https://aplicacion-dad47-default-rtdb.firebaseio.com/"
    private val firebaseApi = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRopas(): MutableMap<String, RopaResponse>? {
        val call = firebaseApi.create(FirebaseApi::class.java).getRopas().execute()
        val ropas = call.body()
        return ropas
    }

    fun getRopa(id: String): RopaResponse? {
        val call = firebaseApi.create(FirebaseApi::class.java).getRopa(id).execute()
        val ropa = call.body()
        id.also { ropa?.id = it }
        return ropa
    }

    fun setRopa(ropa: RopaResponse): PostResponse? {
        val call = firebaseApi.create(FirebaseApi::class.java).setRopa(ropa).execute()
        val results = call.body()
        return results
    }
}