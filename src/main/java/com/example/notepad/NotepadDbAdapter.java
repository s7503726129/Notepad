package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotepadDbAdapter {
    //Table 에서 사용되는 Column name 들
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    //주로 log 에서 사용되어질 구문 - 앱을 구분하기 위함
    private static final String TAG = "NotesDBAdapter";

    //Database 를 생성하고 업그레이드하는데 필요한 클래스
    private DatabaseHelper databaseHelper;
    //Database 에 직접 작업(CRUD)하기 위해서 필요한 클래스
    private SQLiteDatabase sqLiteDatabase;

    //Table 생성 SQl문
    private static final String DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, "
            + "title text not null , body text not null);";

    //Database 명(파일명), Phone 에서 data/date/패키지명/database/하위에 해당 파일 생성됨
    private static final String DATABASE_NAME = "data";
    //table name
    private static final String DATABASE_TABLE = "notes";
    //Database 변경(업그레이드) 시 관리를 위해서 사용되는 버전
    private static final int DATABASE_VERSION = 2;
    //클래스에서 필요시 사용하기 위한 Context
    private final Context context;

    //Database 생성과 업그렝드를 위한 담당하는 클래스
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * 생성자에서 Database 를 생성하여, App 설치가 한번 호출된다.
         * @param context
         */
        DatabaseHelper(Context context) {
            //부모생성자를 호출하여 Database 를 생성
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Database 를 생성한 후에 한번 호출된다, 주로 Table 생성 등의 초기화를 처리한다.
         * @param sqLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //sql 을 직접 실행하여 테이블을 생성한다.
            sqLiteDatabase.execSQL(DATABASE_CREATE);
        }

        /**
         *
         * @param sqLiteDatabase
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + " which will destroy all old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(sqLiteDatabase);

        }

    }

    /**
     * 생성자. 클래스 내부에 필요한 Context 를 Parameter 로 전달받는다.
     * @param context
     */
    public NotepadDbAdapter(Context context){
        this.context = context;
    }

    //Database 를 open 한다. 데이터를 변경할 때 항상 Open 을 수행한 후에 Database 작업을 수행한다.
    public NotepadDbAdapter open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    //Database 사용후에 close 한다.
    public void close(){
        databaseHelper.close();
    }

    /**
     * //Note 정보를 Table 에 저장한다
     * @param title
     * @param body
     * @return 저장된 Note 의 PK 값
     */
    public long createNote(String title, String body){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        return sqLiteDatabase.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * note 를 삭제한다.
     * @param rowId 삭제할 해당 Note 의 PK 값
     * @return 정상적으로 수정되면 true, 아니면 false 리턴
     */
    public boolean deleteNote(long rowId){

        return sqLiteDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * 전체 데이터를 조회한다.
     * @return
     */
    public Cursor fetchAllNotes() {
        return sqLiteDatabase.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY}, null,null,null, null,null);
    }

    /**
     * 한 건의 데이터를 조회한다.
     * @param rowId 조회할 데이터의 PK 값
     * @return
     * @throws SQLException
     */
    public Cursor fetchNote(long rowId) throws SQLException{
        Cursor mCursor = sqLiteDatabase.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * 데이터를 수정한다.
     * @param rowId 수정할 데이터의 PK 값
     * @param title
     * @param body
     * @return 정상적으로 수정되면 true, 아니면 false 리턴
     */
    public boolean updateNote(long rowId, String title, String body){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_BODY, body);

        int count = sqLiteDatabase.update(DATABASE_TABLE, contentValues, KEY_ROWID + "=" + rowId, null);

        return count > 0;
    }
}
