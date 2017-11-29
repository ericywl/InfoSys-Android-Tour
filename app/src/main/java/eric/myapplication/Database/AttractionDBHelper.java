package eric.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import eric.myapplication.Misc.Attraction;

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
        Log.i("eric1", "AttractionDB updated.");
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

            sqLiteDatabase.insert(tableName, null, values);
        }
    }

    /* SQLITE DATABASE RELATED METHODS*/

    // Add attraction to the database table
    public static void addToTable(SQLiteDatabase sqLiteDatabase, String tableName, Attraction attr) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, attr.getName());
        values.put(COL_ADDR, attr.getAddress());
        values.put(COL_IMAGE, attr.getImage());
        values.put(COL_INFO, attr.getDescription());
        values.put(COL_LARGE_IMAGE, attr.getLargeImage());

        sqLiteDatabase.insert(tableName, null, values);
        Log.i("eric1", attr.getName() + " added to " + tableName);
    }

    // Remove attraction with the respective name from the database table
    public static void removeFromTable(SQLiteDatabase sqLiteDatabase, String tableName, String attrName) {
        String selection = COL_NAME + "=?";
        String[] selectionArgs = {attrName};
        sqLiteDatabase.delete(tableName, selection, selectionArgs);
        Log.i("eric1", attrName + " removed from " + tableName);
    }

    // Get the attraction with the corresponding name
    public static Attraction getAttraction(SQLiteDatabase sqLiteDatabase, String tableName, String attrName) {
        String selection = COL_NAME + "=?";
        String[] selectionArgs = {attrName};
        Cursor cursor = sqLiteDatabase.query(tableName,
                null, selection, selectionArgs, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int addrIndex = cursor.getColumnIndex(COL_ADDR);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);
        int largeImageIndex = cursor.getColumnIndex(COL_LARGE_IMAGE);
        int infoIndex = cursor.getColumnIndex(COL_INFO);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String addr = cursor.getString(addrIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);
            int largeImage = cursor.getInt(largeImageIndex);

            if (name.equals(attrName)) {
                cursor.close();
                Log.i("eric1", "Getting " + name);
                return new Attraction(name, addr, image, largeImage, info);
            }
        }

        // Should not get here
        Log.i("eric1", "Did not get any result with " + attrName + "!");
        return null;
    }

    // Get a list of attractions for the ListView
    public static ArrayList<Attraction> getAttractionList(SQLiteDatabase sqLiteDatabase, String tableName) {
        ArrayList<Attraction> output = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(tableName,
                null, null, null, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int addrIndex = cursor.getColumnIndex(COL_ADDR);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);
        int largeImageIndex = cursor.getColumnIndex(COL_LARGE_IMAGE);
        int infoIndex = cursor.getColumnIndex(COL_INFO);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String addr = cursor.getString(addrIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);
            int largeImage = cursor.getInt(largeImageIndex);

            Attraction attr = new Attraction(name, addr, image, largeImage, info);
            output.add(attr);
        }

        cursor.close();
        Log.i("eric1", "Retrieved attraction list.");
        return output;
    }

    // Get a list of attraction names for the Spinner
    public static ArrayList<String> getAttractionNameList(SQLiteDatabase sqLiteDatabase, String tableName) {
        ArrayList<String> output = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(tableName,
                null, null, null, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            output.add(name);
        }

        cursor.close();
        Log.i("eric1", "Retrieved name list.");
        return output;
    }
}
