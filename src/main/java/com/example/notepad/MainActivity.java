package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView; //화면에 보이는 역할
    private RecyclerViewNoteAdapter recyclerViewNoteAdapter; //실질적인 데이터 작업
    private RecyclerView.LayoutManager layoutManager; //화면에 추가

    private List<Note> noteList; //데이터값을 가져옴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView =findViewById(R.id.recyclerViewNote); //전체적인 화면의 큰 틀 참조
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchAllNotes(); //데이터 조회

        OnClickRowLstener onClickRowlLstener = new OnClickRowLstener() {
            @Override
            public void onClick(int position) {
                Note note = noteList.get(position);

                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                intent.putExtra("noteId", note.getId());

                startActivityForResult(intent, 2);
            }

            @Override
            public void onLongClick(final int position) {
                Note note = noteList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Note Delete");
                builder.setMessage("삭제 하시겠습니까?");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note note = noteList.get(position);

                        NotepadDbAdapter notepadDbAdapter = new NotepadDbAdapter(MainActivity.this);
                        notepadDbAdapter.open();
                        boolean result = notepadDbAdapter.deleteNote(note.getId());
                        notepadDbAdapter.close();

                        if(result){
                            //noteList.remove(note);
                            noteList.remove(position);

                            recyclerViewNoteAdapter.setNoteList(noteList);
                        }

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };


        //recyclerViewNoteAdapter = new RecyclerViewNoteAdapter(noteList); //데이터 전송
        recyclerViewNoteAdapter = new RecyclerViewNoteAdapter(noteList, onClickRowlLstener);
        recyclerView.setAdapter(recyclerViewNoteAdapter); //화면과 데이터 연결
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * requestCode      1 : addActivity
     *                  2 : editActivity
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemId =item.getItemId();

        if(menuItemId == R.id.add_menu) {
            Intent intent = new Intent(this, NoteAddActivity.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        searchAllNotes();

        recyclerViewNoteAdapter.setNoteList(noteList);
    }

    private void searchAllNotes(){

        NotepadDbAdapter notepadDbAdapter = new NotepadDbAdapter(this);
        notepadDbAdapter.open();
        Cursor cursor = notepadDbAdapter.fetchAllNotes();

        List<Note> noteList = new ArrayList<>();

        while (cursor.moveToNext()){

            Note note = new Note();

            note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            note.setBody(cursor.getString(cursor.getColumnIndex("body")));

            noteList.add(note);
        }

        cursor.close();

        this.noteList = noteList;

    }
}