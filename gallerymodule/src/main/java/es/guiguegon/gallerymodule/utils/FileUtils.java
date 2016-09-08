package es.guiguegon.gallerymodule.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by guillermoguerrero on 2/6/16.
 */
public class FileUtils {

    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public final static String VIDEO_MIME_TYPE = "video";
    private static final String TAG = "[FileUtils]";
    private final static String MEDIA_FOLDER = "";

    private FileUtils() {
        //empty constructor
    }

    public static File getOutputMediaFile(int type) {
        File mediaStorageDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MEDIA_FOLDER);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "Failed to create directory " + MEDIA_FOLDER);
            return null;
        }
        String timeStamp = now();
        File mediaFile;
        switch (type) {
            case MEDIA_TYPE_IMAGE:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
                break;
            case MEDIA_TYPE_VIDEO:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
                break;
            default:
                mediaFile = null;
                break;
        }
        return mediaFile;
    }

    public static File createFileFromPath(String path) throws IOException {
        return new File(path);
    }

    public static void deleteFile(ContentResolver contentResolver, Uri imageUri) {
        try {
            File file = createFileFromPath(imageUri.getPath());
            file.delete();
            String canonicalPath;
            try {
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                Log.e(TAG, "[deleteFile]", e);
                canonicalPath = file.getAbsolutePath();
            }
            final Uri uri = MediaStore.Files.getContentUri("external");
            final int result = contentResolver.delete(uri, MediaStore.Files.FileColumns.DATA + "=?",
                    new String[] { canonicalPath });
            if (result == 0) {
                final String absolutePath = file.getAbsolutePath();
                if (!absolutePath.equals(canonicalPath)) {
                    contentResolver.delete(uri, MediaStore.Files.FileColumns.DATA + "=?",
                            new String[] { absolutePath });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "[deleteFile]", e);
        }
    }

    public static String now() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }
}
