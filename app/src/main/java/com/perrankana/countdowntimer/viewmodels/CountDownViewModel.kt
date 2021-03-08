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

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.perrankana.countdowntimer.helper.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CountDownViewModel : ViewModel(), CoroutineScope by MainScope() {

    private val _count = MutableLiveData<CountDownState>(CountDownState.SetTimer())
    val count: LiveData<CountDownState> = _count

    fun onCountDownStart() {
        val timerCount = count.value?.count ?: 0
        if (timerCount == 0) return
        launch {
            tickFlow(timerCount).collect {
                _count.value = CountDownState.Counting(timerCount, it)
                if (it == 0) {
                    _count.value = CountDownState.End()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun tickFlow(countdown: Int) = callbackFlow {
        val timer = CountDownTimer(countdown) { time ->
            offer(time)
        }
        awaitClose {
            timer.cancel()
        }
    }

    fun onTimerChanged(timer: String) {
        if (timer.isDigitsOnly()) {
            _count.value = CountDownState.SetTimer(timer.toInt())
        }
    }

    fun onStartAgain() {
        _count.value = CountDownState.SetTimer()
    }
}

sealed class CountDownState(val count: Int) {
    class SetTimer(count: Int = 0) : CountDownState(count)
    class Start(count: Int) : CountDownState(count)
    class Counting(val totalCount: Int, count: Int) : CountDownState(count)
    class End : CountDownState(count = 0)
}
