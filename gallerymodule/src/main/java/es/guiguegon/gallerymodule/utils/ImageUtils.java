package es.guiguegon.gallerymodule.utils;
/**
 * Created by guillermoguerrero on 2/6/16.
 */

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ImageUtils {

    private static final String TAG = "[ScreenUtils]";

    private ImageUtils() {
        //empty contructor
    }

    public static void loadImageFromUri(Context context, String imageUri, ImageView imageView) {
        try {
            Glide.with(context.getApplicationContext()).load(imageUri).into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "[loadImageFromUri]", e);
        }
    }
}
