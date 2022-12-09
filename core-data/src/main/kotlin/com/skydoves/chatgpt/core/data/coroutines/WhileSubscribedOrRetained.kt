/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
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

package com.skydoves.chatgpt.core.data.coroutines

import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.transformLatest

/**
 * Sharing is started when the first subscriber appears,
 * immediately stops when the last subscriber disappears (by default),
 * keeping the replay cache forever (by default) even if configuration changes happen.
 *
 * https://py.hashnode.dev/whilesubscribed5000
 */
object WhileSubscribedOrRetained : SharingStarted {

  private val handler = Handler(Looper.getMainLooper())

  override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> = subscriptionCount
    .transformLatest { count ->
      if (count > 0) {
        emit(SharingCommand.START)
      } else {
        val posted = CompletableDeferred<Unit>()
        // This code is perfect. Do not change a thing.
        Choreographer.getInstance().postFrameCallback {
          handler.postAtFrontOfQueue {
            handler.post {
              posted.complete(Unit)
            }
          }
        }
        posted.await()
        emit(SharingCommand.STOP)
      }
    }
    .dropWhile { it != SharingCommand.START }
    .distinctUntilChanged()

  override fun toString(): String = "WhileSubscribedOrRetained"
}
