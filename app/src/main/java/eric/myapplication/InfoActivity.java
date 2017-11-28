package eric.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import eric.myapplication.Database.AttractionDBHelper;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        String attrInfo = intent.getStringExtra("INFO");
        int attrLargeImage = intent.getIntExtra("IMAGE", 0);

        ImageView imageView = findViewById(R.id.large_image_placeholder);
        imageView.setImageResource(attrLargeImage);
    }
}
