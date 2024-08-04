package com.example.quizapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizapp.components.Questions

@Composable
fun QuizApp(modifier: Modifier = Modifier, viewModel: QuestionViewModel = hiltViewModel()) {
    Questions(viewModel)
}
