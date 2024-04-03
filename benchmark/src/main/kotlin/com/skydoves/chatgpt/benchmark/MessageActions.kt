/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.chatgpt.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.messagesExplore() = device.apply {
  messagesWaitForContent()
  messagesScrollDownUp()
}

fun MacrobenchmarkScope.messagesWaitForContent() = device.apply {
  wait(Until.hasObject(By.res("Stream_MessagesScreen")), STANDARD_TIMEOUT)
}

fun MacrobenchmarkScope.messagesScrollDownUp() = device.apply {
  val channelList = waitAndFindObject(By.res("Stream_Messages"), STANDARD_TIMEOUT)
  flingElementDownUp(channelList)
}
