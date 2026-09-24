package com.example.flashcardquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class QuizAcivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView tvCardContent;
    private FloatingActionButton btnEditCard, btnDeleteCard, btnAddCard;
    private Button btnPrev, btnShowAnswer, btnNext;

    private DatabaseHelper databaseHelper;
    private List<Flashcard> flashcardList;
    private int currentIndex = 0;
    private boolean isShowingAnswer = false;
    private CardView flashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_acivity);

        tvCardContent = findViewById(R.id.tvCardContent);
        btnEditCard = findViewById(R.id.btnEditCard);
        btnDeleteCard = findViewById(R.id.btnDeleteCard);
        btnAddCard = findViewById(R.id.btnAddCard);
        btnPrev = findViewById(R.id.btnPrev);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        btnNext = findViewById(R.id.btnNext);
        backButton = findViewById(R.id.btnBack);
        flashcard = findViewById(R.id.flashCard);

        // Initialize SQLite Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Load data from SQLite database instead of sample list
        loadFlashcardsFromDB();
        updateCardDisplay();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizAcivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnNext.setOnClickListener(v -> {
            if (flashcardList != null && !flashcardList.isEmpty()) {
                currentIndex = (currentIndex + 1) % flashcardList.size();
                isShowingAnswer = false;
                btnShowAnswer.setText("Show answer");
                updateCardDisplay();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (flashcardList != null && !flashcardList.isEmpty()) {
                currentIndex = (currentIndex - 1 + flashcardList.size()) % flashcardList.size();
                isShowingAnswer = false;
                btnShowAnswer.setText("Show answer");
                updateCardDisplay();
            }
        });

        btnShowAnswer.setOnClickListener(v -> {
            if (flashcardList != null && !flashcardList.isEmpty()) {
                isShowingAnswer = !isShowingAnswer;
                flipCardAnimation();
            }
        });

        // 1️⃣ Add Button Click (Saves to SQLite DB)
        btnAddCard.setOnClickListener(v -> {
            AddCardFragment addDialog = new AddCardFragment();
            addDialog.setOnCardSavedListener((question, answer) -> {
                databaseHelper.addFlashcard(question, answer);
                loadFlashcardsFromDB();
                currentIndex = flashcardList.size() - 1; // Jump to the newly added card
                isShowingAnswer = false;
                updateCardDisplay();
            });
            addDialog.show(getSupportFragmentManager(), "AddCardDialog");
        });

        // 2️⃣ Edit Button Click (Updates in SQLite DB)
        btnEditCard.setOnClickListener(v -> {
            if (flashcardList.isEmpty()) {
                Toast.makeText(QuizAcivity.this, "No cards to edit", Toast.LENGTH_SHORT).show();
                return;
            }

            Flashcard currentCard = flashcardList.get(currentIndex);

            AddCardFragment editDialog = new AddCardFragment();
            editDialog.setInitialData(currentCard.getQuestion(), currentCard.getAnswer());
            editDialog.setOnCardSavedListener((question, answer) -> {
                databaseHelper.updateFlashcard(currentCard.getId(), question, answer);
                loadFlashcardsFromDB();
                isShowingAnswer = false;
                updateCardDisplay();
            });
            editDialog.show(getSupportFragmentManager(), "EditCardDialog");
        });

        // 3️⃣ Delete Button Click (Removes from SQLite DB)
        btnDeleteCard.setOnClickListener(v -> {
            if (flashcardList.isEmpty()) {
                Toast.makeText(QuizAcivity.this, "No cards to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            Flashcard currentCard = flashcardList.get(currentIndex);
            databaseHelper.deleteFlashcard(currentCard.getId());

            loadFlashcardsFromDB();

            if (flashcardList.isEmpty()) {
                tvCardContent.setText("NO FLASHCARDS AVAILABLE\nTAP '+' TO ADD ONE");
                currentIndex = 0;
            } else {
                if (currentIndex >= flashcardList.size()) {
                    currentIndex = flashcardList.size() - 1;
                }
                isShowingAnswer = false;
                updateCardDisplay();
            }

            Toast.makeText(QuizAcivity.this, "Card deleted", Toast.LENGTH_SHORT).show();
        });
    }

    private void flipCardAnimation() {
        float scale = getResources().getDisplayMetrics().density;
        flashcard.setCameraDistance(8000 * scale);

        android.animation.ObjectAnimator flipOut = android.animation.ObjectAnimator.ofFloat(flashcard, "rotationY", 0f, 90f);
        flipOut.setDuration(150);

        android.animation.ObjectAnimator flipIn = android.animation.ObjectAnimator.ofFloat(flashcard, "rotationY", -90f, 0f);
        flipIn.setDuration(150);

        flipOut.addListener(new android.animation.AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                updateCardDisplay();
                flipIn.start();
            }
        });

        flipOut.start();
    }

    private void loadFlashcardsFromDB() {
        flashcardList = databaseHelper.getAllFlashcards();
    }

    private void updateCardDisplay() {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Flashcard currentCard = flashcardList.get(currentIndex);
            if (isShowingAnswer) {
                tvCardContent.setText(currentCard.getAnswer());
                btnShowAnswer.setText("Show question");
            } else {
                tvCardContent.setText(currentCard.getQuestion());
                btnShowAnswer.setText("Show answer");
            }
        }
    }
}