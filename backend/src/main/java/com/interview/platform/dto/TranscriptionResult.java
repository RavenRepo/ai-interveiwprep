package com.interview.platform.dto;

/**
 * Holds the result of a speech-to-text transcription.
 */
public class TranscriptionResult {

    private String text;
    private double confidence;

    public TranscriptionResult() {
    }

    public TranscriptionResult(String text, double confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "TranscriptionResult{" +
                "text='" + (text != null && text.length() > 80 ? text.substring(0, 80) + "..." : text) + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
