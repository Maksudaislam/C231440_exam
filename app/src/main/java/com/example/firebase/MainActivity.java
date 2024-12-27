package com.example.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button btn_out, btn_note, btn_map, btn_temp, btn_cam, saveNoteButton, showNotesButton,Camera;
    EditText noteEditText;
    ListView notesListView;
    FirebaseAuth auth;
    FirebaseUser user;
    NotesDatabaseHelper dbHelper;
    NotesAdapter notesAdapter;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text2);
        btn_out = findViewById(R.id.logout);
        btn_map = findViewById(R.id.btnLocation);
        btn_cam = findViewById(R.id.camera);
        btn_temp = findViewById(R.id.Temp);
Camera=findViewById(R.id.camera);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dbHelper = new NotesDatabaseHelper(this);

        noteEditText = findViewById(R.id.noteEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        showNotesButton = findViewById(R.id.showNotesButton);
        notesListView = findViewById(R.id.notesListView);

        if (user == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("WELCOME! " + user.getEmail());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                if (googleMap == null) {
                    Toast.makeText(MainActivity.this, "Google Map is not available", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng defaultLocation = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in Sydney"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
                }
                mMap = googleMap;
            });
        }

        // Logout button functionality
        btn_out.setOnClickListener(v -> {
            // Clear session data if needed
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            Toast.makeText(MainActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        });

        saveNoteButton.setOnClickListener(v -> {
            String note = noteEditText.getText().toString();
            if (TextUtils.isEmpty(note)) {
                Toast.makeText(MainActivity.this, "Please enter a note", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addNote(note);
                Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                noteEditText.setText("");
            }
        });
       Camera.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, ImageActivity.class);
               startActivity(intent);
           }
       });

        showNotesButton.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllNotes();
            if (cursor.getCount() == 0) {
                Toast.makeText(MainActivity.this, "No notes found", Toast.LENGTH_SHORT).show();
            } else {
                notesAdapter = new NotesAdapter(MainActivity.this, cursor);
                notesListView.setAdapter(notesAdapter);
            }
        });
    }
}
