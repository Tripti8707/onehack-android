package com.arbrr.onehack.ui.welcome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;
import com.arbrr.onehack.ui.announcements.AnnouncementsFragment;

/**
 * Created by Nilay on 7/9/15.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "Login";
    public static final String TITLE = "Log In";

    private EditText emailField;
    private EditText passwordField;

    // progress bar
    private ProgressDialog mProgressDialog;

    public LoginFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Context
        final Context context = this.getActivity().getApplicationContext();

        // Get network manager and shared preferences.
        final NetworkManager mNetworkManager = NetworkManager.getInstance();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Automatically log user in if his/her credentials are saved.
        // NOTE: SPECIFICALLY DONE BEFORE VIEW IS INFLATED
        if (sharedPref.contains("username") && sharedPref.contains("password")) {
            // show loading dialog
            showProgressDialog(sharedPref.getString("username", "Loging in..."), "OneHack is logging you in.");

            mNetworkManager.logUserIn(sharedPref.getString("username", ""), sharedPref.getString("password", ""), new OneHackCallback<User>() {
                @Override
                public void success(User response) {
                    Log.d(TAG, "Logged in!");

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();
                    fragmentTransaction.replace(R.id.main_fragment_container, announcementsFragment);
                    fragmentTransaction.commit();

                    mProgressDialog.dismiss();
                }

                @Override
                public void failure(Throwable error) {
                    Log.d(TAG, "Couldn't log in :(");
                    Toast.makeText(context, R.string.login_invalid_cred, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }

        // inflate view, etc. (only done if user credentials are not saved)
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.log_in);

        emailField = (EditText) view.findViewById(R.id.login_email_field);
        passwordField = (EditText) view.findViewById(R.id.login_password_field);

        // Log in button (onClick)
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show loading dialog
                showProgressDialog(emailField.getText().toString(), "OneHack is logging you in.");

                //close keyboard if open already
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                mNetworkManager.logUserIn(emailField.getText().toString(), passwordField.getText().toString(), new OneHackCallback<User>() {
                    @Override
                    public void success(User response) {
                        Log.d(TAG, "Logged in!");

                        // Remember username/password if successfully logged in.
                        sharedPref.edit()
                                  .putString("username", emailField.getText().toString())
                                  .putString("password", passwordField.getText().toString())
                                  .apply();

                        Log.d(TAG, emailField.getText().toString() + ", " + passwordField.getText().toString());

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();
                        fragmentTransaction.replace(R.id.main_fragment_container, announcementsFragment);
                        fragmentTransaction.commit();

                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void failure(Throwable error) {
                        Log.d(TAG, "Couldn't log in :(");
                        Toast.makeText(context,
                                       R.string.login_invalid_cred,
                                       Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        Button signupButton = (Button) view.findViewById(R.id.login_signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SignupFragment signupFragment = new SignupFragment();
                fragmentTransaction.replace(R.id.main_fragment_container, signupFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void showProgressDialog (String title, String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
}
