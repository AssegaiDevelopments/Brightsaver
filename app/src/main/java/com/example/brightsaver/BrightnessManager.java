package com.example.brightsaver;

import android.app.Activity;
import android.provider.Settings;
import android.view.WindowManager;


public class BrightnessManager {
    private final Activity activity;
    public BrightnessManager(Activity activity) {
        this.activity = activity;
    }

    public void setWindowBrightness(int percent) {
        int safePercent = Math.max(1, Math.min(100, percent));
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = safePercent / 100.0f;
        activity.getWindow().setAttributes(params);
    }

    public int getWindowBrightnessPercent() {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        if (params.screenBrightness < 0) return 50;
        return Math.max(1, Math.min(100, Math.round(params.screenBrightness * 100)));
    }

    public boolean setWindowBrightnessGlobal(int percent) {
        // int 0-255 for global settings
        int safePercent = Math.max(1, Math.min(100, percent));
        int brightness = Math.round(safePercent * 255.0f / 100.0f);

        if (!Settings.System.canWrite(activity)) {
            return false;
        }

        return Settings.System.putInt(
                activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                brightness
        );
    }
}
