package com.example.brandon.emojimms2.data;

import android.content.Context;

/**
 * Created by Brandon on 8/24/2017.
 */

public class DownloadXmlTaskParams {

    Context mContext;
    String mUrl;

    public DownloadXmlTaskParams(Context context, String url)
    {
        mContext = context;
        mUrl = url;
    }
}
