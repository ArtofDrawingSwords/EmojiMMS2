package com.example.brandon.emojimms2.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.brandon.emojimms2.Utils.Constants;
import com.example.brandon.emojimms2.fragments.ImageGridFragment;
import com.example.brandon.emojimms2.fragments.ImageSelectedFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 8/24/2017.
 */

// Todo: fix bug when you press back on this activity to bring you to blank main activity
public class SimpleImageActivity extends AppCompatActivity{

    private static final String TAG = "SIMPLE_IMAGE_ACTIVITY";

    // Todo: change the way the urls are retrieved
    public ArrayList<String> mUrlList;
    public String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;
        //Todo: possibly set titles for each fragment later
        int titleRes;

        switch(frIndex)
        {
            default:
            case ImageGridFragment.INDEX:
                mUrlList = (ArrayList<String>) getIntent().getSerializableExtra(Constants.Extra.IMAGE_URL_LIST);
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if(fr == null)
                {
                    fr = new ImageGridFragment();
                }
                break;
            case ImageSelectedFragment.INDEX:
                mUrl = (String) getIntent().getSerializableExtra(Constants.Extra.IMAGE_URL);
                tag = ImageSelectedFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if(fr == null)
                {
                    fr = new ImageSelectedFragment();
                }
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }

    public void debugAnalytics(View v) {
        File file = new File(getCacheDir() + "");
        File[] files = file.listFiles();
        if(files.length > 0){
            for (File mFile : files) {
                Log.d(TAG, "debugAnalytics: " + mFile);
            }
        } else {
            Log.d(TAG, "debugAnalytics: no files in cache");
        }
    }

}
