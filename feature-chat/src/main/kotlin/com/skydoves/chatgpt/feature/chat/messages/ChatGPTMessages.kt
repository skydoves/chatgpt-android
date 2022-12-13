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

package com.skydoves.chatgpt.feature.chat.messages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.chatgpt.core.data.chat.chatGPTUser
import com.skydoves.chatgpt.core.navigation.AppComposeNavigator
import com.skydoves.chatgpt.feature.chat.R
import com.skydoves.chatgpt.feature.chat.theme.ChatGPTStreamTheme
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.common.state.DeletedMessageVisibility
import io.getstream.chat.android.common.state.MessageFooterVisibility
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.common.state.Reply
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageContainer
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.core.internal.InternalStreamChatApi

@OptIn(InternalStreamChatApi::class)
@Composable
fun ChatGPTMessages(
  channelId: String,
  composeNavigator: AppComposeNavigator,
  viewModel: ChatGPTMessagesViewModel = hiltViewModel(),
  messageLimit: Int = 30,
  showHeader: Boolean = true,
  enforceUniqueReactions: Boolean = true,
  showDateSeparators: Boolean = true,
  showSystemMessages: Boolean = true,
  deletedMessageVisibility: DeletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
  messageFooterVisibility: MessageFooterVisibility = MessageFooterVisibility.WithTimeDifference(),
  onBackPressed: () -> Unit = { composeNavigator.navigateUp() },
  onHeaderActionClick: (channel: Channel) -> Unit = {}
) {
  val factory = MessagesViewModelFactory(
    context = LocalContext.current,
    channelId = channelId,
    enforceUniqueReactions = enforceUniqueReactions,
    messageLimit = messageLimit,
    showSystemMessages = showSystemMessages,
    showDateSeparators = showDateSeparators,
    deletedMessageVisibility = deletedMessageVisibility,
    messageFooterVisibility = messageFooterVisibility
  )

  val listViewModel = viewModel(MessageListViewModel::class.java, factory = factory)
  val composerViewModel = viewModel(MessageComposerViewModel::class.java, factory = factory)
  val attachmentsPickerViewModel =
    viewModel(AttachmentsPickerViewModel::class.java, factory = factory)

  val backAction = {
    val isInThread = listViewModel.isInThread
    val isShowingOverlay = listViewModel.isShowingOverlay

    when {
      attachmentsPickerViewModel.isShowingAttachments ->
        attachmentsPickerViewModel
          .changeAttachmentState(false)
      isShowingOverlay -> listViewModel.selectMessage(null)
      isInThread -> {
        listViewModel.leaveThread()
        composerViewModel.leaveThread()
      }
      else -> onBackPressed()
    }
  }

  BackHandler(enabled = true, onBack = backAction)

  HandleToastMessages()

  ChatGPTStreamTheme {
    Box(modifier = Modifier.fillMaxSize()) {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          if (showHeader) {
            val messageMode = listViewModel.messageMode
            val connectionState by listViewModel.connectionState.collectAsState()
            val user by listViewModel.user.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            MessageListHeader(
              modifier = Modifier.height(62.dp),
              channel = listViewModel.channel,
              currentUser = user,
              typingUsers = if (isLoading) {
                listOf(chatGPTUser)
              } else {
                emptyList()
              },
              connectionState = connectionState,
              messageMode = messageMode,
              onBackPressed = backAction,
              onHeaderActionClick = onHeaderActionClick
            )
          }
        },
        bottomBar = {
          MessageComposer(
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .align(Alignment.Center),
            viewModel = composerViewModel,
            onSendMessage = {
              viewModel.sendMessage(text = it.text)
              composerViewModel.sendMessage(it)
            },
            onAttachmentsClick = {
              attachmentsPickerViewModel.changeAttachmentState(true)
            },
            onCommandsClick = { composerViewModel.toggleCommandsVisibility() },
            onCancelAction = {
              listViewModel.dismissAllMessageActions()
              composerViewModel.dismissMessageActions()
            }
          )
        }
      ) {
        val currentState = listViewModel.currentMessagesState

        MessageList(
          modifier = Modifier
            .fillMaxSize()
            .background(ChatTheme.colors.appBackground)
            .padding(it),
          viewModel = listViewModel,
          lazyListState = rememberMessageListState(parentMessageId = currentState.parentMessageId),
          onThreadClick = { message ->
            composerViewModel.setMessageMode(MessageMode.MessageThread(message))
            listViewModel.openMessageThread(message)
          },
          onImagePreviewResult = { result ->
            when (result?.resultType) {
              ImagePreviewResultType.QUOTE -> {
                val message = listViewModel.getMessageWithId(result.messageId)

                if (message != null) {
                  composerViewModel.performMessageAction(Reply(message))
                }
              }

              ImagePreviewResultType.SHOW_IN_CHAT -> {
                listViewModel.focusMessage(result.messageId)
              }
              null -> Unit
            }
          }
        ) { state ->
          var messageState = state
          if (messageState is MessageItemState &&
            (messageState.message.extraData["ChatGPT"] as? Boolean) == true
          ) {
            messageState =
              messageState.copy(
                isMine = false,
                message = messageState.message.copy(
                  user = messageState.message.user.copy(
                    id = chatGPTUser.id,
                    name = chatGPTUser.name,
                    image = chatGPTUser.image
                  )
                )
              )
          }

          MessageContainer(messageListItem = messageState)
        }
      }
    }
  }
}

@Composable
private fun HandleToastMessages(
  viewModel: ChatGPTMessagesViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val isMessageEmpty by viewModel.isMessageEmpty.collectAsState()
  val isError by viewModel.isError.collectAsState()

  LaunchedEffect(key1 = isMessageEmpty) {
    if (isMessageEmpty) {
      viewModel.sendStreamChatMessage(context.getString(R.string.toast_hello))
    }
  }

  LaunchedEffect(key1 = isError) {
    if (isError) {
      viewModel.sendStreamChatMessage(context.getString(R.string.toast_error_session))
    }
  }
}
