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

package com.skydoves.chatgpt.core.network

import com.skydoves.chatgpt.core.network.BuildConfig.GPT_SESSION
import com.skydoves.chatgpt.core.preferences.Preferences
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class GPTInterceptor @Inject constructor(
  private val preferences: Preferences
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url
    val url = originalUrl.newBuilder().build()
    val requestBuilder = originalRequest.newBuilder().url(url)
    val authorization =
      preferences.authorization.takeIf { it.isNotEmpty() } ?: "Bearer $GPT_SESSION"
    requestBuilder.addHeader("authorization", authorization)
    val cookie = preferences.cookie.takeIf { it.isNotEmpty() } ?: BuildConfig.CF_CLEARANCE
    requestBuilder.addHeader("cookie", cookie)
    val request = requestBuilder.build()
    return chain.proceed(request)
  }
}
