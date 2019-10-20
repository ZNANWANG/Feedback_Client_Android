package com.example.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import main.AllFunctions;
import pl.droidsonroids.gif.GifImageView;

public class Activity_Homepage extends AppCompatActivity {

    private GifImageView gifImageView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_homepage);
        mToolbar.setTitle("Rapid Feedback -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Homepage.this, Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Homepage.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Homepage.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        gifImageView = findViewById(R.id.geogif);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public void toPart1(View view) {
        Intent intent = new Intent(this, Activity_Assessment_Preparation.class);
        startActivity(intent);
    }

    public void toPart2(View view) {
        Intent intent = new Intent(this, Activity_Realtime_Assessment.class);
        startActivity(intent);
    }

    public void toPart3(View view) {
        Intent intent = new Intent(this, Activity_Review_Report.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("hasBackPressed", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
