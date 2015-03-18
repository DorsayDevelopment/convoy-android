package com.dorsaydevelopment.convoy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.UiLifecycleHelper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by brycen on 15-03-16.
 */
public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private UiLifecycleHelper uiHelper;

    private EditText usernameText;
    private EditText passwordText;

    public RegisterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_register, container, false);
        usernameText = (EditText) l.findViewById(R.id.register_username_text);
        passwordText = (EditText) l.findViewById(R.id.register_password_text);

        ((Button) l.findViewById(R.id.register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        ((Button) l.findViewById(R.id.register_facebook_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbRegister();
            }
        });

        return l;
    }

    public void register() {
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();

        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        if (validationError) {
            Toast.makeText(getActivity().getApplicationContext(), validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // TODO: progress dialog

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // TODO: Dismiss the progress dialog
                if (e != null) {
                    // Show the error message
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void fbRegister() {
        ParseFacebookUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    ApplicationController.getInstance().getFacebookInfo();
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().finish();
                } else {
                    ApplicationController.getInstance().getFacebookInfo();
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}