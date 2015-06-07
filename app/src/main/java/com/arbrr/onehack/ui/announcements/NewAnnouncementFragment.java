package com.arbrr.onehack.ui.announcements;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arbrr.onehack.R;

/**
 * Created by Nilay on 6/2/15.
 */
public class NewAnnouncementFragment extends Fragment implements View.OnClickListener{
    //request codes for intents
    private final static int SELECT_PICTURE_REQUEST = 1;
    private final static int CAMERA_REQUEST = 2;

    private Button addPictureButton;
    private Button takePictureButton;
    private Button choosePhotoButton;

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
        getActivity().setTitle(R.string.title_activity_main); //set ActionBar title

        addPictureButton = (Button) view.findViewById(R.id.add_picture_button);
        addPictureButton.setOnClickListener(this);
        takePictureButton = (Button) view.findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(this);
        takePictureButton.setVisibility(View.GONE);
        choosePhotoButton = (Button) view.findViewById(R.id.choose_photo_button);
        choosePhotoButton.setOnClickListener(this);
        choosePhotoButton.setVisibility(View.GONE);

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
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                //save the announcement
                Toast.makeText(getActivity(), "Save the Announcement!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_cancel:
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
            takePictureButton.setVisibility(View.VISIBLE);
            choosePhotoButton.setVisibility(View.VISIBLE);
        }
        else if(v.getId() == R.id.choose_photo_button){
            //let the user choose a picture through whatever program he/she wishes
            Intent selectPicture = new Intent(Intent.ACTION_GET_CONTENT);
            selectPicture.setType("image/*");
            startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), SELECT_PICTURE_REQUEST);
        }
        else if(v.getId() == R.id.take_picture_button){
            //allow user to take a picture
            Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePic, CAMERA_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //do something with the results of choosing a photo and taking a picture
    }
}

