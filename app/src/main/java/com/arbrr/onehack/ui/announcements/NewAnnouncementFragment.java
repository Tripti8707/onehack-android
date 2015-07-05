package com.arbrr.onehack.ui.announcements;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Announcement;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;

import java.util.Date;

/**
 * Created by Nilay on 6/2/15.
 */
public class NewAnnouncementFragment extends Fragment implements View.OnClickListener{
    private static final String tag = "ONEHACK-AF";

    //request codes for intents
    private final static int SELECT_PICTURE_REQUEST = 1;
    private final static int CAMERA_REQUEST = 2;

    private EditText titleField;
    private EditText bodyField;
    private ImageView imageView;

    private NetworkManager mNetworkManager;

    private AddPictureDialog dialog;

    public NewAnnouncementFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_announcement, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.new_announcement_title);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        titleField = (EditText) view.findViewById(R.id.new_title);
        bodyField = (EditText) view.findViewById(R.id.new_body);

        Button addPictureButton = (Button) view.findViewById(R.id.add_picture_button);
        addPictureButton.setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.new_announcement_image);

        //log user in - just temporary code until full app is ready
        mNetworkManager = NetworkManager.getInstance();
        mNetworkManager.logUserIn("admin@admin.com", "admin", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(tag, "Logged in!");
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Couldn't log in :(");
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // create the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_new_announcement, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                //get the current date
                Date date = new Date();
                //get the title and body
                String titleString = titleField.getText().toString();
                String bodyString = bodyField.getText().toString();

                //create new announcement object
                Announcement a = new Announcement();
                a.setName(titleString);
                a.setInfo(bodyString);
                a.setBroadcastTime(date);

                //save the announcement to the network
                mNetworkManager.createAnnouncement(a, new OneHackCallback<Announcement>() {
                    @Override
                    public void success(Announcement announcement) {
                        Log.d(tag, "Successfully created announcement!");
                    }

                    @Override
                    public void failure(Throwable error) {
                        Log.d(tag, ":(");
                    }
                });

                //close keyboard
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //navigate back to announcments fragment
                getFragmentManager().popBackStack();
                return true;
            case android.R.id.home:
                //close keyboard
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //go back to announcements list
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.add_picture_button){
            dialog = AddPictureDialog.newInstance();
            dialog.setTargetFragment(this, AddPictureDialog.REQUEST_CODE);
            dialog.show(this.getFragmentManager(), "AddPictureDialog");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == AddPictureDialog.REQUEST_CODE) {
            dialog.dismiss();

            int actionRequest = data.getIntExtra(AddPictureDialog.ACTION_KEY, 0);

            if(actionRequest == SELECT_PICTURE_REQUEST){
                //let the user choose a picture through whatever program he/she wishes
                Intent selectPicture = new Intent(Intent.ACTION_GET_CONTENT);
                selectPicture.setType("image/*");
                startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), SELECT_PICTURE_REQUEST);
            }
            else if(actionRequest == CAMERA_REQUEST){
                //let the user take a picture
                Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePic, CAMERA_REQUEST);;
            }
        }
        //do something with the results of choosing a photo or taking a picture
        else if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            imageView.setImageBitmap((Bitmap) extras.get("data"));
        }
        else if(requestCode == SELECT_PICTURE_REQUEST && resultCode ==  Activity.RESULT_OK){
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            }
            catch(Exception e){
                //do something?
                e.printStackTrace();
            }
        }
    }

    //DialogFragment allowing the user to select whether they'd like to upload an exisiting photo
    //or take a new one
    public static class AddPictureDialog extends DialogFragment{
        public final static String ACTION_KEY = "Action";
        private final static int SELECT_PICTURE_REQUEST = 1;
        private final static int CAMERA_REQUEST = 2;
        private final static int REQUEST_CODE = 3; //this dialog's request code

        static AddPictureDialog newInstance() {
            AddPictureDialog dialog = new AddPictureDialog();
            return dialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.dialog_new_announcement, container, false);

            Button takePictureButton = (Button) v.findViewById(R.id.take_picture_button);
            takePictureButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //tell host fragment to start an intent to take a picture
                    Intent intent = new Intent();
                    intent.putExtra(ACTION_KEY, CAMERA_REQUEST);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
                }
            });

            Button choosePicturebutton = (Button) v.findViewById(R.id.choose_picture_button);
            choosePicturebutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //tell host fragment to start an intent to let the user select a picture
                    Intent intent = new Intent();
                    intent.putExtra(ACTION_KEY, SELECT_PICTURE_REQUEST);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
                }
            });

            return v;
        }
    }
}
