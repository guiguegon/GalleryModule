package es.guiguegon.gallery.helpers;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import es.guiguegon.gallery.model.GalleryMedia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by guiguegon on 23/10/2015.
 */
public class GalleryHelper {

    public interface GalleryHelperListener {
        void onGalleryReady(List<GalleryMedia> galleryMedias);
    }

    private final String TAG = "[" + this.getClass().getSimpleName() + "]";

    private Context context;
    private Handler handler;
    private GalleryHelperListener galleryHelperListener;

    public GalleryHelper(Context context) {
        this.context = context;
    }

    public GalleryHelper(Context context, GalleryHelperListener galleryHelperListener) {
        this.context = context;
        this.galleryHelperListener = galleryHelperListener;
    }

    public void setGalleryHelperListener(GalleryHelperListener galleryHelperListener) {
        this.galleryHelperListener = galleryHelperListener;
    }

    public void getGalleryAsync() {
        new AsyncTask<Void, Void, Void>() {
            List<GalleryMedia> galleryMedias = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {
                // your async action
                galleryMedias.addAll(getGalleryImages());
                galleryMedias.addAll(getGalleryVideos());
                Collections.sort(galleryMedias);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (galleryHelperListener != null) {
                    galleryHelperListener.onGalleryReady(galleryMedias);
                }
            }
        }.execute();
    }

    private List<GalleryMedia> getGalleryImages() {
        ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();
        try {
            final String[] columns = {
                    MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.MIME_TYPE,
                    MediaStore.Images.Media.DATE_TAKEN
            };
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor imageCursor = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
            if (imageCursor != null) {
                int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int idColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int mimeTypeColumIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
                int dateTakenColumIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                imageCursor.moveToFirst();
                int videoCount = imageCursor.getCount();
                for (int i = 0; i < videoCount; i++) {
                    galleryMedias.add(new GalleryMedia().setMediaUri(imageCursor.getString(dataColumnIndex))
                            .setId(imageCursor.getLong(idColumnIndex))
                            .setMimeType(imageCursor.getString(mimeTypeColumIndex))
                            .setDateTaken(imageCursor.getLong(dateTakenColumIndex)));
                    imageCursor.moveToNext();
                }
                imageCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "[getGalleryImages]", e);
        }
        return galleryMedias;
    }

    private List<GalleryMedia> getGalleryVideos() {
        ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();
        try {
            final String[] columns = {
                    MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.DURATION
            };
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            Cursor videoCursor = context.getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " DESC");
            if (videoCursor != null) {
                int dataColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
                int idColumnIndex = videoCursor.getColumnIndex(MediaStore.Video.Media._ID);
                int mimeTypeColumIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
                int dateTakenColumIndex = videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                videoCursor.moveToFirst();
                int videoCount = videoCursor.getCount();
                for (int i = 0; i < videoCount; i++) {
                    galleryMedias.add(new GalleryMedia().setMediaUri(videoCursor.getString(dataColumnIndex))
                            .setId(videoCursor.getLong(idColumnIndex))
                            .setMimeType(videoCursor.getString(mimeTypeColumIndex))
                            .setDateTaken(videoCursor.getLong(dateTakenColumIndex)));
                    videoCursor.moveToNext();
                }
                videoCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "[getGalleryVideos]", e);
        }
        return galleryMedias;
    }
}

