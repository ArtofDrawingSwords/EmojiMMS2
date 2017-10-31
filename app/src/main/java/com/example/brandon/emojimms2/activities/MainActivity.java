package com.example.brandon.emojimms2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.brandon.emojimms2.R;
import com.example.brandon.emojimms2.Utils.Constants;
import com.example.brandon.emojimms2.data.DownloadXmlTask;
import com.example.brandon.emojimms2.data.DownloadXmlTaskParams;
import com.example.brandon.emojimms2.data.EmojiXmlParser;
import com.example.brandon.emojimms2.fragments.ImageGridFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadXmlTaskParams params = new DownloadXmlTaskParams(MainActivity.this, Constants.XML_URL);
        new DownloadXmlTask().execute(params);
    }

    public void startGridViewActivity(List<EmojiXmlParser.Emoji> emojis)
    {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
        intent.putExtra(Constants.Extra.IMAGE_URL_LIST, getUrlListFromEmojis(emojis));
        startActivity(intent);
        finish();
    }

    private ArrayList<String> getUrlListFromEmojis(List<EmojiXmlParser.Emoji> emojis)
    {
        ArrayList<String> list = new ArrayList<String>();
        for (EmojiXmlParser.Emoji emoji : emojis) {
            list.add(emoji.url);
        }
        return list;
    }
}
