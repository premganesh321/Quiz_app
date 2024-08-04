package com.example.quizapp.components

import android.icu.text.UnicodeSetSpanner.CountMethod
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.screens.QuestionViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.example.quizapp.model.QuestionItem


@Composable
fun Questions(viewModel: QuestionViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()?.shuffled()?.take(10)
    val questionIndex = remember {
        mutableIntStateOf(0)
    }
    val score = remember {
        viewModel.score
    }
    if (viewModel.data.value.loading == true) {
        //loading
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        Log.d("Loading", "it is loading ")
    } else {
        //show the question
        val question = try {
            questions?.get(questionIndex.value)
        } catch (ex: java.lang.Exception) {
            null
        }
        if (questions != null) {
            QuestionDisplay(
                question = question!!,
                questionIndex = questionIndex,
                noofQuestion = questions.size,
                score = score.value,
                viewModel = viewModel,
            ) {
                questionIndex.intValue += 1
            }
        }
    }
}

//@Preview
@Composable
fun QuestionDisplay(
    modifier: Modifier = Modifier,
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    noofQuestion: Int,
    score: Int,
    viewModel: QuestionViewModel,
    onNextClicked: (Int) -> Unit
) {
    val choiceState = remember(question) {
        question.choices.toMutableList()
    }
    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choiceState[it] == question.answer
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value >= 2){
                ProgressBar(
                modifier = Modifier.padding(top = 10.dp),
                score = score,
                totalQuestions = noofQuestion
            )}
            TrackTheQuestion(counter = questionIndex.value, outOff = noofQuestion)
            DottedLine()
            Column {
                Text(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f),
                    text = question.question,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
                choiceState.forEachIndexed { index, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .border(
                                width = 4.dp,
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(corner = CornerSize(5.dp))
                            )
                            .clip(RoundedCornerShape(corner = CornerSize(5.dp)))
                            .background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index), onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (correctAnswerState.value == true && index == answerState.value) {
                                    Color.Green
                                } else {
                                    Color.Red
                                }
                            )
                        )
                        if (correctAnswerState.value == true && index == answerState.value) {
                            Text(text = answerText, color = Color.Green)
                        } else if (correctAnswerState.value == false && index == answerState.value) {
                            Text(text = answerText, color = Color.Red)
                        } else {
                            Text(text = answerText)
                        }
                    }
                }
                Button(
                    onClick = {
                        onNextClicked(questionIndex.value)
                        viewModel.updateScore(correctAnswerState.value == true)
                        Log.d("score", viewModel.score.value.toString())
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .align(alignment = CenterHorizontally),
                    shape = RoundedCornerShape(corner = CornerSize(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = Color.White,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackTheQuestion(modifier: Modifier = Modifier, counter: Int = 10, outOff: Int = 100) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Question",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Text(
            text = ":   ",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Text(
            text = counter.toString(),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            color = Color.Black
        )
        Text(
            text = "/",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 30.sp),
            color = Color.DarkGray
        )
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = outOff.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 23.sp,
                fontWeight = FontWeight.Light
            ),
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DottedLine(
    modifier: Modifier = Modifier, pathEffect: PathEffect = PathEffect.dashPathEffect(
        floatArrayOf(10f, 10f), 0f
    )
) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.DarkGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@Preview
@Composable
fun ProgressBar(modifier: Modifier = Modifier, score: Int = 507, totalQuestions: Int = 4875) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFF00A3E0),
            Color(0xFFAB47BC)
        )
    )

    val progress by animateFloatAsState(
        targetValue = score.coerceIn(0, totalQuestions) / totalQuestions.toFloat()
    )

    Log.d("ProgressBar", "Score: $score, Total Questions: $totalQuestions, Progress: $progress")

    Box(
        modifier = modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .border(width = 4.dp, color = Color.DarkGray, shape = RoundedCornerShape(34.dp))
            .clip(RoundedCornerShape(34.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(brush = gradient)
        ) {
            Text(
                text = "$score",
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(34.dp))
                    .align(alignment = Alignment.Center),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}
