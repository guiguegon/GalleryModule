package es.guiguegon.gallerymodule.helpers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by guiguegon on 23/10/2015.
 */
public class GalleryHelper {

    private static GalleryHelper mInstance;
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    private GalleryHelperListener galleryHelperListener;
    private Context context;

    private GalleryHelper() {
    }

    public static GalleryHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GalleryHelper();
        }
        return mInstance;
    }

    public void onCreate(Context context, GalleryHelperListener galleryHelperListener) {
        this.context = context;
        this.galleryHelperListener = galleryHelperListener;
    }

    public void onDestroy() {
        this.context = null;
        this.galleryHelperListener = null;
    }

    public void getGalleryAsync() {
        final Observable<List<GalleryMedia>> observable =
                Observable.create((Observable.OnSubscribe<List<GalleryMedia>>) subscriber -> {
                    subscriber.onNext(getGallery());
                    subscriber.onCompleted();
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(this::onGalleryMedia, this::onGalleryError);
    }

    @UiThread
    private void onGalleryMedia(List<GalleryMedia> galleryMedias) {
        if (galleryHelperListener != null) {
            galleryHelperListener.onGalleryReady(galleryMedias);
        }
    }

    @UiThread
    private void onGalleryError(Throwable throwable) {
        Log.e(TAG, "[onGalleryError]", throwable);
        if (galleryHelperListener != null) {
            galleryHelperListener.onGalleryError();
        }
    }

    @WorkerThread
    private List<GalleryMedia> getGallery() {
        List<GalleryMedia> galleryMedias = new ArrayList<>();
        galleryMedias.addAll(getGalleryImages());
        galleryMedias.addAll(getGalleryVideos());
        Collections.sort(galleryMedias);
        return galleryMedias;
    }

    @WorkerThread
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
                int imageCount = imageCursor.getCount();
                for (int i = 0; i < imageCount; i++) {
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

    @WorkerThread
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

    public interface GalleryHelperListener {
        void onGalleryReady(List<GalleryMedia> galleryMedias);

        void onGalleryError();
    }
}

