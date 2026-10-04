package com.example.brightsaver;


public class BrightnessPreset {
    private final String name;
    private final int brightnessPercent;
    private final String description;

    public BrightnessPreset(String name, int brightnessPercent, String description) {
        this.name = name;
        this.brightnessPercent = Math.max(1, Math.min(100, brightnessPercent));
        this.description = description;
    }

    public String getName() { return name; }
    public int getBrightnessPercent() { return brightnessPercent; }
    public String getDescription() { return description; }
}
