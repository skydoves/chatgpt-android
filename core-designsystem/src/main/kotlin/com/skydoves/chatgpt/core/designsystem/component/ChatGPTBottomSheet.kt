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

@file:OptIn(ExperimentalMaterialApi::class)

package com.skydoves.chatgpt.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.skydoves.chatgpt.core.designsystem.theme.LocalBackgroundTheme

@Composable
fun ChatGPTBottomSheet(
  bottomSheetState: BottomSheetState = BottomSheetState.HalfExpanded,
  sheetContent: @Composable ColumnScope.() -> Unit,
  content: @Composable () -> Unit
) {
  val modalSheetState = rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Hidden,
    confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
    skipHalfExpanded = false
  )

  LaunchedEffect(key1 = bottomSheetState) {
    when (bottomSheetState) {
      BottomSheetState.Hidden -> modalSheetState.hide()
      BottomSheetState.HalfExpanded -> modalSheetState.show()
      BottomSheetState.Expanded -> modalSheetState.show()
    }
  }

  ModalBottomSheetLayout(
    sheetState = modalSheetState,
    sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    sheetBackgroundColor = LocalBackgroundTheme.current.color,
    sheetContent = sheetContent,
    content = content
  )
}

enum class BottomSheetState {
  Hidden, Expanded, HalfExpanded;

  companion object {
    fun BottomSheetState.toModalBottomSheetValue(): ModalBottomSheetValue {
      return when (this) {
        Hidden -> ModalBottomSheetValue.Hidden
        Expanded -> ModalBottomSheetValue.Expanded
        HalfExpanded -> ModalBottomSheetValue.HalfExpanded
      }
    }
  }
}

@Composable
fun rememberBottomSheetState(
  bottomSheetState: BottomSheetState = BottomSheetState.HalfExpanded
): MutableState<BottomSheetState> = remember { mutableStateOf(bottomSheetState) }
