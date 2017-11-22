package eric.myapplication.misc;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eric.myapplication.R;

import static eric.myapplication.misc.AttractionContract.AttractionEntry.*;

public class AttractionDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AttractionDB";
    private static final int DATABASE_VER = 1;
    private static final Object[][] attractionArray = {
            {"Changi City Point", "Lorem Ipsum", R.drawable.octopus},
            {"ION Orchard", "Lorem Ipsum", R.drawable.octopus},
            {"Singapore University of Technology and Design", "Lorem Ipsum", R.drawable.octopus},
            {"Singapore Flyer", "Lorem Ipsum", R.drawable.octopus},
            {"Night Safari", "Lorem Ipsum", R.drawable.octopus},
            {"Resorts World Sentosa", "Lorem Ipsum", R.drawable.octopus},
            {"Singapore Zoo", "Lorem Ipsum", R.drawable.octopus},
            {"Buddha Tooth Relic Temple", "Lorem Ipsum", R.drawable.octopus}
    };

    public AttractionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("eric1", "Created");
        final String SQL_CREATE_SELECTED_TABLE = createTable(SELECTED_TABLE_NAME);
        final String SQL_CREATE_AVAILABLE_TABLE = createTable(AVAILABLE_TABLE_NAME);

        sqLiteDatabase.execSQL(SQL_CREATE_SELECTED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_AVAILABLE_TABLE);

        initTable(sqLiteDatabase, AVAILABLE_TABLE_NAME);
        initTable(sqLiteDatabase, SELECTED_TABLE_NAME);
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
                + COL_INFO + " TEXT NOT NULL, "
                + COL_IMAGE + " INTEGER, "
                + COL_REMOVED + " INTEGER)";
    }

    private void initTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        for (Object[] attr : attractionArray) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, (String) attr[0]);
            values.put(COL_INFO, (String) attr[1]);
            values.put(COL_IMAGE, (int) attr[2]);

            if (tableName.equals(AVAILABLE_TABLE_NAME)) values.put(COL_REMOVED, 0);
            else values.put(COL_REMOVED, 1);

            sqLiteDatabase.insert(tableName, null, values);
        }
    }
}
