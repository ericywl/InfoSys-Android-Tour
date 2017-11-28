package eric.myapplication.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncopt.android.widget.text.justify.JustifiedTextView;

import eric.myapplication.Database.AttractionDBHelper;
import eric.myapplication.R;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        String attrName = intent.getStringExtra("NAME");
        String attrInfo = intent.getStringExtra("INFO");
        int attrLargeImage = intent.getIntExtra("IMAGE", 0);

        getSupportActionBar().setTitle(attrName);

        ImageView imageView = findViewById(R.id.large_image_placeholder);
        imageView.setImageResource(attrLargeImage);

        JustifiedTextView infoText = findViewById(R.id.info_text_placeholder);
        infoText.setMovementMethod(new ScrollingMovementMethod());
        infoText.setText(attrInfo);
    }
}
