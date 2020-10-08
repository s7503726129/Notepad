package com.example.notepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewNoteAdapter extends RecyclerView.Adapter<RecyclerViewNoteAdapter.NoteViewHolder> {

    private List<Note> noteList;

    private OnClickRowLstener onClickRowListener;

    public RecyclerViewNoteAdapter(List<Note> noteList){ //생성자
        this.noteList = noteList;
    }

    public RecyclerViewNoteAdapter(List<Note> noteList, OnClickRowLstener onClickRowListener){
        this.noteList = noteList;
        this.onClickRowListener = onClickRowListener;
    }

    public void setNoteList(List<Note> noteList){   //객체가 생성된 후 값이 변경된 경우
        this.noteList = noteList;
        notifyDataSetChanged();                     //값이 변경되면 RecyclerViewNoteAdapter 에 리스트 갱신 요청
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Layout file 로 부터 view 객체를 가져온다.
        //한 행을 표시하기 위한 디자인 파일
        //inflate() - xml을 읽어서 view 객체를 생성함
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, parent, false);

        //한 row 에 해당하는 디자인에 데이터를 표시하는 객체
        NoteViewHolder noteViewHolder = new NoteViewHolder(view);

        return noteViewHolder;
    }

    /**
     * 전체 건수만큼 루틴하면서 타이틀과 이미지 데이터 설정
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = noteList.get(position);

        holder.textViewTitle.setText(note.getTitle());
        holder.imageView.setImageResource(R.drawable.ic_baseline_audiotrack_24);
    }

    /**
     * 전체 건수 체크
     * @return
     */
    @Override
    public int getItemCount() {

        int count = 0;

        if(noteList != null && noteList.size() > 0){
            count = noteList.size();
        }
        return count;
    }

    //각 행마다 이미지와 텍스트 표시하기위해 참조
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        ImageView imageView;

        public NoteViewHolder(View view){
            super(view);
            textViewTitle = view.findViewById(R.id.textViewtitle);
            imageView = view.findViewById(R.id.imageView);


            view.setOnClickListener(onClickListener);
            view.setOnLongClickListener(onLongClickListener);

        }

        //리스트 클릭시 NoteEditActivity 로 이동
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = getBindingAdapterPosition();
                onClickRowListener.onClick(position);
                /*            Note note = noteList.get(position);

                Intent intent = new Intent(view.getContext(), NoteEditActivity.class);
                intent.putExtra("noteId", note.getId());
                view.getContext().startActivity(intent);*/
            }

        };

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = getBindingAdapterPosition();

                onClickRowListener.onLongClick(position);

                return false;
            }
        };

    }

}
