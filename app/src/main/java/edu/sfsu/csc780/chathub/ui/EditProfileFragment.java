package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.User;

/**
 * Created by SangSaephan on 12/5/16.
 */
public class EditProfileFragment extends Activity {
    private TextView currentName;
    private EditText nickname;
    private ImageView currentPicture;
    private Button uploadImage;
    private Button cancelButton;
    private Button saveButton;

    public EditProfileFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Intent intent = getIntent();
        User user = (User) intent.getParcelableExtra(MainActivity.EXTRA_USER);

        currentName = (TextView) findViewById(R.id.current_name);
        currentName.setText(user.getName());

        currentPicture = (ImageView) findViewById(R.id.current_picture);
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .asBitmap()
                .into(currentPicture);

        nickname = (EditText) findViewById(R.id.nickname);

        uploadImage = (Button) findViewById(R.id.upload_image);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),MainActivity.class);
                currentName.setText(nickname.getText().toString());
                startActivity(intent);
            }
        });
    }

}
