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

package com.skydoves.chatgpt.core.network.service

import com.skydoves.sandwich.ApiResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTService {
  @Headers(
    "accept: text/event-stream",
    "accept-encoding: gzip, deflate, br",
    "accept-language: en-GB,en-US;q=0.9,en;q=0.8",
    "content-type: application/json",
    "referer: https://chat.openai.com/chat",
    "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
      "(KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"
  )
  @POST("backend-api/conversation")
  suspend fun sendMessage(@Body body: RequestBody): ApiResponse<ResponseBody>
}
