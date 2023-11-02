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

package com.skydoves.chatgpt.core.network.operator

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import com.skydoves.chatgpt.core.network.R
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.operators.ApiResponseSuspendOperator
import com.skydoves.sandwich.retrofit.statusCode
import io.getstream.log.streamLog
import javax.inject.Inject

internal class ClearCacheGlobalOperator<T> @Inject constructor(
  private val context: Context
) : ApiResponseSuspendOperator<T>() {

  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = Unit

  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception) {
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()

    Handler(Looper.getMainLooper()).post {
      WebView(context).apply {
        clearCache(true)
        clearHistory()
      }
    }
  }

  override suspend fun onError(apiResponse: ApiResponse.Failure.Error) {
    when (apiResponse.statusCode) {
      StatusCode.Unauthorized, StatusCode.Forbidden -> {
        streamLog { "clear cache & histories for Unauthorized and Forbidden" }

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        Handler(Looper.getMainLooper()).post {
          WebView(context).apply {
            clearCache(true)
            clearHistory()
          }

          Toast.makeText(
            context,
            context.getString(R.string.toast_error),
            Toast.LENGTH_SHORT
          ).show()
        }
      }

      else -> Unit
    }
  }
}
