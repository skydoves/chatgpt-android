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
import com.skydoves.chatgpt.core.network.ChatGPTDispatchers
import com.skydoves.chatgpt.core.network.Dispatcher
import com.skydoves.chatgpt.core.network.service.ChatGPTService
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.mapSuccess
import com.squareup.moshi.Moshi
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.utils.onSuccessSuspend
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

internal class GPTMessageRepositoryImpl @Inject constructor(
  @Dispatcher(ChatGPTDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val chatClient: ChatClient,
  private val chatGptService: ChatGPTService
) : GPTMessageRepository {

  override suspend fun sendMessage(gptChatRequest: GPTChatRequest): ApiResponse<GPTChatResponse> {
    val mosih = Moshi.Builder().build()
    val json = mosih.adapter(GPTChatRequest::class.java).toJson(gptChatRequest)
    val requestBody = (json.trimIndent()).toRequestBody(
      contentType = "text/plain".toMediaType()
    )
    val response = chatGptService.sendMessage(requestBody)
    return response.mapSuccess {
      val body = string()
      val chatMessage =
        body.split("\n").maxBy { it.length }.replace("data: ", "")
      mosih.adapter(GPTChatResponse::class.java).fromJson(chatMessage)!!
    }
  }

  override fun watchIsChannelMessageEmpty(cid: String): Flow<Boolean> = flow {
    val result = chatClient.channel(cid).watch().await()
    result.onSuccessSuspend { channel ->
      val messages = channel.messages
      emit(messages.isEmpty())
    }
  }.flowOn(ioDispatcher)
}
