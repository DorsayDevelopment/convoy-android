package com.dorsaydevelopment.convoy;

import android.accounts.AccountManager;
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

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brycen on 15-03-16.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener listener;
    private UiLifecycleHelper uiHelper;

    private EditText usernameText;
    private EditText passwordText;

    private AccountManager accountManager;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);

        accountManager = AccountManager.get(getActivity().getBaseContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);
        usernameText = (EditText) l.findViewById(R.id.login_username_text);
        passwordText = (EditText) l.findViewById(R.id.login_password_text);

        ((Button) l.findViewById(R.id.login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });

        ((Button) l.findViewById(R.id.login_facebook_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLogin();
            }
        });

        return l;
    }

    private void authenticate() {

        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    // Invalid login
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Log in user
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("username", username);
                    installation.saveInBackground();

                    // Go to Main activity
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    private void fbLogin() {
        ParseFacebookUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");

                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().finish();
                } else {
                    // Get first and last name from FB
                    new Request(
                        ParseFacebookUtils.getSession(),
                        "/me",
                        null,
                        HttpMethod.GET,
                        new Request.Callback() {
                            public void onCompleted(Response response) {
                                try {
                                    GraphObject graphObject = response.getGraphObject();
                                    JSONObject jsonObject = graphObject.getInnerJSONObject();
                                    user.put("firstName", jsonObject.getString("first_name"));
                                    user.put("lastName", jsonObject.getString("last_name"));
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.d("MyApp", "User logged in with Facebook as " + ParseUser.getCurrentUser().get("firstName"));
                                        }
                                    });
                                } catch (JSONException je) {
                                    Log.e("FBLogin", je.toString());
                                }
                            }
                        }
                    ).executeAsync();
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
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
