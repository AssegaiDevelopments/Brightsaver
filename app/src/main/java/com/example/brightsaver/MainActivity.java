package com.example.brightsaver;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private BrightnessManager brightnessManager;
    private TextView brightnessLabel, statusLabel;
    private SeekBar brightnessSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brightnessManager = new BrightnessManager(this);
        buildInterface();
        updateBrightnessLabel(brightnessManager.getWindowBrightnessPercent());
    }

    private void buildInterface() {
        String appName = getString(R.string.app_name);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setBackgroundColor(Color.rgb(12, 20, 32));

        TextView title = new TextView(this);
        title.setText(appName);
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText(getString(R.string.subtitle));
        subtitle.setTextColor(Color.rgb(165, 190, 220));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = matchWrap();
        subParams.topMargin = dp(6);
        root.addView(subtitle, subParams);

        TextView info = new TextView(this);
        info.setText(R.string.info);
        info.setTextColor(Color.rgb(225, 232, 242));
        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);
        info.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams infoParams = matchWrap();
        infoParams.topMargin = dp(22);
        infoParams.bottomMargin = dp(20);
        root.addView(info, infoParams);

        TextView presetTitle = new TextView(this);
        presetTitle.setText(R.string.preset_title);
        presetTitle.setTextColor(Color.rgb(120, 190, 255));
        presetTitle.setTextSize(13);
        presetTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(presetTitle, matchWrap());

        addPresetButton(root, new BrightnessPreset("Eco Mode", 35, "Balanced brightness for everyday use"));
        addPresetButton(root, new BrightnessPreset("Solar Mode", 70, "Brighter screen for well-lit environments"));
        addPresetButton(root, new BrightnessPreset("Night Saver", 15, "Dim brightness for dark environments"));

        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(50, 68, 88));
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divParams.topMargin = dp(22);
        divParams.bottomMargin = dp(18);
        root.addView(divider, divParams);

        brightnessLabel = new TextView(this);
        brightnessLabel.setTextColor(Color.WHITE);
        brightnessLabel.setTextSize(18);
        brightnessLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(brightnessLabel, matchWrap());

        brightnessSeekBar = new SeekBar(this);
        brightnessSeekBar.setMax(100);
        brightnessSeekBar.setProgress(brightnessManager.getWindowBrightnessPercent());
        LinearLayout.LayoutParams seekParams = matchWrap();
        seekParams.topMargin = dp(8);
        root.addView(brightnessSeekBar, seekParams);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBrightnessLabel(progress);
                if (fromUser) brightnessManager.setWindowBrightnessGlobal(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        statusLabel = new TextView(this);
        statusLabel.setTextColor(Color.rgb(165, 190, 220));
        statusLabel.setTextSize(12);
        statusLabel.setGravity(Gravity.CENTER);
        statusLabel.setText(R.string.status_label);
        LinearLayout.LayoutParams statusParams = matchWrap();
        statusParams.topMargin = dp(14);
        root.addView(statusLabel, statusParams);

        Button systemButton = new Button(this);
        systemButton.setText(R.string.system_button);
        LinearLayout.LayoutParams buttonParams = matchWrap();
        buttonParams.topMargin = dp(16);
        root.addView(systemButton, buttonParams);

        //reworked global brightness
        systemButton.setOnClickListener(v -> {
            if (!Settings.System.canWrite(MainActivity.this)) {
                requestSystemBrightnessPermission();
            } else {
                int percent = brightnessSeekBar.getProgress();

                boolean success =
                        brightnessManager.setWindowBrightnessGlobal(percent);

                if (success) {
                    statusLabel.setText(R.string.status_label_success_text);
                    Toast.makeText(
                            MainActivity.this,
                            "System brightness updated",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    statusLabel.setText(R.string.status_label_fail_text);
                }
            }
        });


        TextView footer = new TextView(this);
        footer.setText(R.string.footer);
        footer.setTextColor(Color.rgb(120, 145, 170));
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footParams = matchWrap();
        footParams.topMargin = dp(18);
        root.addView(footer, footParams);

        setContentView(root);
    }

    private void addPresetButton(LinearLayout root, BrightnessPreset preset) {
        Button button = new Button(this);
        button.setText(getString(R.string.add_preset_button_text,preset.getName(),preset.getBrightnessPercent()));
        //button.setText(preset.getName() + "  •  " + preset.getBrightnessPercent() + "%");
        button.setAllCaps(false);
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(8);
        root.addView(button, params);
        button.setOnClickListener(v -> {
            if(Settings.System.canWrite(this))brightnessManager.setWindowBrightnessGlobal(preset.getBrightnessPercent());
                    else brightnessManager.setWindowBrightness(preset.getBrightnessPercent());
            brightnessSeekBar.setProgress(preset.getBrightnessPercent());
            statusLabel.setText(preset.getDescription());
            Toast.makeText(this, preset.getName() + " applied", Toast.LENGTH_SHORT).show();
        });
    }

    private void requestSystemBrightnessPermission() {
        if (!Settings.System.canWrite(this)) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Open Android Settings to grant modify system settings.", Toast.LENGTH_LONG).show();
            }
        } else {
            statusLabel.setText(R.string.request_system_brightness_permission_text);
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBrightnessLabel(int percent) {
        if (brightnessLabel != null) brightnessLabel.setText(getString(R.string.update_brightness_level_text, percent));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (brightnessManager != null && brightnessSeekBar != null) {
            int percent =
                    brightnessManager.getWindowBrightnessPercent();

            brightnessSeekBar.setProgress(percent);
            updateBrightnessLabel(percent);
        }
    }


    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
