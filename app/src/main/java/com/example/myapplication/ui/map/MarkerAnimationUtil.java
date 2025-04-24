package com.example.myapplication.ui.map;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import com.google.android.gms.maps.model.Marker;
public class MarkerAnimationUtil {
    private static ValueAnimator bounceAnimator;
    private static ValueAnimator dropAnimator;
    public static void startBounceAnimation(Marker marker) {
        // Hủy animation cũ nếu có
        stopAllAnimations();

        // Tạo animation nhảy lên xuống
        bounceAnimator = ValueAnimator.ofFloat(0, 10, 0);
        bounceAnimator.setDuration(500);
        bounceAnimator.setRepeatCount(ValueAnimator.INFINITE);
        bounceAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bounceAnimator.addUpdateListener(animation -> {
            float animValue = (float) animation.getAnimatedValue();
            if (marker != null && marker.isVisible()) {
                marker.setAnchor(0.5f, 1.0f - animValue / 100);
            }
        });
        bounceAnimator.start();
    }

    public static void startDropAnimation(Marker marker) {
        // Hủy animation cũ nếu có
        stopAllAnimations();

        // Tạo animation thả xuống có nảy
        dropAnimator = ValueAnimator.ofFloat(10, 0);
        dropAnimator.setDuration(300);
        dropAnimator.setInterpolator(new BounceInterpolator());
        dropAnimator.addUpdateListener(animation -> {
            float animValue = (float) animation.getAnimatedValue();
            if (marker != null && marker.isVisible()) {
                marker.setAnchor(0.5f, 1.0f - animValue / 100);
            }
        });
        dropAnimator.start();
    }

    public static void stopAllAnimations() {
        if (bounceAnimator != null && bounceAnimator.isRunning()) {
            bounceAnimator.cancel();
        }

        if (dropAnimator != null && dropAnimator.isRunning()) {
            dropAnimator.cancel();
        }
    }
}