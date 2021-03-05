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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.perrankana.countdowntimer.ui.theme.MyTheme
import com.perrankana.countdowntimer.viewmodels.CountDownState
import com.perrankana.countdowntimer.viewmodels.CountDownViewModel

@Composable
fun CountDownScreen(countDownViewModel: CountDownViewModel) {
    val countDownState: CountDownState by countDownViewModel.count.observeAsState(CountDownState.Start())

    Column {
        Text(text = "${countDownState.count}")
        if (countDownState.showButton) {
            Button(onClick = { countDownViewModel.onCountDownStart() }) {
                Text(text = "Start")
            }
        }
    }
}

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
