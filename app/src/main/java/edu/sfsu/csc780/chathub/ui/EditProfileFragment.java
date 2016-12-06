package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.sfsu.csc780.chathub.R;

/**
 * Created by SangSaephan on 12/5/16.
 */
public class EditProfileFragment extends Activity {

    public EditProfileFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile);
    }

}
