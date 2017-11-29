package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelData.*;

public class TravelDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TravelDB";
    private static final int DB_VER = 1;

    public TravelDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TAXI_TIME = createTable(TAXI_TIME);
        final String SQL_CREATE_TAXI_COST = createTable(TAXI_COST);
        final String SQL_CREATE_PT_TIME = createTable(PT_TIME);
        final String SQL_CREATE_PT_COST = createTable(PT_COST);
        final String SQL_CREATE_WALK_TIME = createTable(WALK_TIME);

        sqLiteDatabase.execSQL(SQL_CREATE_TAXI_TIME);
        sqLiteDatabase.execSQL(SQL_CREATE_TAXI_COST);
        sqLiteDatabase.execSQL(SQL_CREATE_PT_TIME);
        sqLiteDatabase.execSQL(SQL_CREATE_PT_COST);
        sqLiteDatabase.execSQL(SQL_CREATE_WALK_TIME);

        initTable(sqLiteDatabase, TAXI_TIME, TAXI_TIME_ARRAY);
        initTable(sqLiteDatabase, TAXI_COST, TAXI_COST_ARRAY);
        initTable(sqLiteDatabase, PT_TIME, PT_TIME_ARRAY);
        initTable(sqLiteDatabase, PT_COST, PT_COST_ARRAY);
        initTable(sqLiteDatabase, WALK_TIME, WALK_TIME_ARRAY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAXI_TIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAXI_COST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PT_TIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PT_COST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WALK_TIME);
        onCreate(sqLiteDatabase);
    }

    private String createTable(String tableName) {
        return "CREATE TABLE " + tableName + "("
                + ORIGIN + " TEXT PRIMARY KEY, "
                + MBS + " REAL NOT NULL, "
                + BUDDHA_TOOTH + " REAL NOT NULL, "
                + KWAN_IM + " REAL NOT NULL, "
                + SIONG_LIM + " REAL NOT NULL, "
                + THIAN_HOCK + " REAL NOT NULL, "
                + KONG_MENG + " REAL NOT NULL, "
                + BURMESE + " REAL NOT NULL, "
                + SAKYA_MUNI + " REAL NOT NULL, "
                + LEONG_SAN + " REAL NOT NULL, "
                + WAT_ANANDA + " REAL NOT NULL, "
                + FOO_HAI + " REAL NOT NULL)";
    }

    private void initTable(SQLiteDatabase sqLiteDatabase, String tableName, Object[][] array) {
        for (Object[] originArray : array) {
            ContentValues values = new ContentValues();
            values.put(ORIGIN, (String) originArray[0]);
            values.put(MBS, Double.valueOf(originArray[1].toString()));
            values.put(BUDDHA_TOOTH, Double.valueOf(originArray[2].toString()));
            values.put(KWAN_IM, Double.valueOf(originArray[3].toString()));
            values.put(SIONG_LIM, Double.valueOf(originArray[4].toString()));
            values.put(THIAN_HOCK, Double.valueOf(originArray[5].toString()));
            values.put(KONG_MENG, Double.valueOf(originArray[6].toString()));
            values.put(BURMESE, Double.valueOf(originArray[7].toString()));
            values.put(SAKYA_MUNI, Double.valueOf(originArray[8].toString()));
            values.put(LEONG_SAN, Double.valueOf(originArray[9].toString()));
            values.put(WAT_ANANDA, Double.valueOf(originArray[10].toString()));
            values.put(FOO_HAI, Double.valueOf(originArray[11].toString()));

            sqLiteDatabase.insert(tableName, null, values);
        }
    }

    public static List<String> getAttractionNameList(SQLiteDatabase sqLiteDatabase, String tableName) {
        List<String> output = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(tableName,
                null, null, null, null, null, ORIGIN);

        int nameIndex = cursor.getColumnIndex(ORIGIN);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            output.add(name);
        }

        cursor.close();
        Log.i("eric1", "Retrieved name list.");
        return output;
    }

    public static double getEntry(SQLiteDatabase sqLiteDatabase,String tableName, String from, String to) {
        String whereClause = ORIGIN + "=?";
        String[] whereArgs = {from};
        Cursor cursor = sqLiteDatabase.query(tableName, null, whereClause, whereArgs,
                null, null, null);

        double output = 0;
        int placeIndex = cursor.getColumnIndex(to);

        while (cursor.moveToNext()) {
            output = cursor.getDouble(placeIndex);
            cursor.close();
        }

        return output;
    }
}
