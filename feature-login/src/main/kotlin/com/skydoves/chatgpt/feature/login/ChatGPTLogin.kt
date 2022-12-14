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

import android.os.Handler
import android.os.Looper
import android.webkit.WebChromeClient
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.acsbendi.requestinspectorwebview.RequestInspectorWebViewClient
import com.acsbendi.requestinspectorwebview.WebViewRequest
import com.skydoves.chatgpt.core.designsystem.component.ChatGPTSmallTopBar
import com.skydoves.chatgpt.core.navigation.AppComposeNavigator
import com.skydoves.chatgpt.core.navigation.ChatGPTScreens
import com.skydoves.chatgpt.core.network.AUTHORIZATION
import com.skydoves.chatgpt.core.network.COOKIE
import com.skydoves.chatgpt.core.network.USER_AGENT

@Composable
fun ChatGPTLogin(
  composeNavigator: AppComposeNavigator,
  viewModel: ChatGPTLoginViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val webView = remember { WebView(context) }

  BackHandler {
    if (webView.canGoBack()) {
      webView.goBack()
    } else {
      composeNavigator.navigateUp()
    }
  }

  Scaffold(topBar = {
    ChatGPTSmallTopBar(
      title = stringResource(id = R.string.top_bar_login)
    )
  }) { padding ->
    AndroidView(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      factory = {
        webView.apply {
          webChromeClient = WebChromeClient()
          webViewClient = object : RequestInspectorWebViewClient(this@apply) {
            override fun shouldInterceptRequest(
              view: WebView,
              webViewRequest: WebViewRequest
            ): WebResourceResponse? {
              if (checkIfAuthorized(webViewRequest.headers)) {
                val authorization = webViewRequest.headers[AUTHORIZATION] ?: return null
                val cookie = webViewRequest.headers[COOKIE] ?: return null
                val userAgent = webViewRequest.headers[USER_AGENT] ?: return null
                viewModel.persistLoginInfo(
                  authorization = authorization,
                  cookie = cookie,
                  userAgent = userAgent
                )
                composeNavigator.navigateAndClearBackStack(ChatGPTScreens.Channels.name)

                Handler(Looper.getMainLooper()).post {
                  Toast.makeText(context, R.string.toast_logged_in, Toast.LENGTH_SHORT).show()
                }
              }
              return super.shouldInterceptRequest(view, webViewRequest)
            }
          }.apply {
            loadUrl("https://chat.openai.com/chat")
          }
        }
      }
    )
  }
}

private fun checkIfAuthorized(header: Map<String, String>): Boolean {
  return header.containsKey(AUTHORIZATION) &&
    header.containsKey(COOKIE) &&
    header.containsKey(USER_AGENT)
}
