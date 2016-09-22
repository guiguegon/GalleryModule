package es.guiguegon.gallerymodule.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import es.guiguegon.gallerymodule.BuildConfig;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import es.guiguegon.gallerymodule.utils.FileUtils;
import java.io.File;

/**
 * Created by guiguegon on 23/10/2015.
 */
public class CameraHelper {

    private static final int REQUEST_CODE_CAMERA = 15;
    private static final String MIME_TYPE_IMAGE = "image/jpeg";
    private static final String MIME_TYPE_VIDEO = "video/mp4";
    private static CameraHelper mInstance;
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    private Uri mediaUri;
    private String mediaPath;
    private String mimeType;
    private Context context;

    private CameraHelper() {
    }

    public static CameraHelper getInstance() {
        if (mInstance == null) {
            mInstance = new CameraHelper();
        }
        return mInstance;
    }

    public void onCreate(Context context) {
        this.context = context;
    }

    public void onDestroy() {
        this.context = null;
    }

    public void dispatchGetPictureIntent(Activity activity) {
        try {
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values = new ContentValues(1);
            mimeType = MIME_TYPE_IMAGE;
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            File file = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE);
            mediaPath = file != null ? file.getAbsolutePath() : null;
            mediaUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            Log.e(TAG, "[dispatchGetPictureIntent]", e);
        }
    }

    public void dispatchGetVideoIntent(Activity activity) {
        try {
            Intent cameraIntent = new Intent();
            cameraIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
            ContentValues values = new ContentValues(1);
            mimeType = MIME_TYPE_VIDEO;
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            File file = FileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_VIDEO);
            mediaPath = file != null ? file.getAbsolutePath() : null;
            mediaUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            Log.e(TAG, "[dispatchGetVideoIntent]", e);
        }
    }

    public GalleryMedia onGetPictureIntentResults(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                galleryAddPic();
                long duration = 0;
                if (MIME_TYPE_VIDEO.equals(mimeType)) {
                    duration = getGalleryMediaDuration();
                }
                return GalleryMedia.create(0, mediaPath, mimeType, duration, 0);
            }
        }
        return null;
    }

    private long getGalleryMediaDuration() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, mediaUri);
        return Long.valueOf(
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

    }

    private void galleryAddPic() {
        if (context != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(mediaPath)));
            context.sendBroadcast(mediaScanIntent);
        }
    }
}

