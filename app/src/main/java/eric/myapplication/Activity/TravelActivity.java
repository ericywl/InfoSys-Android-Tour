package eric.myapplication.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
        dbtext.setText(String.valueOf(getMBSTime(TAXI_TIME, FOO_HAI)));
    }

    public double getMBSTime(String tableName, String place) {
        String whereClause = ORIGIN + "=?";
        String[] whereArgs = {MBS};
        Cursor cursor = travelDB.query(tableName, null, whereClause, whereArgs,
                null, null, null);

        double output = 0;
        int placeIndex = cursor.getColumnIndex(place);

        while (cursor.moveToNext()) {
            output = cursor.getInt(placeIndex);
            cursor.close();
        }

        return output;
    }
}
