package fightingpit.barrons1100;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AG on 02-Sep-15.
 */


public class DatabaseHelper extends SQLiteOpenHelper {
    Context DB_CONTEXT;

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        DB_CONTEXT = context;
    }



    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating Table
        for(int i=0; i < DatabaseContract.SQL_CREATE_TABLE_ARRAY.length; i++) {
            db.execSQL(DatabaseContract.SQL_CREATE_TABLE_ARRAY[i]);
        }

        // Inserting Data into table
        InputStream aInitQuery = DB_CONTEXT.getResources().openRawResource(R.raw.barrons_db_insert);
        BufferedReader aInsertReader = new BufferedReader(new InputStreamReader(aInitQuery));

        // Iterate through lines (assuming each insert has its own line and there is no other stuff)
        try {
            while (aInsertReader.ready()) {
                String aInsertStatement = aInsertReader.readLine();
                db.execSQL(aInsertStatement);
            }
            aInsertReader.close();
        }
        catch(IOException e)
        {
            Log.d("ABG", "DB Insert Failed");
        }


    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=0; i < DatabaseContract.SQL_DROP_TABLE_ARRAY.length; i++) {
            db.execSQL(DatabaseContract.SQL_DROP_TABLE_ARRAY[i]);
        }
        onCreate(db);
    }

    public Integer getCountByAlphabet(String iAlphabet,String iFavSelector){

        SQLiteDatabase db = getReadableDatabase();
        Integer returnValue = 0;
        if(iFavSelector.equalsIgnoreCase("a")){
            String selection = DatabaseContract.WordListDB.WORD + " like ?";
            String[] selectionArgs = {iAlphabet + "%"};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if(iFavSelector.equalsIgnoreCase("m")){
            String selection = DatabaseContract.WordListDB.WORD + " like ?" + " AND " + DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {iAlphabet + "%", String.valueOf(1)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if (iFavSelector.equalsIgnoreCase("u")){
            String selection = DatabaseContract.WordListDB.WORD + " like ?" + " AND " + DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {iAlphabet + "%", String.valueOf(0)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }
        db.close();
        return returnValue;
    }


    public Integer getCountBySetNumber(String iSetNumber,String iFavSelector){
        SQLiteDatabase db = getReadableDatabase();
        Integer returnValue = 0;
        if(iFavSelector.equalsIgnoreCase("a")){
            String selection = DatabaseContract.WordListDB.SET_NUMBER + "=?";
            String[] selectionArgs = {iSetNumber};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if(iFavSelector.equalsIgnoreCase("m")){
            String selection = DatabaseContract.WordListDB.SET_NUMBER + "=?" + " AND " + DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {iSetNumber, String.valueOf(1)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if (iFavSelector.equalsIgnoreCase("u")){
            String selection = DatabaseContract.WordListDB.SET_NUMBER + "=?" + " AND " + DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {iSetNumber, String.valueOf(0)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }
        db.close();
        return returnValue;
    }


    public int getWordListCount(String iFavSelector){
        SQLiteDatabase db = getReadableDatabase();
        Integer returnValue = 0;
        if(iFavSelector.equalsIgnoreCase("a")){
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, null, null, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if(iFavSelector.equalsIgnoreCase("m")){
            String selection = DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {String.valueOf(1)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }else if (iFavSelector.equalsIgnoreCase("u")){
            String selection = DatabaseContract.WordListDB.FAVOURITE + "=?";
            String[] selectionArgs = {String.valueOf(0)};
            Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);
            returnValue = c.getCount();
            c.close();
        }
        db.close();
        return returnValue;
    }

    public List<GenericContainer> getWordList(String iFavSelector){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, null, null, null, null, null);
        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();

            if(iFavSelector.equalsIgnoreCase("a")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                }else {
                    aContainer.setFavourite(false);
                }
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                aReturnList.add(aContainer);

            }else if (iFavSelector.equalsIgnoreCase("m")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                    aReturnList.add(aContainer);
                }
            }else if (iFavSelector.equalsIgnoreCase("u")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(!(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1)){
                    aContainer.setFavourite(false);
                    aReturnList.add(aContainer);
                }
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

    public List<GenericContainer> getWordListByAlphabet(String iAlphabet, String iFavSelector){
        SQLiteDatabase db = getReadableDatabase();
        //String[] projection = {DatabaseContract.WordListDB.FOOD_ITEM_NAME};
        String selection = DatabaseContract.WordListDB.WORD + " like ?";
        String[] selectionArgs = {iAlphabet + "%"};
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();

            if(iFavSelector.equalsIgnoreCase("a")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                }else {
                    aContainer.setFavourite(false);
                }
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                aReturnList.add(aContainer);

            }else if (iFavSelector.equalsIgnoreCase("m")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                    aReturnList.add(aContainer);
                }
            }else if (iFavSelector.equalsIgnoreCase("u")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(!(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1)){
                    aContainer.setFavourite(false);
                    aReturnList.add(aContainer);
                }
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

    public List<GenericContainer> getWordListBySet(String iSetNumber, String iFavSelector){
        SQLiteDatabase db = getReadableDatabase();
        String selection = DatabaseContract.WordListDB.SET_NUMBER + "=?";
        String[] selectionArgs = {iSetNumber};
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();

            if(iFavSelector.equalsIgnoreCase("a")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                }else {
                    aContainer.setFavourite(false);
                }
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                aReturnList.add(aContainer);

            }else if (iFavSelector.equalsIgnoreCase("m")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                    aContainer.setFavourite(true);
                    aReturnList.add(aContainer);
                }
            }else if (iFavSelector.equalsIgnoreCase("u")){
                aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
                aContainer.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.MEANING)));
                aContainer.setProgress(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS)));
                if(!(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1)){
                    aContainer.setFavourite(false);
                    aReturnList.add(aContainer);
                }
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

    public Integer getProgress(String iWord){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {DatabaseContract.WordListDB.PROGRESS};
        String selection = DatabaseContract.WordListDB.WORD + "=?";
        String[] selectionArgs = {iWord};

        Cursor c = db.query(
                DatabaseContract.WordListDB.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int aReturnValue = -1;
        if(c.getCount()>0){
            c.moveToFirst();
            aReturnValue = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.PROGRESS));
        }
        c.close();
        db.close();
        return aReturnValue;
    }


    public boolean isFavourite(String iWord){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {DatabaseContract.WordListDB.FAVOURITE};
        String selection = DatabaseContract.WordListDB.WORD + "=?";
        String[] selectionArgs = {iWord};

        Cursor c = db.query(
                DatabaseContract.WordListDB.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean aReturnValue = false;
        if(c.getCount()>0){
            c.moveToFirst();
            if(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.FAVOURITE))==1){
                aReturnValue = true;
            }
        }
        c.close();
        db.close();
        return aReturnValue;
    }


    public void updateProgress(String iWord, Integer iProgress){

        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WordListDB.PROGRESS, iProgress);
        String selection = DatabaseContract.WordListDB.WORD + "=?";
        String[] selectionArgs = { iWord };
        db.update(DatabaseContract.WordListDB.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public void updateFavourite(String iWord, boolean iIsFav){

        int iFav =0;
        if(iIsFav){
            iFav =1;
        }
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WordListDB.FAVOURITE, iFav);
        String selection = DatabaseContract.WordListDB.WORD + "=?";
        String[] selectionArgs = { iWord };
        db.update(DatabaseContract.WordListDB.TABLE_NAME,values,selection,selectionArgs);
        db.close();
    }

    public List<GenericContainer> resetWordList(){
        SQLiteDatabase db = getReadableDatabase();

        String selection = DatabaseContract.WordListDB.PROGRESS + "!=?";
        String[] columns = { DatabaseContract.WordListDB.WORD};
        String[] selectionArgs = {String.valueOf(DB_CONTEXT.getResources().getInteger(R.integer.max_progress_val))};
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();
            aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
            aReturnList.add(aContainer);
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

    public List<GenericContainer> resetWordListByAlphabet(String iAlphabet){
        SQLiteDatabase db = getReadableDatabase();

        String selection = DatabaseContract.WordListDB.WORD + " like ? and " + DatabaseContract.WordListDB.PROGRESS + "!=?";
        String[] columns = { DatabaseContract.WordListDB.WORD};
        String[] selectionArgs = {iAlphabet + "%", String.valueOf(DB_CONTEXT.getResources().getInteger(R.integer.max_progress_val))};
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();
            aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
            aReturnList.add(aContainer);
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

    public List<GenericContainer> resetWordListBySet(String iSetNumber){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = { DatabaseContract.WordListDB.WORD};
        String selection = DatabaseContract.WordListDB.SET_NUMBER + "=? and " + DatabaseContract.WordListDB.PROGRESS + "!=?";
        String[] selectionArgs = {iSetNumber, String.valueOf(DB_CONTEXT.getResources().getInteger(R.integer.max_progress_val))};
        Cursor c = db.query(DatabaseContract.WordListDB.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        List<GenericContainer> aReturnList = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            GenericContainer aContainer = new GenericContainer();
            aContainer.setWord(c.getString(c.getColumnIndexOrThrow(DatabaseContract.WordListDB.WORD)));
            aReturnList.add(aContainer);
            c.moveToNext();
        }
        c.close();
        db.close();
        return aReturnList;
    }

}
