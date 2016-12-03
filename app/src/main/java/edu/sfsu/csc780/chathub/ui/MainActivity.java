/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.csc780.chathub.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.ChatMessage;
import edu.sfsu.csc780.chathub.model.User;

import static android.R.attr.y;
import static android.R.id.input;
import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        MessageUtil.MessageLoadListener, SensorEventListener {

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    public static final int REQUEST_PREFERENCES = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_RECORD_AUDIO = 4;
    public static final int MSG_LENGTH_LIMIT = 64;
    private static final double MAX_LINEAR_DIMENSION = 500.0;
    public static final String ANONYMOUS = "anonymous";
    private static final int REQUEST_PICK_IMAGE = 1;
    private String mUsername;
    private User mUserModel;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private FloatingActionButton mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseRecyclerAdapter<ChatMessage, MessageUtil.MessageViewHolder>
            mFirebaseAdapter;

    private ImageButton mImageButton;
    private ImageButton mPhotoButton;
    private ImageButton mLocationButton;
    private ImageButton mMicrophoneButton;

    private int mSavedTheme;

    private String[] badWords = {"a55",
            "anal",
            "anus",
            "ar5e",
            "arrse",
            "arse",
            "ass",
            "'ass-fucker'",
            "asses",
            "assfucker",
            "assfukka",
            "asshole",
            "assholes",
            "asswhole",
            "a_s_s",
            "'b!tch'",
            "b00bs",
            "b17ch",
            "b1tch",
            "ballbag",
            "balls",
            "ballsack",
            "bastard",
            "beastial",
            "beastiality",
            "bellend",
            "bestial",
            "bestiality",
            "'bi+ch'",
            "biatch",
            "bitch",
            "bitcher",
            "bitchers",
            "bitches",
            "bitchin",
            "bitching",
            "bloody",
            "'blow job'",
            "blowjob",
            "blowjobs",
            "boiolas",
            "bollock",
            "bollok",
            "boner",
            "boob",
            "boobs",
            "booobs",
            "boooobs",
            "booooobs",
            "booooooobs",
            "breasts",
            "buceta",
            "bugger",
            "bum",
            "'bunny fucker'",
            "butt",
            "butthole",
            "buttmuch",
            "buttplug",
            "c0ck",
            "c0cksucker",
            "'carpet muncher'",
            "cawk",
            "chink",
            "cipa",
            "cl1t",
            "clit",
            "clitoris",
            "clits",
            "cnut",
            "cock",
            "'cock-sucker'",
            "cockface",
            "cockhead",
            "cockmunch",
            "cockmuncher",
            "cocks",
            "cocksuck",
            "cocksucked",
            "cocksucker",
            "cocksucking",
            "cocksucks",
            "cocksuka",
            "cocksukka",
            "cok",
            "cokmuncher",
            "coksucka",
            "coon",
            "cox",
            "crap",
            "cum",
            "cummer",
            "cumming",
            "cums",
            "cumshot",
            "cunilingus",
            "cunillingus",
            "cunnilingus",
            "cunt",
            "cuntlick",
            "cuntlicker",
            "cuntlicking",
            "cunts",
            "cyalis",
            "cyberfuc",
            "cyberfuck",
            "cyberfucked",
            "cyberfucker",
            "cyberfuckers",
            "cyberfucking",
            "d1ck",
            "damn",
            "dick",
            "dickhead",
            "dildo",
            "dildos",
            "dink",
            "dinks",
            "dirsa",
            "dlck",
            "'dog-fucker'",
            "doggin",
            "dogging",
            "donkeyribber",
            "doosh",
            "duche",
            "dyke",
            "ejaculate",
            "ejaculated",
            "ejaculates",
            "ejaculating",
            "ejaculatings",
            "ejaculation",
            "ejakulate",
            "'f u c k'",
            "'f u c k e r'",
            "f4nny",
            "fag",
            "fagging",
            "faggitt",
            "faggot",
            "faggs",
            "fagot",
            "fagots",
            "fags",
            "fanny",
            "fannyflaps",
            "fannyfucker",
            "fanyy",
            "fatass",
            "fcuk",
            "fcuker",
            "fcuking",
            "feck",
            "fecker",
            "felching",
            "fellate",
            "fellatio",
            "fingerfuck",
            "fingerfucked",
            "fingerfucker",
            "fingerfuckers",
            "fingerfucking",
            "fingerfucks",
            "fistfuck",
            "fistfucked",
            "fistfucker",
            "fistfuckers",
            "fistfucking",
            "fistfuckings",
            "fistfucks",
            "flange",
            "fook",
            "fooker",
            "fuck",
            "fucka",
            "fucked",
            "fucker",
            "fuckers",
            "fuckhead",
            "fuckheads",
            "fuckin",
            "fucking",
            "fuckings",
            "fuckingshitmotherfucker",
            "fuckme",
            "fucks",
            "fuckwhit",
            "fuckwit",
            "'fudge packer'",
            "fudgepacker",
            "fuk",
            "fuker",
            "fukker",
            "fukkin",
            "fuks",
            "fukwhit",
            "fukwit",
            "fux",
            "fux0r",
            "f_u_c_k",
            "gangbang",
            "gangbanged",
            "gangbangs",
            "gaylord",
            "gaysex",
            "goatse",
            "God",
            "'god-dam'",
            "'god-damned'",
            "goddamn",
            "goddamned",
            "hardcoresex",
            "hell",
            "heshe",
            "hoar",
            "hoare",
            "hoer",
            "homo",
            "hore",
            "horniest",
            "horny",
            "hotsex",
            "'jack-off'",
            "jackoff",
            "jap",
            "'jerk-off'",
            "jism",
            "jiz",
            "jizm",
            "jizz",
            "kawk",
            "knob",
            "knobead",
            "knobed",
            "knobend",
            "knobhead",
            "knobjocky",
            "knobjokey",
            "kock",
            "kondum",
            "kondums",
            "kum",
            "kummer",
            "kumming",
            "kums",
            "kunilingus",
            "'l3i+ch'",
            "l3itch",
            "labia",
            "lust",
            "lusting",
            "m0f0",
            "m0fo",
            "m45terbate",
            "ma5terb8",
            "ma5terbate",
            "masochist",
            "'master-bate'",
            "masterb8",
            "'masterbat*'",
            "masterbat3",
            "masterbate",
            "masterbation",
            "masterbations",
            "masturbate",
            "'mo-fo'",
            "mof0",
            "mofo",
            "mothafuck",
            "mothafucka",
            "mothafuckas",
            "mothafuckaz",
            "mothafucked",
            "mothafucker",
            "mothafuckers",
            "mothafuckin",
            "mothafucking",
            "mothafuckings",
            "mothafucks",
            "'mother fucker'",
            "motherfuck",
            "motherfucked",
            "motherfucker",
            "motherfuckers",
            "motherfuckin",
            "motherfucking",
            "motherfuckings",
            "motherfuckka",
            "motherfucks",
            "muff",
            "mutha",
            "muthafecker",
            "muthafuckker",
            "muther",
            "mutherfucker",
            "n1gga",
            "n1gger",
            "nazi",
            "nigg3r",
            "nigg4h",
            "nigga",
            "niggah",
            "niggas",
            "niggaz",
            "nigger",
            "niggers",
            "nob",
            "'nob jokey'",
            "nobhead",
            "nobjocky",
            "nobjokey",
            "numbnuts",
            "nutsack",
            "orgasim",
            "orgasims",
            "orgasm",
            "orgasms",
            "p0rn",
            "pawn",
            "pecker",
            "penis",
            "penisfucker",
            "phonesex",
            "phuck",
            "phuk",
            "phuked",
            "phuking",
            "phukked",
            "phukking",
            "phuks",
            "phuq",
            "pigfucker",
            "pimpis",
            "piss",
            "pissed",
            "pisser",
            "pissers",
            "pisses",
            "pissflaps",
            "pissin",
            "pissing",
            "pissoff",
            "poop",
            "porn",
            "porno",
            "pornography",
            "pornos",
            "prick",
            "pricks",
            "pron",
            "pube",
            "pusse",
            "pussi",
            "pussies",
            "pussy",
            "pussys",
            "rectum",
            "retard",
            "rimjaw",
            "rimming",
            "'s hit'",
            "'s.o.b.'",
            "sadist",
            "schlong",
            "screwing",
            "scroat",
            "scrote",
            "scrotum",
            "semen",
            "sex",
            "'sh!+'",
            "'sh!t'",
            "sh1t",
            "shag",
            "shagger",
            "shaggin",
            "shagging",
            "shemale",
            "'shi+'",
            "shit",
            "shitdick",
            "shite",
            "shited",
            "shitey",
            "shitfuck",
            "shitfull",
            "shithead",
            "shiting",
            "shitings",
            "shits",
            "shitted",
            "shitter",
            "shitters",
            "shitting",
            "shittings",
            "shitty",
            "skank",
            "slut",
            "sluts",
            "smegma",
            "smut",
            "snatch",
            "'son-of-a-bitch'",
            "spac",
            "spunk",
            "s_h_i_t",
            "t1tt1e5",
            "t1tties",
            "teets",
            "teez",
            "testical",
            "testicle",
            "tit",
            "titfuck",
            "tits",
            "titt",
            "tittie5",
            "tittiefucker",
            "titties",
            "tittyfuck",
            "tittywank",
            "titwank",
            "tosser",
            "turd",
            "tw4t",
            "twat",
            "twathead",
            "twatty",
            "twunt",
            "twunter",
            "v14gra",
            "v1gra",
            "vagina",
            "viagra",
            "vulva",
            "w00se",
            "wang",
            "wank",
            "wanker",
            "wanky",
            "whoar",
            "whore",
            "willies",
            "willy",
            "xrated",
            "xxx"};

    private View.OnClickListener mImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView photoView =  (ImageView) v.findViewById(R.id.messageImageView);
            // Only show the larger view in dialog if there's a image for the message
            if (photoView.getVisibility() == View.VISIBLE) {
                Bitmap bitmap = ((GlideBitmapDrawable) photoView.getDrawable()).getBitmap();
                showPhotoDialog( ImageDialogFragment.newInstance(bitmap));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DesignUtils.applyColorfulTheme(this);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            if (mUser.getPhotoUrl() != null) {
                mPhotoUrl = mUser.getPhotoUrl().toString();
            } else {
                mPhotoUrl = "";
            }

            mUserModel = new
                    User(mUser.getDisplayName(), mUser.getEmail(), mPhotoUrl, "default_nickname", "default_phone");

            System.out.println("BOOMM");
            System.out.println(mUserModel.getEmail());
            System.out.println("SONN");
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = MessageUtil.getFirebaseAdapter(this,
                this,  /* MessageLoadListener */
                mLinearLayoutManager,
                mMessageRecyclerView,
                mImageClickListener);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MSG_LENGTH_LIMIT)});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);


        mSendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Bad word filter using replaceAll function
                String input = mMessageEditText.getText().toString();

                String filteredText = input;

                for(String badword : badWords) {
                    filteredText = filteredText.replaceAll("(?i)" + badword, new String(new char[badword.length()]).replace("\0", "*"));
                }

                System.out.println("BOOMM1233444");
                System.out.println(mUserModel.getProfileImageUrl());
                System.out.println("SON999999N");

                ChatMessage chatMessage = new
                        ChatMessage(filteredText,
                        mUserModel);

                System.out.println("999");
                System.out.println(chatMessage.getUser());
                System.out.println(chatMessage.getText());
                System.out.println("kkk");

                MessageUtil.send(chatMessage);
                mMessageEditText.setText("");
            }
        });


        mImageButton = (ImageButton) findViewById(R.id.shareImageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        mPhotoButton = (ImageButton) findViewById(R.id.cameraButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePhotoIntent();
            }
        });

        mLocationButton = (ImageButton) findViewById(R.id.locationButton);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMap();
            }
        });

        mMicrophoneButton = (ImageButton) findViewById(R.id.microphoneButton);
        mMicrophoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudio();
            }
        });
    }

    private AlertDialog display() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete text field")
                .setTitle("WARNING!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMessageEditText.setText("");
                        dialog.cancel();
                        onSensorListener();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onSensorListener();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    public void onSensorChanged(SensorEvent event) {
        if(!(mMessageEditText.getText().toString().isEmpty())) {
            if(event.values[0] > 30 || event.values[0] < -30) {
                mSensorManager.unregisterListener(this);
                display().show();
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void onSensorListener() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure the implicit intent can be handled
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationUtils.startLocationUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean isGranted = (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        if (isGranted && requestCode == LocationUtils.REQUEST_CODE) {
            LocationUtils.startLocationUpdates(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.preferences_menu:
                mSavedTheme = DesignUtils.getPreferredTheme(this);
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivityForResult(i, REQUEST_PREFERENCES);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadComplete() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void pickImage() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void recordAudio() {
        // ACTION_RECOGNIZE_SPEECH is the intent to recognize speech input
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //LANGUAGE_MODEL_FREE_FORM gets speech input in free form english
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                LANGUAGE_MODEL_FREE_FORM);

        //EXTRA_PROMPT prompts the user with the message to start speaking
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Start speaking now...");

        startActivityForResult(intent, REQUEST_RECORD_AUDIO);
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
                Uri uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());

                // Resize if too big for messaging
                Bitmap bitmap = getBitmapForUri(uri);
                Bitmap resizedBitmap = scaleImage(bitmap);
                if (bitmap != resizedBitmap) {
                    uri = savePhotoImage(resizedBitmap);
                }

                createImageMessage(uri);
            } else {
                Log.e(TAG, "Cannot get image for uploading");
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (data != null && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.d(TAG, "imageBitmap size:" + imageBitmap.getByteCount());
                createImageMessage(savePhotoImage(imageBitmap));
            } else {
                Log.e(TAG, "Cannot get photo URI after taking photo");
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO && resultCode == RESULT_OK) {

            if (data != null && data.getExtras() != null) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mMessageEditText.setText(result.get(0));
                Log.e(TAG, "Processed speech to text.");
            } else {
                Log.e(TAG, "Unable to process speech to text. Please try again.");
            }
        } else if (requestCode == REQUEST_PREFERENCES) {
            if (DesignUtils.getPreferredTheme(this) != mSavedTheme) {
                DesignUtils.applyColorfulTheme(this);
                this.recreate();
            }
        }
    }

    private void createImageMessage(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "Could not create image message with null uri");
            return;
        }

        final StorageReference imageReference = MessageUtil.getImageStorageReference(mUser, uri);
        UploadTask uploadTask = imageReference.putFile(uri);

        // Register observers to listen for when task is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to upload image message");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ChatMessage chatMessage = new
                        ChatMessage(mMessageEditText.getText().toString(),
                        mUserModel, imageReference.toString());
                MessageUtil.send(chatMessage);
                mMessageEditText.setText("");
            }
        });
    }

    private Bitmap getBitmapForUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap scaleImage(Bitmap bitmap) {
        int originalHeight = bitmap.getHeight();
        int originalWidth = bitmap.getWidth();
        double scaleFactor =  MAX_LINEAR_DIMENSION / (double)(originalHeight + originalWidth);

        // We only want to scale down images, not scale upwards
        if (scaleFactor < 1.0) {
            int targetWidth = (int) Math.round(originalWidth * scaleFactor);
            int targetHeight = (int) Math.round(originalHeight * scaleFactor);
            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        } else {
            return bitmap;
        }
    }

    private Uri savePhotoImage(Bitmap imageBitmap) {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile == null) {
            Log.d(TAG, "Error creating media file");
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(photoFile);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return Uri.fromFile(photoFile);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String imageFileNamePrefix = "chathub-" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileNamePrefix,    /* prefix */
                ".jpg",                 /* suffix */
                storageDir              /* directory */
        );
        return imageFile;
    }


    private void loadMap() {
        Loader<Bitmap> loader = getSupportLoaderManager().initLoader(0, null, new LoaderManager
                .LoaderCallbacks<Bitmap>() {
            @Override
            public Loader<Bitmap> onCreateLoader(final int id, final Bundle args) {
                return new MapLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(final Loader<Bitmap> loader, final Bitmap result) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                mLocationButton.setEnabled(true);

                if (result == null) return;
                // Resize if too big for messaging
                Bitmap resizedBitmap = scaleImage(result);
                Uri uri = null;
                if (result != resizedBitmap) {
                    uri = savePhotoImage(resizedBitmap);
                } else {
                    uri = savePhotoImage(result);
                }
                createImageMessage(uri);

            }

            @Override
            public void onLoaderReset(final Loader<Bitmap> loader) {
            }

        });

        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mLocationButton.setEnabled(false);
        loader.forceLoad();
    }

    void showPhotoDialog(DialogFragment dialogFragment) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) { ft.remove(prev); }
        ft.addToBackStack(null);

        dialogFragment.show(ft, "dialog");
    }
}
