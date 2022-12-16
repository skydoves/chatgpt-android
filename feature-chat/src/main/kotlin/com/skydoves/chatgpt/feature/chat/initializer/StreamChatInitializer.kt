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

package com.skydoves.chatgpt.feature.chat.initializer

import android.content.Context
import android.content.Intent
import androidx.startup.Initializer
import com.skydoves.chatgpt.core.preferences.Preferences
import com.skydoves.chatgpt.feature.chat.BuildConfig
import com.skydoves.chatgpt.feature.chat.di.ApplicationEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator
import io.getstream.log.streamLog
import javax.inject.Inject
import kotlin.random.Random

/**
 * StreamChatInitializer initializes all Stream Client components.
 */
class StreamChatInitializer : Initializer<Unit> {

  @set:Inject
  internal lateinit var preferences: Preferences

  override fun create(context: Context) {
    ApplicationEntryPoint.resolve(context).inject(this)

    streamLog { "StreamChatInitializer is initialized" }

    /**
     * initialize a global instance of the [ChatClient].
     * The ChatClient is the main entry point for all low-level operations on chat.
     * e.g, connect/disconnect user to the server, send/update/pin message, etc.
     */
    val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING
    val offlinePluginFactory = StreamOfflinePluginFactory(
      config = Config(
        backgroundSyncEnabled = true,
        userPresence = true,
        persistenceEnabled = true,
        uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING
      ),
      appContext = context
    )
    val chatClient = ChatClient.Builder(BuildConfig.STREAM_CHAT_SDK, context)
      .notifications(createNotificationConfig(), createNotificationHandler(context))
      .withPlugin(offlinePluginFactory)
      .logLevel(logLevel)
      .build()

    val user = User(
      id = preferences.userUUID,
      name = "User ${Random.nextInt(10000)}",
      image = "https://picsum.photos/id/${Random.nextInt(1000)}/300/300"
    )

    val token = chatClient.devToken(user.id)
    chatClient.connectUser(user, token).enqueue()
  }

  /**
   * Creates [NotificationConfig] that configures push notifications.
   */
  private fun createNotificationConfig(): NotificationConfig {
    return NotificationConfig(
      pushDeviceGenerators = listOf(
        FirebasePushDeviceGenerator()
      )
    )
  }

  /**
   * Creates [NotificationHandler] that handles new push notifications and
   * customizes an intent the user triggers when clicking on a notification.
   */
  private fun createNotificationHandler(
    context: Context
  ): NotificationHandler {
    return NotificationHandlerFactory.createNotificationHandler(
      context = context,
      newMessageIntent = { _: String, _: String, _: String ->
        context.packageManager.getLaunchIntentForPackage("com.skydoves.chatgpt.MainActivity")
          ?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          }!!
      }
    )
  }

  override fun dependencies(): List<Class<out Initializer<*>>> =
    listOf(StreamLogInitializer::class.java)
}
