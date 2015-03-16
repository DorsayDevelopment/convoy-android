package com.dorsaydevelopment.convoy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class AuthenticationActivity extends FragmentActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        fragmentManager = getFragmentManager();
        final Fragment loginFragment = new LoginFragment();
        final Fragment registerFragment = new RegisterFragment();

        fragmentTransaction = fragmentManager.beginTransaction();

//        ((Button) findViewById(R.id.to_login_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                fragmentTransaction.replace(R.id.authentication_container, loginFragment);
//            }
//        });

        ((Button) findViewById(R.id.to_register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.authentication_container, registerFragment);
            }
        });

        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authentication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class LoginFragment extends Fragment {

        private EditText usernameText;
        private EditText passwordText;

        public LoginFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_login, container, false);
            usernameText = (EditText) getActivity().findViewById(R.id.login_username_text);
            passwordText = (EditText) getActivity().findViewById(R.id.login_password_text);

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

                        // TODO: store account in account manager

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
                public void done(ParseUser user, ParseException err) {
                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");

                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        Log.d("MyApp", "User logged in through Facebook!");

                        // TODO: store account in account manager
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                        getActivity().finish();
                    }
                }
            });
        }
    }

    public static class RegisterFragment extends Fragment {

        private EditText usernameText;
        private EditText passwordText;

        public RegisterFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            usernameText = (EditText) getActivity().findViewById(R.id.register_username_text);
            passwordText = (EditText) getActivity().findViewById(R.id.register_password_text);

            ((Button) container.findViewById(R.id.register_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register();
                }
            });

            return inflater.inflate(R.layout.fragment_register, container, false);
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
                        Intent intent = new Intent(getActivity().getApplicationContext(), DispatchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });


        }

    }
}
