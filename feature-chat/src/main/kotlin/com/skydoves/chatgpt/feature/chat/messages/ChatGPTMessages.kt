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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skydoves.chatgpt.core.data.chat.chatGPTUser
import com.skydoves.chatgpt.core.data.chat.commonChannelId
import com.skydoves.chatgpt.core.navigation.AppComposeNavigator
import com.skydoves.chatgpt.core.preferences.Empty
import com.skydoves.chatgpt.feature.chat.R
import com.skydoves.chatgpt.feature.chat.theme.ChatGPTStreamTheme
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.common.state.Delete
import io.getstream.chat.android.common.state.DeletedMessageVisibility
import io.getstream.chat.android.common.state.Flag
import io.getstream.chat.android.common.state.MessageFooterVisibility
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.common.state.Reply
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messageoptions.MessageOptionItemState
import io.getstream.chat.android.compose.state.messages.SelectedMessageOptionsState
import io.getstream.chat.android.compose.state.messages.SelectedMessageReactionsPickerState
import io.getstream.chat.android.compose.state.messages.SelectedMessageReactionsState
import io.getstream.chat.android.compose.state.messages.SelectedMessageState
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.components.SimpleDialog
import io.getstream.chat.android.compose.ui.components.messageoptions.defaultMessageOptionsState
import io.getstream.chat.android.compose.ui.components.reactionpicker.ReactionsPicker
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedMessageMenu
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedReactionsMenu
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
            imagePreviewResultAction(
              result,
              listViewModel,
              composerViewModel
            )
          }
        ) { state ->
          var messageState = state
          if (channelId != commonChannelId &&
            messageState is MessageItemState &&
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
                ),
                currentUser = chatGPTUser
              )
          }

          MessageContainer(
            messageListItem = messageState,
            onLongItemClick = { message -> listViewModel.selectMessage(message) },
            onReactionsClick = { message -> listViewModel.selectReactions(message) },
            onThreadClick = { message -> listViewModel.openMessageThread(message) },
            onGiphyActionClick = { action -> listViewModel.performGiphyAction(action) },
            onQuotedMessageClick = { message -> listViewModel.scrollToSelectedMessage(message) },
            onImagePreviewResult = { result ->
              imagePreviewResultAction(
                result,
                listViewModel,
                composerViewModel
              )
            }
          )
        }
      }

      MessageMenus(
        listViewModel = listViewModel,
        composerViewModel = composerViewModel
      )

      MessageDialogs(listViewModel = listViewModel)
    }
  }
}

