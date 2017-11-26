package eric.myapplication.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import eric.myapplication.R;

public class LandingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    // Proceed to info activity
    public void infoOnClick(View view) {
        Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
    }

    // Proceed to planner activity
    public void planOnClick(View view) {
        Intent intent = new Intent(view.getContext(), PlanActivity.class);
        startActivity(intent);
    }
}
