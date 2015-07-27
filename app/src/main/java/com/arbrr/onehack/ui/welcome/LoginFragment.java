package com.arbrr.onehack.ui.welcome;

import android.content.Context;
import android.os.Bundle;
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
    private static final String tag = "Login";

    private EditText emailField;
    private EditText passwordField;

    public LoginFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.log_in);

        emailField = (EditText) view.findViewById(R.id.login_email_field);
        passwordField = (EditText) view.findViewById(R.id.login_password_field);

        final Context context = this.getActivity().getApplicationContext();

        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show();

                //close keyboard if open already
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //try logging user in with give credentials
                NetworkManager mNetworkManager = NetworkManager.getInstance();
                mNetworkManager.logUserIn(emailField.getText().toString(), passwordField.getText().toString(), new OneHackCallback<User>() {
                    @Override
                    public void success(User response) {
                        Log.d(tag, "Logged in!");

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();
                        fragmentTransaction.replace(R.id.main_fragment_container, announcementsFragment);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void failure(Throwable error) {
                        Log.d(tag, "Couldn't log in :(");
                        Toast.makeText(context, R.string.login_invalid_cred, Toast.LENGTH_SHORT).show();
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
}
