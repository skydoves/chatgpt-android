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

package com.skydoves.chatgpt.feature.chat.di

import android.content.Context
import com.skydoves.chatgpt.feature.chat.initializer.StreamChatInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ApplicationEntryPoint {

  fun inject(streamChatInitializer: StreamChatInitializer)

  companion object {

    fun resolve(context: Context): ApplicationEntryPoint {
      val appContext = context.applicationContext ?: throw IllegalStateException(
        "applicationContext was not found in NetworkEntryPoint"
      )
      return EntryPointAccessors.fromApplication(
        appContext,
        ApplicationEntryPoint::class.java
      )
    }
  }
}
