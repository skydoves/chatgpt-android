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

package com.skydoves.chatgpt.core.data.repository

import com.skydoves.chatgpt.core.model.GPTChatRequest
import com.skydoves.chatgpt.core.model.GPTChatResponse
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.Flow

interface GPTMessageRepository {

  suspend fun sendMessage(gptChatRequest: GPTChatRequest): ApiResponse<GPTChatResponse>

  fun watchIsChannelMessageEmpty(cid: String): Flow<Boolean>
}
