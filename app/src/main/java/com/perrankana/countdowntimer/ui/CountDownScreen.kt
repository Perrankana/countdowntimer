/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.perrankana.countdowntimer.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.perrankana.countdowntimer.ui.theme.MyTheme
import com.perrankana.countdowntimer.ui.theme.typography
import com.perrankana.countdowntimer.viewmodels.CountDownState
import com.perrankana.countdowntimer.viewmodels.CountDownViewModel
import java.util.concurrent.TimeUnit

@Composable
fun CountDownScreen(countDownViewModel: CountDownViewModel) {
    val countDownState: CountDownState by countDownViewModel.count.observeAsState(CountDownState.SetTimer())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (countDownState is CountDownState.SetTimer ||
            countDownState is CountDownState.Start
        ) {
            EnterCount(
                countDownState,
                onValueChanged = {
                    countDownViewModel.onTimerChanged(it)
                }
            ) {
                countDownViewModel.onCountDownStart()
            }
        }
        if (countDownState is CountDownState.Counting ||
            countDownState is CountDownState.End
        ) {

            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (timer, button) = createRefs()

                if (countDownState is CountDownState.Counting) {
                    CountDown(
                        countDownState = countDownState as CountDownState.Counting,
                        modifier = Modifier.constrainAs(timer) {
                            centerTo(parent)
                        }
                    )
                }
                if (countDownState is CountDownState.End) {
                    Wheels(
                        360f, 360f, countDownState,
                        modifier = Modifier.constrainAs(timer) {
                            centerTo(parent)
                        }
                    )
                    Button(
                        modifier = Modifier
                            .constrainAs(button) {
                                bottom.linkTo(parent.bottom, margin = 24.dp)
                                centerHorizontallyTo(parent)
                            },
                        onClick = { countDownViewModel.onStartAgain() }
                    ) {
                        Text(text = "Start Again", style = typography.h4)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnterCount(
    countDownState: CountDownState,
    onValueChanged: (String) -> Unit,
    onCountEntered: () -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (textField, button) = createRefs()

        TextField(
            modifier = Modifier.constrainAs(textField) {
                centerTo(parent)
            },
            value = countDownState.count.toString(),
            onValueChange = onValueChanged,
            textStyle = typography.h4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(40.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Button(
            modifier = Modifier
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                    centerHorizontallyTo(parent)
                },
            onClick = onCountEntered
        ) {
            Text(text = "Start", style = typography.h4)
        }
    }
}

@Composable
private fun CountDown(
    countDownState: CountDownState.Counting,
    modifier: Modifier = Modifier
) {
    val arc: Float by animateFloatAsState(
        countToArc(
            countDownState.count,
            countDownState.totalCount
        )
    )
    val arc2: Float by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = TimeUnit.SECONDS.toMillis(1).toInt()
            },
            repeatMode = RepeatMode.Restart
        )
    )
    Wheels(arc, arc2, countDownState, modifier)
}

@Composable
private fun Wheels(
    arc: Float,
    arc2: Float,
    countDownState: CountDownState,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .width(250.dp)
            .height(250.dp)
    ) {
        val (countText, arch) = createRefs()
        val background = MaterialTheme.colors.secondary
        val primary = MaterialTheme.colors.primary

        Canvas(
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .constrainAs(arch) {
                    centerTo(countText)
                }
        ) {
            drawOuterArc(primary, arc, size.width, size.height)
            drawInnerArc(background, arc2, size.width, size.height)
        }
        Text(
            text = "${countDownState.count}",
            modifier = Modifier.constrainAs(countText) {
                centerTo(parent)
            },
            style = typography.h1, color = MaterialTheme.colors.onBackground
        )
    }
}

private fun DrawScope.drawInnerArc(
    background: Color,
    arc2: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    drawArc(
        color = background,
        -90f,
        arc2,
        useCenter = true,
        alpha = 0.8f,
        size = Size(canvasWidth - 100, canvasHeight - 100),
        topLeft = Offset(x = 50f, y = 50f)
    )
}

private fun DrawScope.drawOuterArc(
    primary: Color,
    arc: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    drawArc(
        primary,
        -90f,
        arc,
        useCenter = false,
        size = Size(canvasWidth, canvasHeight),
        style = Stroke(width = 40f, cap = StrokeCap.Round)
    )
}

private fun countToArc(count: Int, totalCount: Int) = (360 - ((count * 360) / totalCount)).toFloat()

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        CountDownScreen(CountDownViewModel())
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        CountDownScreen(CountDownViewModel())
    }
}
