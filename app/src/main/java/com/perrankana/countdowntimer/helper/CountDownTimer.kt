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
package com.perrankana.countdowntimer.helper

import java.util.Timer
import java.util.TimerTask

class CountDownTimer(countdown: Int, private val offer: (Int) -> Unit) : Timer() {
    init {
        var time = countdown
        scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try {
                        offer(time)
                    } catch (e: Exception) {
                    }
                    if (time == 0) {
                        cancel()
                    }
                    time -= 1
                }
            },
            0,
            1000
        )
    }
}
