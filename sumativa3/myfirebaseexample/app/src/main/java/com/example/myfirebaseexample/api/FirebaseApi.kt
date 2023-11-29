package com.example.myfirebaseexample.api

import com.example.myfirebaseexample.api.response.PostResponse
import com.example.myfirebaseexample.api.response.RopaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FirebaseApi {
    @GET("Ropas.json")
    fun getRopas(): Call<MutableMap<String, RopaResponse>>

    @GET("Ropas/{id}.json")
    fun getRopa(
        @Path("id") id: String
    ): Call<RopaResponse>

    @POST("Ropas.json")
    fun setRopa(
        @Body() body: RopaResponse
    ): Call<PostResponse>
}