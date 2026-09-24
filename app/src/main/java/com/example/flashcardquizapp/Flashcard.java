package com.example.flashcardquizapp;

public class Flashcard {
    private String question;
    private String answer;
    private int id;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Flashcard(String question, String answer, int id) {
        this.question = question;
        this.answer = answer;
        this.id = id;
    }
}
