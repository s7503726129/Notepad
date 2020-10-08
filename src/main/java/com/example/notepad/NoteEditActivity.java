package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class NoteEditActivity extends AppCompatActivity {

    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        setTitle("Note Edit");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        noteId = bundle.getInt("noteId");

        NotepadDbAdapter notepadDbAdapter = new NotepadDbAdapter(this);
        notepadDbAdapter.open();
        Cursor cursor = notepadDbAdapter.fetchNote(noteId);

        if(cursor.moveToFirst()){
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String body = cursor.getString(cursor.getColumnIndex("body"));

            EditText editTextTitle = findViewById(R.id.editTextTitle);
            EditText editTextBody = findViewById(R.id.editTextbody);

            editTextTitle.setText(title);
            editTextBody.setText(body);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int menuItemId = item.getItemId();
        if(menuItemId == R.id.save_menu){
            notepadUpdate();
/*            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);*/ //startActivity가 새로 생성되어서 화면에 보여짐
        }
        return super.onOptionsItemSelected(item);
    }

    public void notepadUpdate() {

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        String title = editTextTitle.getText().toString();

        EditText editTextBody = findViewById(R.id.editTextbody);
        String body = editTextBody.getText().toString();

        String message = null;
        boolean isRun = false;

        if(title == null || title.trim().length() == 0){
            message = "제목을 입력하시오.";
            //            isRun = false;
        }else if(body == null || body.trim().length() == 0){
            message = "내용을 입력하시오.";
            //            isRun = false;
        }else {
            isRun = true;
        }

        if(isRun){
            NotepadDbAdapter notepadDbAdapter = new NotepadDbAdapter(this);
            notepadDbAdapter.open();
            boolean result  = notepadDbAdapter.updateNote(noteId, title, body);
            notepadDbAdapter.close();

            if(result){
                message = "정상적으로 수정되었습니다.";
            }else {
                message = "수정에 실패하였습니다.";
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note Edit");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}