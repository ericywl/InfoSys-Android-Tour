package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eric.myapplication.R;

import static eric.myapplication.Database.AttractionContract.AttractionEntry.*;

public class AttractionDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AttractionDB";
    private static final int DATABASE_VER = 1;
    private static final Object[][] attractionArray = {
            {"Buddha Tooth Relic Temple", "Lorem Ipsum",
                    R.mipmap.buddha_tooth_relic_temple},
            {"Kwan Im Thong Hood Cho Temple", "Lorem Ipsum",
                    R.mipmap.kwan_im_thong_hood_cho_temple},
            {"Siong Lim Temple", "Lorem Ipsum",
                    R.mipmap.siong_lim_temple},
            {"Thian Hock Keng Temple", "Lorem Ipsum",
                    R.mipmap.thian_hock_keng_temple},
            {"Kong Meng San Phor Kark See Monastery", "Lorem Ipsum",
                    R.mipmap.kong_meng_san_phor_kark_see_temple},
            {"Burmese Buddhist Temple", "Lorem Ipsum",
                    R.mipmap.burmese_buddhist_temple},
            {"Sakyamuni Buddha Gaya Temple", "Lorem Ipsum",
                    R.mipmap.sakya_muni_buddha_gaya_temple},
            {"Foo Hai Ch'an Monastery", "Lorem Ipsum",
                    R.mipmap.foo_hai_chan_monastery},
            {"Leong San See Temple", "Lorem Ipsum",
                    R.mipmap.leong_san_see_temple},
            {"Wat Ananda Metyarama Thai Buddhist Temple", "Lorem Ipsum",
                    R.mipmap.wat_ananda_metyarama_thai_buddhist_temple}
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
                + COL_IMAGE + " INTEGER)";
    }

    private void initTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        for (Object[] attr : attractionArray) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, (String) attr[0]);
            values.put(COL_INFO, (String) attr[1]);
            values.put(COL_IMAGE, (int) attr[2]);

            sqLiteDatabase.insert(tableName, null, values);
        }
    }
}
