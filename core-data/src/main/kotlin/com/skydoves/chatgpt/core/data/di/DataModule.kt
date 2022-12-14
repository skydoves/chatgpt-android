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

package com.skydoves.chatgpt.core.data.di

import com.skydoves.chatgpt.core.data.repository.GPTChannelRepository
import com.skydoves.chatgpt.core.data.repository.GPTChannelRepositoryImpl
import com.skydoves.chatgpt.core.data.repository.GPTLoginRepository
import com.skydoves.chatgpt.core.data.repository.GPTLoginRepositoryImpl
import com.skydoves.chatgpt.core.data.repository.GPTMessageRepository
import com.skydoves.chatgpt.core.data.repository.GPTMessageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

  @Binds
  fun bindsChatGPTChannelsRepository(
    gptChannelRepository: GPTChannelRepositoryImpl
  ): GPTChannelRepository

  @Binds
  fun bindsChatGPTMessagesRepository(
    gptMessageRepository: GPTMessageRepositoryImpl
  ): GPTMessageRepository

  @Binds
  fun bindChatGPTLoginRepository(
    gptLoginRepository: GPTLoginRepositoryImpl
  ): GPTLoginRepository
}
