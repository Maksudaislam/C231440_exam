package com.example.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;

    public NotesAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @SuppressLint("Range")
    @Override
    public Object getItem(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getString(cursor.getColumnIndex("note"));
        }
        return null;
    }

    @SuppressLint("Range")
    @Override
    public long getItemId(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getInt(cursor.getColumnIndex("id"));
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        if (cursor != null && cursor.moveToPosition(position)) {
            @SuppressLint("Range") String note = cursor.getString(cursor.getColumnIndex("note"));
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(note);
        }

        return convertView;
    }

    // Clean up the cursor when it's no longer needed
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
