package com.example.quizapp.network

import com.example.quizapp.model.QuestionItem
import retrofit2.http.GET
import javax.inject.Singleton


@Singleton
interface QuestionApi {
    @GET("world.json")
    suspend fun getAllQuestions(): ArrayList<QuestionItem>
}