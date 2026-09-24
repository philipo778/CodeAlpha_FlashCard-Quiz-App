package com.example.flashcardquizapp;

public class AddCardFragment extends androidx.fragment.app.DialogFragment {

    private com.google.android.material.textfield.TextInputEditText etQuestion, etAnswer;
    private android.widget.Button btnSaveCard;

    private String initialQuestion = "";
    private String initialAnswer = "";

    // Method to pass existing data for editing
    public void setInitialData(String question, String answer) {
        this.initialQuestion = question;
        this.initialAnswer = answer;
    }

    public interface OnCardSavedListener {
        void onCardSaved(String question, String answer);
    }

    private OnCardSavedListener listener;

    public void setOnCardSavedListener(OnCardSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                                          android.os.Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_add_card, container, false);

        etQuestion = view.findViewById(R.id.etQuestion);
        etAnswer = view.findViewById(R.id.etAnswer);
        btnSaveCard = view.findViewById(R.id.btnSaveCard);

        // Pre-fill fields if editing an existing card
        if (initialQuestion != null && !initialQuestion.isEmpty()) {
            etQuestion.setText(initialQuestion);
        }
        if (initialAnswer != null && !initialAnswer.isEmpty()) {
            etAnswer.setText(initialAnswer);
        }

        btnSaveCard.setOnClickListener(v -> {
            String question = etQuestion.getText().toString().trim();
            String answer = etAnswer.getText().toString().trim();

            if (question.isEmpty()) {
                etQuestion.setError("Please enter a question");
                return;
            }

            if (answer.isEmpty()) {
                etAnswer.setError("Please enter an answer");
                return;
            }

            if (listener != null) {
                listener.onCardSaved(question, answer);
            }

            dismiss();
        });

        return view;
    }
}