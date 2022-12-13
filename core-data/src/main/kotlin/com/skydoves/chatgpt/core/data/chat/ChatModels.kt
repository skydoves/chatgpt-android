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

package com.skydoves.chatgpt.core.data.chat

import io.getstream.chat.android.client.models.User

val chatGPTUser = User(
  id = "70ef052a-da88-4451-af92-99f7ed335a71",
  role = "user",
  name = "ChatGPT",
  image = "https://user-images.githubusercontent.com/24237865/" +
    "206655413-fb7c70f6-703e-476b-9ee9-861bfb8bf6f7.jpeg"
)

val commonChannelId: String = "messaging:4d7cd1e8-e6d6-4df3-bfad-babbb9411cce"