private fun imagePreviewResultAction(
  result: ImagePreviewResult?,
  listViewModel: MessageListViewModel,
  composerViewModel: MessageComposerViewModel
) {
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

@Composable
private fun MessageDialogs(listViewModel: MessageListViewModel) {
  val messageActions = listViewModel.messageActions

  val deleteAction = messageActions.firstOrNull { it is Delete }

  if (deleteAction != null) {
    SimpleDialog(
      modifier = Modifier.padding(16.dp),
      title = stringResource(
        id = io.getstream.chat.android.compose.R.string.stream_compose_delete_message_title
      ),
      message = stringResource(
        id = io.getstream.chat.android.compose.R.string.stream_compose_delete_message_text
      ),
      onPositiveAction = { listViewModel.deleteMessage(deleteAction.message) },
      onDismiss = { listViewModel.dismissMessageAction(deleteAction) }
    )
  }

  val flagAction = messageActions.firstOrNull { it is Flag }

  if (flagAction != null) {
    SimpleDialog(
      modifier = Modifier.padding(16.dp),
      title = stringResource(
        id = io.getstream.chat.android.compose.R.string.stream_compose_flag_message_title
      ),
      message = stringResource(
        id = io.getstream.chat.android.compose.R.string.stream_compose_flag_message_text
      ),
      onPositiveAction = { listViewModel.flagMessage(flagAction.message) },
      onDismiss = { listViewModel.dismissMessageAction(flagAction) }
    )
  }
}

@Composable
private fun BoxScope.MessageMenus(
  listViewModel: MessageListViewModel,
  composerViewModel: MessageComposerViewModel
) {
  val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState

  val selectedMessage = selectedMessageState?.message ?: Message()

  MessagesScreenMenus(
    listViewModel = listViewModel,
    composerViewModel = composerViewModel,
    selectedMessageState = selectedMessageState,
    selectedMessage = selectedMessage
  )

  MessagesScreenReactionsPicker(
    listViewModel = listViewModel,
    composerViewModel = composerViewModel,
    selectedMessageState = selectedMessageState,
    selectedMessage = selectedMessage
  )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.MessagesScreenReactionsPicker(
  listViewModel: MessageListViewModel,
  composerViewModel: MessageComposerViewModel,
  selectedMessageState: SelectedMessageState?,
  selectedMessage: Message
) {
  AnimatedVisibility(
    visible = selectedMessageState is SelectedMessageReactionsPickerState &&
      selectedMessage.id.isNotEmpty(),
    enter = fadeIn(),
    exit = fadeOut(
      animationSpec = tween(
        durationMillis = AnimationConstants.DefaultDurationMillis / 2
      )
    )
  ) {
    ReactionsPicker(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .heightIn(max = 400.dp)
        .wrapContentHeight()
        .animateEnterExit(
          enter = slideInVertically(
            initialOffsetY = { height -> height },
            animationSpec = tween()
          ),
          exit = slideOutVertically(
            targetOffsetY = { height -> height },
            animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
          )
        ),
      message = selectedMessage,
      onMessageAction = { action ->
        composerViewModel.performMessageAction(action)
        listViewModel.performMessageAction(action)
      },
      onDismiss = { listViewModel.removeOverlay() }
    )
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.MessagesScreenMenus(
  listViewModel: MessageListViewModel,
  composerViewModel: MessageComposerViewModel,
  selectedMessageState: SelectedMessageState?,
  selectedMessage: Message
) {
  val user by listViewModel.user.collectAsState()

  val ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf()

  val isInThread = listViewModel.isInThread

  val newMessageOptions = defaultMessageOptionsState(
    selectedMessage = selectedMessage,
    currentUser = user,
    isInThread = isInThread,
    ownCapabilities = ownCapabilities
  )

  var messageOptions by remember {
    mutableStateOf<List<MessageOptionItemState>>(emptyList())
  }

  if (newMessageOptions.isNotEmpty()) {
    messageOptions = newMessageOptions
  }

  AnimatedVisibility(
    visible = selectedMessageState is SelectedMessageOptionsState &&
      selectedMessage.id.isNotEmpty(),
    enter = fadeIn(),
    exit = fadeOut(
      animationSpec = tween(
        durationMillis = AnimationConstants.DefaultDurationMillis / 2
      )
    )
  ) {
    SelectedMessageMenu(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .animateEnterExit(
          enter = slideInVertically(
            initialOffsetY = { height -> height },
            animationSpec = tween()
          ),
          exit = slideOutVertically(
            targetOffsetY = { height -> height },
            animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
          )
        ),
      messageOptions = messageOptions,
      message = selectedMessage,
      ownCapabilities = ownCapabilities,
      onMessageAction = { action ->
        composerViewModel.performMessageAction(action)
        listViewModel.performMessageAction(action)
      },
      onShowMoreReactionsSelected = {
        listViewModel.selectExtendedReactions(selectedMessage)
      },
      onDismiss = { listViewModel.removeOverlay() }
    )
  }

  AnimatedVisibility(
    visible = selectedMessageState is SelectedMessageReactionsState &&
      selectedMessage.id.isNotEmpty(),
    enter = fadeIn(),
    exit = fadeOut(
      animationSpec = tween(
        durationMillis = AnimationConstants.DefaultDurationMillis / 2
      )
    )
  ) {
    SelectedReactionsMenu(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .animateEnterExit(
          enter = slideInVertically(
            initialOffsetY = { height -> height },
            animationSpec = tween()
          ),
          exit = slideOutVertically(
            targetOffsetY = { height -> height },
            animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
          )
        ),
      currentUser = user,
      message = selectedMessage,
      onMessageAction = { action ->
        composerViewModel.performMessageAction(action)
        listViewModel.performMessageAction(action)
      },
      onShowMoreReactionsSelected = {
        listViewModel.selectExtendedReactions(selectedMessage)
      },
      onDismiss = { listViewModel.removeOverlay() },
      ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf()
    )
  }
}

@Composable
private fun HandleToastMessages(
  viewModel: ChatGPTMessagesViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val isMessageEmpty by viewModel.isMessageEmpty.collectAsState()
  val error by viewModel.errorMessage.collectAsState(initial = String.Empty)

  LaunchedEffect(key1 = isMessageEmpty) {
    if (isMessageEmpty) {
      viewModel.sendStreamChatMessage(context.getString(R.string.toast_hello))
    }
  }

  LaunchedEffect(key1 = error) {
    if (error.isNotEmpty()) {
      viewModel.sendStreamChatMessage("$error: ${context.getString(R.string.toast_hello)}")
    }
  }
}
