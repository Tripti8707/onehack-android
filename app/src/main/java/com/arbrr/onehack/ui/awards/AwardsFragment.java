package com.arbrr.onehack.ui.awards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Award;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import java.util.List;

/**
 * Created by Omkar Moghe on 6/14/2015.
 */
public class AwardsFragment extends Fragment {

    public static final String TITLE = "Awards";
    private static final String tag = "ONEHACK-WF";

    private NetworkManager mNetworkManager;

    public AwardsFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awards, container, false);

        mNetworkManager = NetworkManager.getInstance();
        mNetworkManager.logUserIn("tom_erdmann@mac.com", "test", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(tag, "Logged in!");
                getAwards();
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Couldn't log in :(");
            }
        });


        return view;
    }

    private void getAwards() {
//        Award award = new Award();
//        award.company = "Apple";
//        award.info = "Eat an apple the fastest";
//        award.hackathon_id = 1;
//        award.name = "Fastest apple eater";
//        award.prize = "One million";
//        mNetworkManager.createAward(award, new OneHackCallback<Award>() {
//            @Override
//            public void success(Award response) {
//
//            }
//
//            @Override
//            public void failure(Throwable error) {
//
//            }
//        });

        mNetworkManager.getAwards(new OneHackCallback<List<Award>>() {
            @Override
            public void success(List<Award> awards) {
                Log.d(tag, "Got awards, first one's title is " + awards.get(0).getName());
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Something went wrong getting the awards");
            }
        });
    }
}
