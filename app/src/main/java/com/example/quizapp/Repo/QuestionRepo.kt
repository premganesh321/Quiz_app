package com.example.quizapp.Repo

import com.example.quizapp.data.DataOrException
import com.example.quizapp.model.QuestionItem
import com.example.quizapp.network.QuestionApi
import javax.inject.Inject

class QuestionRepo @Inject constructor(
    private val api : QuestionApi
) {
    private var dataOrException =
        DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions() : DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data  = api.getAllQuestions()
            if(dataOrException.data.toString().isNotEmpty()){
                dataOrException.loading = false
            }
        }
        catch (exception : Exception){
             dataOrException.exception = exception

        }
        return dataOrException
    }
}
