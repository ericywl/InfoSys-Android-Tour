package eric.myapplication.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import eric.myapplication.Database.TravelDBHelper;
import eric.myapplication.R;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;

public class TravelActivity extends AppCompatActivity {
    private TextView dbtext;
    private SQLiteDatabase travelDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        dbtext = findViewById(R.id.dbtext);

        TravelDBHelper travelDBHelper = new TravelDBHelper(this);
        travelDB = travelDBHelper.getReadableDatabase();


    }

    private double getEntry(String tableName, String from, String to) {
        String whereClause = ORIGIN + "=?";
        String[] whereArgs = {from};
        Cursor cursor = travelDB.query(tableName, null, whereClause, whereArgs,
                null, null, null);

        double output = 0;
        int placeIndex = cursor.getColumnIndex(to);

        while (cursor.moveToNext()) {
            output = cursor.getInt(placeIndex);
            cursor.close();
        }

        return output;
    }
}
