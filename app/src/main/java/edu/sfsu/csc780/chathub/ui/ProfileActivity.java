package edu.sfsu.csc780.chathub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.User;

import static android.R.id.message;

/**
 * Created by montewithpillow on 12/5/16.
 */

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        User user = (User) intent.getParcelableExtra(MainActivity.EXTRA_USER);
        TextView textView = new TextView(this);
        textView.setTextSize(25);
        textView.setText(user.getName());

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_profile);
        layout.addView(textView);
    }

}
