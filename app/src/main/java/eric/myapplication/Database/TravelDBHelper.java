package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;

public class TravelDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TravelDB";
    private static final int DB_VER = 1;
    private static final Object[][] TAXI_TIME_ARRAY = {
            {MBS, 0, 7, 7, 17, 5, 23, 15, 11, 12, 16, 13},
            {BUDDHA_TOOTH, 6, 0, 7, 15, 4, 20, 13, 9, 10, 14, 16},
            {KWAN_IM, 6, 7, 0, 13, 7, 16, 9, 6, 6, 15, 12},
            {SIONG_LIM, 13, 16, 14, 0, 16, 14, 9, 10, 12, 21, 10},
            {THIAN_HOCK, 7, 4, 8, 17, 0, 22, 15, 11, 12, 15, 15},
            {KONG_MENG, 21, 18, 17, 15, 18, 0, 12, 14, 14, 24, 18},
            {BURMESE, 13, 14, 10, 6, 15, 13, 0, 9, 10, 18, 10},
            {SAKYA_MUNI, 9, 10, 6, 11, 11, 14, 8, 0, 2, 15, 11},
            {LEONG_SAN, 9, 11, 6, 11, 11, 14, 8, 2, 0, 18, 11},
            {WAT_ANANDA, 8, 8, 7, 14, 7, 16, 11, 8, 8, 0, 13},
            {FOO_HAI, 13, 16, 11, 12, 14, 18, 12, 10, 13, 24, 0}
    };
    private static final Object[][] TAXI_COST_ARRAY = {};
    private static final Object[][] TRANSPORT_TIME_ARRAY = {};
    private static final Object[][] TRANSPORT_COST_ARRAY = {};
    private static final Object[][] WALK_TIME_ARRAY = {};

    public TravelDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TAXI_TIME = createTable(TAXI_TIME);
        // final String SQL_CREATE_TAXI_COST = createTable(TAXI_COST);
        // final String SQL_CREATE_TRANSPORT_TIME = createTable(TRANSPORT_TIME);
        // final String SQL_CREATE_TRANSPORT_COST = createTable(TRANSPORT_COST);
        // final String SQL_CREATE_WALK_TIME = createTable(WALK_TIME);

        sqLiteDatabase.execSQL(SQL_CREATE_TAXI_TIME);
        // sqLiteDatabase.execSQL(SQL_CREATE_TAXI_COST);
        // sqLiteDatabase.execSQL(SQL_CREATE_TRANSPORT_TIME);
        // sqLiteDatabase.execSQL(SQL_CREATE_TRANSPORT_COST);
        // sqLiteDatabase.execSQL(SQL_CREATE_WALK_TIME);

        initTable(sqLiteDatabase, TAXI_TIME, TAXI_TIME_ARRAY);
        // initTable(sqLiteDatabase, TAXI_COST, TAXI_COST_ARRAY);
        // initTable(sqLiteDatabase, TRANSPORT_TIME, TRANSPORT_TIME_ARRAY);
        // initTable(sqLiteDatabase, TRANSPORT_COST, TRANSPORT_COST_ARRAY);
        // initTable(sqLiteDatabase, WALK_TIME, WALK_TIME_ARRAY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAXI_TIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAXI_COST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSPORT_TIME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSPORT_COST);
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
            values.put(MBS, (double) originArray[1]);
            values.put(BUDDHA_TOOTH, (double) originArray[2]);
            values.put(KWAN_IM, (double) originArray[3]);
            values.put(SIONG_LIM, (double) originArray[4]);
            values.put(THIAN_HOCK, (double) originArray[5]);
            values.put(KONG_MENG, (double) originArray[6]);
            values.put(BURMESE, (double) originArray[7]);
            values.put(SAKYA_MUNI, (double) originArray[8]);
            values.put(LEONG_SAN, (double) originArray[9]);
            values.put(WAT_ANANDA, (double) originArray[10]);
            values.put(FOO_HAI, (double) originArray[11]);

            sqLiteDatabase.insert(tableName, null, values);
        }
    }
}
