package com.example.brightsaver;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.view.WindowManager;
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
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(12, 20, 32));

        TextView title = new TextView(this);
        title.setText("BrightSaver");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Smart Screen Dimmer Utility");
        subtitle.setTextColor(Color.rgb(165, 190, 220));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = matchWrap();
        subParams.topMargin = dp(6);
        root.addView(subtitle, subParams);

        TextView info = new TextView(this);
        info.setText("Choose a power-saving preset or adjust brightness manually. Lower brightness may help reduce screen power use.");
        info.setTextColor(Color.rgb(225, 232, 242));
        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);
        info.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams infoParams = matchWrap();
        infoParams.topMargin = dp(22);
        infoParams.bottomMargin = dp(20);
        root.addView(info, infoParams);

        TextView presetTitle = new TextView(this);
        presetTitle.setText("QUICK PRESETS");
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
                if (fromUser) brightnessManager.setWindowBrightness(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        statusLabel = new TextView(this);
        statusLabel.setTextColor(Color.rgb(165, 190, 220));
        statusLabel.setTextSize(12);
        statusLabel.setGravity(Gravity.CENTER);
        statusLabel.setText("Changes apply to this app window. System-wide control may require permission.");
        LinearLayout.LayoutParams statusParams = matchWrap();
        statusParams.topMargin = dp(14);
        root.addView(statusLabel, statusParams);

        Button systemButton = new Button(this);
        systemButton.setText("Allow system brightness control");
        LinearLayout.LayoutParams buttonParams = matchWrap();
        buttonParams.topMargin = dp(16);
        root.addView(systemButton, buttonParams);
        systemButton.setOnClickListener(v -> requestSystemBrightnessPermission());

        TextView footer = new TextView(this);
        footer.setText("BrightSaver • GreenIT Project");
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
        button.setText(preset.getName() + "  •  " + preset.getBrightnessPercent() + "%");
        button.setAllCaps(false);
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(8);
        root.addView(button, params);
        button.setOnClickListener(v -> {
            brightnessManager.setWindowBrightness(preset.getBrightnessPercent());
            brightnessSeekBar.setProgress(preset.getBrightnessPercent());
            statusLabel.setText(preset.getDescription());
            Toast.makeText(this, preset.getName() + " applied", Toast.LENGTH_SHORT).show();
        });
    }

    private void requestSystemBrightnessPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
                && !Settings.System.canWrite(this)) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Open Android Settings to grant modify system settings.", Toast.LENGTH_LONG).show();
            }
        } else {
            statusLabel.setText("System brightness permission is already granted. Use Android's system brightness setting if needed.");
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBrightnessLabel(int percent) {
        if (brightnessLabel != null) brightnessLabel.setText("Screen brightness: " + percent + "%");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (brightnessManager != null && brightnessSeekBar != null) {
            brightnessSeekBar.setProgress(brightnessManager.getWindowBrightnessPercent());
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
