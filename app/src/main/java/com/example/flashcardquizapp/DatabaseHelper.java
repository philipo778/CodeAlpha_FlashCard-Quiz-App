package com.example.flashcardquizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "flashcards";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION + " TEXT, "
                + COLUMN_ANSWER + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new flashcard into SQLite database
    public void addFlashcard(String question, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve all flashcards from SQLite database
    public List<Flashcard> getAllFlashcards() {
        List<Flashcard> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION));
                String answer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));
                list.add(new Flashcard(id, question, answer));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // If database is completely empty, insert sample data automatically
// If database is completely empty, insert real software engineering sample data automatically
        if (list.isEmpty()) {
            addFlashcard("What is Java?", "An object-oriented, platform-independent programming language.");
            addFlashcard("What is an Android Activity?", "A single, focused screen with a user interface that the user can interact with.");
            addFlashcard("What does SQL stand for?", "Structured Query Language, used for storing and managing data in relational databases.");
            return getAllFlashcards();
        }

        return list;
    }

    // Update an existing flashcard by ID
    public void updateFlashcard(int id, String question, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete a flashcard from SQLite database by ID
    public void deleteFlashcard(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}