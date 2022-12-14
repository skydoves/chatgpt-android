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

package com.skydoves.chatgpt.feature.login

import androidx.lifecycle.ViewModel
import com.skydoves.chatgpt.core.data.repository.GPTLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.log.streamLog
import javax.inject.Inject

@HiltViewModel
class ChatGPTLoginViewModel @Inject constructor(
  private val gptLoginRepository: GPTLoginRepository
) : ViewModel() {

  fun persistLoginInfo(authorization: String, cookie: String, userAgent: String) {
    streamLog { "authorization: $authorization\ncookie: $cookie\nuserAgent:$userAgent" }
    gptLoginRepository.persistLoginInfo(
      authorization = authorization,
      cookie = cookie,
      userAgent = userAgent
    )
  }
}
