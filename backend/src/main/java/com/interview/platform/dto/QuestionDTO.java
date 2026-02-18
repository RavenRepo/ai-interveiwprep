package com.interview.platform.dto;

public class QuestionDTO {

    private String questionText;
    private String category;
    private String difficulty;

    public QuestionDTO() {
    }

    public QuestionDTO(String questionText, String category, String difficulty) {
        this.questionText = questionText;
        this.category = category;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "QuestionDTO{" +
                "questionText='" + questionText + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
