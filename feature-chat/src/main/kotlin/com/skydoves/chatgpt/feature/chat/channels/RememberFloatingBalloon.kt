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

package com.skydoves.chatgpt.feature.chat.channels

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import com.skydoves.balloon.compose.setTextColor
import com.skydoves.chatgpt.core.designsystem.theme.PURPLE500

@Composable
fun rememberFloatingBalloon() = rememberBalloonBuilder {
  setWidthRatio(1.0f)
  setHeight(BalloonSizeSpec.WRAP)
  setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
  setArrowOrientation(ArrowOrientation.BOTTOM)
  setArrowPosition(0.5f)
  setArrowSize(10)
  setPadding(12)
  setMarginHorizontal(12)
  setTextSize(15f)
  setCornerRadius(8f)
  setTextColor(Color.White)
  setBackgroundColor(PURPLE500)
  setBalloonAnimation(BalloonAnimation.ELASTIC)
}
