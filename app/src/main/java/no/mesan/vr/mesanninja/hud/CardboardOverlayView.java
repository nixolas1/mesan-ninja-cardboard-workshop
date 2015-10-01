package no.mesan.vr.mesanninja.hud;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import no.mesan.vr.mesanninja.MainActivity;

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

    public void show3DToast(final String message) {
        ((MainActivity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });

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

    private abstract class EndAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }
}
