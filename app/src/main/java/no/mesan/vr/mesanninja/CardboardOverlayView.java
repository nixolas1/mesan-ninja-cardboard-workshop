/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.mesan.vr.mesanninja;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Contains two sub-views to provide a simple stereo HUD.
 */
public class CardboardOverlayView extends LinearLayout {
  private final CardboardOverlayEyeView leftView;
  private final CardboardOverlayEyeView rightView;
  private AlphaAnimation textFadeAnimation;

  public CardboardOverlayView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOrientation(HORIZONTAL);

    LayoutParams params = new LayoutParams(
      LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
    params.setMargins(0, 0, 0, 0);

    leftView = new CardboardOverlayEyeView(context, attrs);
    leftView.setLayoutParams(params);
    addView(leftView);

    rightView = new CardboardOverlayEyeView(context, attrs);
    rightView.setLayoutParams(params);
    addView(rightView);

    // Set some reasonable defaults.
    setDepthOffset(0.01f);
    setColor(Color.rgb(150, 255, 180));
    setVisibility(View.VISIBLE);

    textFadeAnimation = new AlphaAnimation(1.0f, 0.0f);
    textFadeAnimation.setDuration(5000);
  }

  public void show3DToast(String message) {
    setText(message);
    setTextAlpha(1f);
    textFadeAnimation.setAnimationListener(new EndAnimationListener() {
      @Override
      public void onAnimationEnd(Animation animation) {
        setTextAlpha(0f);
      }
    });
    startAnimation(textFadeAnimation);
  }

  private abstract class EndAnimationListener implements Animation.AnimationListener {
    @Override
    public void onAnimationRepeat(Animation animation) {}
    @Override
    public void onAnimationStart(Animation animation) {}
  }

  private void setDepthOffset(float offset) {
    leftView.setOffset(offset);
    rightView.setOffset(-offset);
  }

  private void setText(String text) {
    leftView.setText(text);
    rightView.setText(text);
  }

  private void setTextAlpha(float alpha) {
    leftView.setTextViewAlpha(alpha);
    rightView.setTextViewAlpha(alpha);
  }

  private void setColor(int color) {
    leftView.setColor(color);
    rightView.setColor(color);
  }
}
