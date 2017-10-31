package com.example.brandon.emojimms2.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.brandon.emojimms2.activities.MainActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Brandon on 8/24/2017.
 */

public class DownloadXmlTask extends AsyncTask<DownloadXmlTaskParams, Void, List<EmojiXmlParser.Emoji>> {

    public static final String TAG = "DownloadXmlTask";

    Context mContext;

    @Override
    protected List<EmojiXmlParser.Emoji> doInBackground(DownloadXmlTaskParams... params) {
        try {
            //Log.d(TAG, "doInBackground: download xml task mcontext " + params[0].mContext + " urls " + params[0].mUrl);
            mContext = params[0].mContext;
            return loadXmlFromNetwork(params[0].mUrl);
        } catch (IOException e) {
            Log.d("MainActivity", "Connection Error");
            //return getResources().getString(R.string.connection_error);
            return null;
        } catch (XmlPullParserException e) {
            Log.d("MainActivity", "XML Error");
            //return getResources().getString(R.string.xml_error);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<EmojiXmlParser.Emoji> result) {
        if (result != null) {
            ((MainActivity) mContext).startGridViewActivity(result);
        }
    }

    private List<EmojiXmlParser.Emoji> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        EmojiXmlParser emojiXmlParser = new EmojiXmlParser();
        List<EmojiXmlParser.Emoji> emojis = null;

        try {
            stream = downloadUrl(urlString);
            emojis = emojiXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return emojis;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}

