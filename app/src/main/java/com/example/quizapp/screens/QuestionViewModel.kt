package com.example.quizapp.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.Repo.QuestionRepo
import com.example.quizapp.data.DataOrException
import com.example.quizapp.model.QuestionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel  @Inject constructor(private val repository: QuestionRepo) : ViewModel() {
    val data : MutableState<DataOrException<ArrayList<QuestionItem>,Boolean,Exception>>
    = mutableStateOf(
        DataOrException(null,true,Exception(""))
    )
    var score = mutableIntStateOf(0)

    init {
        getAllQuestion()
    }
    private fun getAllQuestion(){
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if (data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }

    fun updateScore(isCorrect: Boolean) {
        if (isCorrect) {
            score.value += 1
        } else {
            score.value -= 1
        }
    }
}
