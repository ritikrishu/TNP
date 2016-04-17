package android.tnp.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.tnp.chat.ChatMessage;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SANYAM TYAGI on 4/1/2016.
 */
public class CreateDB extends SQLiteOpenHelper {

    public static String DATABASENAME="database.db";
    public static String TABLEDISPLAY="Display_Data";
    public static String ID="ID";
    public static String SUBJECT="SUBJECT";
    public static String DATE="DATE";
    public static String DATA="DATA";

    public static String TABLECHAT="Chat_Histroy";
    public static String MESSAGE="MESSAGE";
    public static String ISME="ISME";


    //call the constructor to create the db.
    public CreateDB(Context context) {
        super(context, DATABASENAME, null, 1);
        //create the db
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery="create table "+TABLEDISPLAY+" ("+ID+" INTEGER,"+SUBJECT+" TEXT," +
               DATA+" TEXT)";
        db.execSQL(createQuery);
        createQuery="create table "+TABLECHAT+" ("+MESSAGE+" TEXT," +
                DATE+" TEXT,"+ISME+" BOOLEAN)";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(ArrayList<BeanPlacementData> list){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i=0;i<list.size();i++){
            if(list.get(i).getSubject().replace("Fwd: ","").length()>1){
                cv = new ContentValues();
                cv.put(SUBJECT,list.get(i).getSubject().replace("Fwd: ",""));
                cv.put(ID,list.get(i).getNum());
                cv.put(DATA,filterContent(Html.fromHtml(list.get(i).getData()).toString()));
                db.insert(TABLEDISPLAY, null, cv);
            }

        }
    }
    public void insertChatData(ChatMessage chatMessage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MESSAGE,chatMessage.getMessage());
        cv.put(DATE,chatMessage.getDate());
        cv.put(ISME,chatMessage.getIsme());
        db.insert(TABLECHAT, null, cv);
    }



    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TABLEDISPLAY+" ORDER BY ID DESC",null);
        return result;
    }

    public Cursor getChatData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TABLECHAT,null);
        return result;
    }

    public String getLastId(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM Display_Data ORDER BY ID DESC LIMIT 1 ",null);
        if(result.moveToFirst())
            return result.getString(result.getColumnIndex("ID"));
        else
            return "0";
    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLEDISPLAY,null,null);

    }
    private String filterContent(String str) {
        String[] data = str.split("\n");
        boolean flag = false;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (data[i].contains("Cc:")) {
                flag = true;
                continue;
            }

            if (flag) {
                if (data[i].trim().equalsIgnoreCase("::Disclaimer::")) {
                    break;
                }
                result.append(data[i]);
                result.append("\n");
            }

        }
        return result.toString();

    }

}


