package com.navieo.questionsapp.ApiClasses

import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("RiderApi/Pricing/GetQuestionOptions?TUPC=XCON200007")
    fun getQuestions(@Header("Authorization") token: String): retrofit2.Call<ApiResponse>
}