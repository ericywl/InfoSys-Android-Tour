package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eric.myapplication.R;

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
        final String SQL_CREATE_SELECTED_TABLE = createTable(AttractionContract.AttractionEntry.SELECTED_TABLE_NAME);
        final String SQL_CREATE_AVAILABLE_TABLE = createTable(AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME);

        sqLiteDatabase.execSQL(SQL_CREATE_SELECTED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_AVAILABLE_TABLE);

        initTable(sqLiteDatabase, AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME);
        initTable(sqLiteDatabase, AttractionContract.AttractionEntry.SELECTED_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AttractionContract.AttractionEntry.SELECTED_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private String createTable(String tableName) {
        return "CREATE TABLE " + tableName + "("
                + AttractionContract.AttractionEntry.COL_NAME + " TEXT PRIMARY KEY, "
                + AttractionContract.AttractionEntry.COL_INFO + " TEXT NOT NULL, "
                + AttractionContract.AttractionEntry.COL_IMAGE + " INTEGER, "
                + AttractionContract.AttractionEntry.COL_REMOVED + " INTEGER)";
    }

    private void initTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        for (Object[] attr : attractionArray) {
            ContentValues values = new ContentValues();
            values.put(AttractionContract.AttractionEntry.COL_NAME, (String) attr[0]);
            values.put(AttractionContract.AttractionEntry.COL_INFO, (String) attr[1]);
            values.put(AttractionContract.AttractionEntry.COL_IMAGE, (int) attr[2]);

            if (tableName.equals(AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME)) values.put(AttractionContract.AttractionEntry.COL_REMOVED, 0);
            else values.put(AttractionContract.AttractionEntry.COL_REMOVED, 1);

            sqLiteDatabase.insert(tableName, null, values);
        }
    }
}
