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