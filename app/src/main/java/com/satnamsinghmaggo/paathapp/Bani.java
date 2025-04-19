package com.satnamsinghmaggo.paathapp;

public class Bani {
    private final String title;
    private final String description;
    private final String content;
    private final String audioUrl;

    public Bani(String title, String description, String content, String audioUrl) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.audioUrl = audioUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
} 