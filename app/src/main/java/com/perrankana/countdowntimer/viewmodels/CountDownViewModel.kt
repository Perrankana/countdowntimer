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
package com.perrankana.countdowntimer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.perrankana.countdowntimer.helper.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel(), CoroutineScope by MainScope() {

    private val _count = MutableLiveData<CountDownState>(CountDownState.Start())
    val count: LiveData<CountDownState> = _count

    fun onCountDownStart() {
        launch {
            tickFlow(20).collect {
                _count.value = CountDownState.Counting(it)
                if (it == 0) {
                    _count.value = CountDownState.End()
                }
            }
        }
    }

    private fun tickFlow(countdown: Int) = callbackFlow {
        val timer = CountDownTimer(countdown = countdown) { time ->
            offer(time)
        }
        awaitClose {
            timer.cancel()
        }
    }
}

sealed class CountDownState(val count: Int, val showButton: Boolean) {
    class Start : CountDownState(count = 20, showButton = true)
    class Counting(count: Int) : CountDownState(count = count, showButton = false)
    class End : CountDownState(count = 0, showButton = true)
}
