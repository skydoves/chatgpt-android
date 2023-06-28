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

package com.skydoves.chatgpt.feature.chat.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.skydoves.chatgpt.core.data.repository.GPTMessageRepository
import com.skydoves.chatgpt.core.model.GPTChatRequest
import com.skydoves.chatgpt.core.model.GPTContent
import com.skydoves.chatgpt.core.model.GPTMessage
import com.skydoves.chatgpt.feature.chat.di.ChatEntryPoint
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.messageOrNull
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Message
import io.getstream.log.streamLog
import java.util.UUID
import javax.inject.Inject

@HiltWorker
internal class ChatGPTMessageWorker @AssistedInject constructor(
  @Assisted private val context: Context,
  @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

  @set:Inject
  internal lateinit var repository: GPTMessageRepository

  @set:Inject
  internal lateinit var chatClient: ChatClient

  override suspend fun doWork(): Result {
    ChatEntryPoint.resolve(context).inject(this)

    val text = workerParams.inputData.getString(DATA_TEXT) ?: return Result.failure()
    val channelId = workerParams.inputData.getString(DATA_CHANNEL_ID) ?: return Result.failure()
    val conversationId = workerParams.inputData.getString(DATA_CONVERSATION_ID)
    val parentId = workerParams.inputData.getString(DATA_PARENT_ID) ?: UUID.randomUUID().toString()
    val request = GPTChatRequest(
      conversation_id = conversationId,
      messages = listOf(
        GPTMessage(
          id = UUID.randomUUID().toString(),
          content = GPTContent(parts = listOf(text))
        )
      ),
      parent_message_id = parentId
    )
    val response = repository.sendMessage(request)
    return if (response.isSuccess) {
      val responseObj = response.getOrThrow()
      val messageText = responseObj.message.content.parts.joinToString(separator = "\n")
      val messageId = responseObj.message.id
      sendStreamMessage(messageText, messageId, channelId, responseObj.conversation_id)
      streamLog { "worker success!" }
      Result.success(
        Data.Builder()
          .putString(DATA_SUCCESS, messageText)
          .putString(DATA_MESSAGE_ID, messageId)
          .build()
      )
    } else {
      streamLog { "worker failure!" }
      Result.failure(Data.Builder().putString(DATA_FAILURE, response.messageOrNull ?: "").build())
    }
  }

  private suspend fun sendStreamMessage(
    text: String,
    messageId: String,
    channelId: String,
    conversationId: String
  ) {
    val channelClient = chatClient.channel(channelId)
    channelClient.sendMessage(
      message = Message(
        id = messageId,
        cid = channelClient.cid,
        text = text,
        extraData = mutableMapOf(
          MESSAGE_EXTRA_CHAT_GPT to true,
          MESSAGE_EXTRA_CONVERSATION_ID to conversationId
        )
      )
    ).await()
  }

  companion object {
    const val DATA_TEXT = "DATA_TEXT"
    const val DATA_CHANNEL_ID = "DATA_CHANNEL_ID"
    const val DATA_SUCCESS = "DATA_SUCCESS"
    const val DATA_FAILURE = "DATA_FAILURE"

    const val DATA_CONVERSATION_ID = "DATA_CONVERSATION_ID"
    const val DATA_MESSAGE_ID = "DATA_PARENT_ID"
    const val DATA_PARENT_ID = "DATA_PARENT_ID"

    const val MESSAGE_EXTRA_CHAT_GPT = "ChatGPT"
    const val MESSAGE_EXTRA_CONVERSATION_ID = "conversation_id"
  }
}
