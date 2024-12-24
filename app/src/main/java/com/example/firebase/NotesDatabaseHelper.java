package com.example.firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotesDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOTE = "note";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NOTE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a note
    public void addNote(String note) {
        if (note == null || note.trim().isEmpty()) {
            Log.e("Database", "Attempted to add a null or empty note.");
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTE, note);
            db.insert(TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e("Database Error", "Error inserting note: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public int updateNote(int id, String newNote) {
        if (newNote == null || newNote.trim().isEmpty()) {
            Log.e("Database", "Attempted to update to a null or empty note.");
            return 0;
        }

        SQLiteDatabase db = null;
        int rowsUpdated = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTE, newNote);
            rowsUpdated = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            Log.e("Database Error", "Error updating note: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
        return rowsUpdated;
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            if (rowsDeleted > 0) {
                Log.d("Delete Note", "Note with id " + id + " deleted successfully.");
            } else {
                Log.d("Delete Note", "No note found with id " + id + " to delete.");
            }
        } catch (SQLException e) {
            Log.e("Database Error", "Error deleting note: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }

    public String getNoteById(int id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String note = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME, new String[]{COLUMN_NOTE}, COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));
            }
        } catch (SQLException e) {
            Log.e("Database Error", "Error fetching note: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return note;
    }
}
