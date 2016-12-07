package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.ChatMessage;
import edu.sfsu.csc780.chathub.model.User;

import static android.R.attr.name;
import static android.R.attr.phoneNumber;
import static android.content.ContentValues.TAG;
import static edu.sfsu.csc780.chathub.ui.MainActivity.EXTRA_FIREBASE_USER;

/**
 * Created by SangSaephan on 12/5/16.
 */
public class EditProfileFragment extends Activity {

    private static FirebaseStorage sStorage = FirebaseStorage.getInstance();
    private static final int REQUEST_PICK_IMAGE = 1;

    private TextView currentName;
    private EditText nickname;
    private ImageView currentPicture;
    private Button uploadImage;
    private Button cancelButton;
    private Button saveButton;

    private String updatedPhotoURI = "";

    public EditProfileFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Intent intent = getIntent();
        final User user = (User) intent.getParcelableExtra(MainActivity.EXTRA_USER);

        System.out.println(user.getName());
        System.out.println(user.getEmail());
        System.out.println(user.getNickname());
        System.out.println(user.getProfileImageUrl());

        currentName = (TextView) findViewById(R.id.current_name);
        currentName.setText(user.getNickname());

        currentPicture = (ImageView) findViewById(R.id.current_picture);
        System.out.println(user.getProfileImageUrl().substring(0,4));
        if (user.getProfileImageUrl().substring(0,4).equals("http")) {
            Glide.with(EditProfileFragment.this)
                    .load(user.getProfileImageUrl())
                    .into(currentPicture);
        } else {
            try {
                final StorageReference gsReference =
                        sStorage.getReferenceFromUrl(user.getProfileImageUrl());
                gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(EditProfileFragment.this)
                                .load(uri)
                                .into(currentPicture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        System.out.println("Yo it failed");
                    }
                });
            } catch (IllegalArgumentException e) {
                System.out.println("FAILLLLL!!");
            }
        }

        updatedPhotoURI = user.getProfileImageUrl();

        nickname = (EditText) findViewById(R.id.nickname);

        uploadImage = (Button) findViewById(R.id.upload_image);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened"
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });

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
                User updatedUser = new User(user.getName(), user.getEmail(), updatedPhotoURI, nickname.getText().toString());
                MessageUtil.saveUser(updatedUser);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: request=" + requestCode + ", result=" + resultCode);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            // Process selected image here
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (data != null) {
                final Uri uri = data.getData();

                final StorageReference imageReference = MessageUtil.getProfileImageStorageReference(uri);
                UploadTask uploadTask = imageReference.putFile(uri);
                updatedPhotoURI = imageReference.toString();

                // Register observers to listen for when task is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Failed to upload image message");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        currentPicture = (ImageView) findViewById(R.id.current_picture);
                        Glide.with(EditProfileFragment.this)
                                .load(uri)
                                .asBitmap()
                                .into(currentPicture);
                    }
                });
            } else {
                Log.e(TAG, "Cannot get image for uploading");
            }
        }
    }

}
