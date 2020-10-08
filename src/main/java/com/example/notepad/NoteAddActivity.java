package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NoteAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        setTitle("Add Note");
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

            notepadSave();

            Intent intent = new Intent(this, MainActivity.class);
           /* startActivityForResult(intent, 3);*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void notepadSave(){

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        String title = editTextTitle.getText().toString();

        EditText editTextBody = findViewById(R.id.editTextbody);
        String body = editTextBody.getText().toString();

        String message = null;
        boolean isRun = false;

        if(title == null || title.trim().length() == 0){
            message = "제목을 입력해주세요.";
//            isRun = false;
        } else if(body == null || body.trim().length() == 0){
            message = "내용을 입력해주세요.";
//            isRun = false;
        }else{
            isRun = true;
        }

        if(isRun){
            NotepadDbAdapter notepadDbAdapter = new NotepadDbAdapter(this);
            notepadDbAdapter.open();
            long pk = notepadDbAdapter.createNote(title, body);
            notepadDbAdapter.close();

            if(pk>0){
                message = "정상적으로 입력되었습니다.";
            }else {
                message = "입력에 실패하였습니다.";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note Add");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}