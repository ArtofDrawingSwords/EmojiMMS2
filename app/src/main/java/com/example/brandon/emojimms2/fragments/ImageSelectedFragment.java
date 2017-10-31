package com.example.brandon.emojimms2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brandon.emojimms2.R;
import com.example.brandon.emojimms2.activities.SimpleImageActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Brandon on 8/25/2017.
 */

public class ImageSelectedFragment extends Fragment {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;

    private static final String TAG = "ImageSelectedFragment";

    public static final int INDEX = 1;
    ImageView mImageView;
    ProgressBar mProgressBar;
    TextView mBitmapDimensions;
    ImageButton mMMSButton;
    ImageButton mTwitterButton;
    ImageButton mInstagramButton;
    ImageButton mShareButton;
    Context mContext;
    Boolean hasPackageMMS = false;
    Boolean hasPackageMessaging = false;
    Boolean sharedClicked = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_selected, container, false);

        mContext = getActivity();
        mImageView = (ImageView) rootView.findViewById(R.id.image);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        mBitmapDimensions = (TextView) rootView.findViewById(R.id.tv_bitmap_dimensions);
        mMMSButton = (ImageButton) rootView.findViewById(R.id.btnSendMMS);
        mMMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                sendMMS(bitmap);
            }
        });
        mShareButton = (ImageButton) rootView.findViewById(R.id.btnShare);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedClicked = true;
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                sendMMS(bitmap);
            }
        });

        setupImage();

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    private void sendMMS(Bitmap bitmap){
        hasPackageMMS = packageExists("com.android.mms");
        hasPackageMessaging = packageExists("com.google.android.apps.messaging");
        Log.d(TAG, "sendMMS: hasPackageMMS: " + hasPackageMMS);
        Log.d(TAG, "sendMMS: hasPackageMessaging: " + hasPackageMessaging);

        writeBitmapToCache(bitmap, hasPackageMMS);
        shareCachedImage();
    }

    private void writeBitmapToCache(Bitmap bitmap, boolean isReadable)
    {
        try
        {
            File file;
            if(isReadable) {
                file = new File(mContext.getCacheDir(), "image.png");
            } else {
                File dir = new File(mContext.getCacheDir(), "images");
                dir.mkdir();
                file = new File(dir, "image.png");
            }
            FileOutputStream fOut = new FileOutputStream(file);
            Log.d(TAG, "writeBitmapToCache: 111: " + file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush(); //find out what this means
            fOut.close();
            if(isReadable) {
                file.setReadable(true, false);
                Log.d(TAG, "writeBitmapToCache: set To Readable");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void shareCachedImage()
    {
        if(sharedClicked) {
            File file = new File(mContext.getCacheDir(), "images/image.png");
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.example.brandon.emojimms2", file);
            if (contentUri != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                intent.setDataAndType(contentUri, mContext.getContentResolver().getType(contentUri));
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                PackageManager packageManager = mContext.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                startActivity(intent);
            }
        } else if(hasPackageMessaging) {
            File file = new File(mContext.getCacheDir(), "images/image.png");
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.example.brandon.emojimms2", file);
            if (contentUri != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                intent.setDataAndType(contentUri, mContext.getContentResolver().getType(contentUri));
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                PackageManager packageManager = mContext.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                intent.setPackage("com.google.android.apps.messaging");
                startActivity(intent);
            }
        } else if (hasPackageMMS) {
            File file = new File(mContext.getCacheDir(), "image.png");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            intent.setPackage("com.android.mms");
            startActivity(intent);
        } else {
            File file = new File(mContext.getCacheDir(), "image.png");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);
        }

    }

    private void displayBitmapDimensions(ImageView view)
    {
        Bitmap imageBitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
        mBitmapDimensions.setText("Bitmap Dimensions: " + imageBitmap.getWidth() + " x " + imageBitmap.getHeight());
    }

    //Todo: remove or move method
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //Todo: check if this is the most efficient way to check
    public boolean packageExists(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = mContext.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    private void setupImage()
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        SimpleImageActivity mActivity = (SimpleImageActivity) getActivity();
        ImageLoader.getInstance().displayImage(mActivity.mUrl, mImageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
                String bitmapDimensions  = loadedImage.getWidth() + " x " + loadedImage.getHeight();
                mBitmapDimensions.setText(bitmapDimensions);
            }
        });
    }

    private void sendMMSTEST(Bitmap bitmap)
    {
        try{
            File file = new File(mContext.getCacheDir(), "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
