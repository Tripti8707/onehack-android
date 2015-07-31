package com.arbrr.onehack.ui.welcome;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;

/**
 * Created by Nilay on 7/9/15.
 */
public class SignupFragment extends Fragment {

    private static final String TAG = "Signup";
    public static final String TITLE = "Sign Up";

    // progress bar
    private ProgressDialog mProgressDialog;

    public SignupFragment() {
        //required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.signup);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        final NetworkManager networkManager = NetworkManager.getInstance();

        final EditText firstNameField = (EditText) view.findViewById(R.id.signup_firstname_field);
        final EditText lastNameField = (EditText) view.findViewById(R.id.signup_lastname_field);
        final EditText companyNameField = (EditText) view.findViewById(R.id.signup_companyname_field);
        final EditText emailField = (EditText) view.findViewById(R.id.signup_email_field);
        final EditText passwordField = (EditText) view.findViewById(R.id.signup_password_field);

        Button signUpButton = (Button) view.findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show loading dialog
                showProgressDialog(emailField.getText().toString(), "OneHack is signing you up.");

                //close keyboard if open already
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //sign the user up
                networkManager.signUserUp(emailField.getText().toString(), passwordField.getText().toString(), firstNameField.getText().toString(),
                        lastNameField.getText().toString(), companyNameField.getText().toString(), new OneHackCallback<User>() {
                            @Override
                            public void success(User response) {
                                Log.d(TAG, "Successfully created new user");

                                mProgressDialog.dismiss();
                            }

                            @Override
                            public void failure(Throwable error) {
                                Log.d(TAG, "Couldn't create new user");

                                mProgressDialog.dismiss();
                            }
                        });
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.signup_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close keyboard if open
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //go back to last fragment (login screen)
                getActivity().getSupportFragmentManager().popBackStack();
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
