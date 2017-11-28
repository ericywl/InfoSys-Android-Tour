package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static eric.myapplication.Database.AttractionData.*;
import static eric.myapplication.Database.AttractionContract.AttractionEntry.*;

public class AttractionDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AttractionDB";
    private static final int DB_VER = 1;

    public AttractionDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SELECTED_TABLE = createTable(SELECTED_TABLE_NAME);
        final String SQL_CREATE_AVAILABLE_TABLE = createTable(AVAILABLE_TABLE_NAME);

        sqLiteDatabase.execSQL(SQL_CREATE_SELECTED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_AVAILABLE_TABLE);

        // Initialize AvailableTable with all attractions
        initTable(sqLiteDatabase, AVAILABLE_TABLE_NAME);
        Log.i("eric1", "AttractionDB created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SELECTED_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AVAILABLE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private String createTable(String tableName) {
        return "CREATE TABLE " + tableName + "("
                + COL_NAME + " TEXT PRIMARY KEY, "
                + COL_ADDR + " TEXT NOT NULL, "
                + COL_IMAGE + " INTEGER, "
                + COL_LARGE_IMAGE + " INTEGER, "
                + COL_INFO + " TEXT NOT NULL)";
    }

    private void initTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        Log.i("eric1", "Initializing " + tableName);
        for (Object[] attrVal : ATTRACTION_ARRAY) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, (String) attrVal[0]);
            values.put(COL_ADDR, (String) attrVal[1]);
            values.put(COL_IMAGE, (int) attrVal[2]);
            values.put(COL_LARGE_IMAGE, (int) attrVal[3]);
            values.put(COL_INFO, (String) attrVal[4]);

            long id = sqLiteDatabase.insert(tableName, null, values);
            Log.i("eric1", id + "");
        }
    }
}
