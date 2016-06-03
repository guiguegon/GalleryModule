package es.guiguegon.gallery.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by guillermoguerrero on 2/6/16.
 */
public class ScreenUtils {

    private static final String TAG = "[ScreenUtils]";

    public static int getScreenWidth(Context context) {
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        } catch (Exception e) {
            Log.e(TAG, "[getScreenWidth]", e);
        }
        return 0;
    }

    public static int getScreenHeight(Context context) {
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } catch (Exception e) {
            Log.e(TAG, "[getScreenHeight]", e);
        }
        return 0;
    }
}
