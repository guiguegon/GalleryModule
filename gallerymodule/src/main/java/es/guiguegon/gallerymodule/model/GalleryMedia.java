package es.guiguegon.gallerymodule.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import es.guiguegon.gallerymodule.utils.FileUtils;

/**
 * Created by guillermoguerrero on 21/4/16.
 */
@AutoValue
public abstract class GalleryMedia implements Comparable<GalleryMedia>, Parcelable {

    public static GalleryMedia create(long id, String mediaUri, String mimeType, long duration,
            long dateTaken) {
        return new AutoValue_GalleryMedia(id, mediaUri, mimeType, duration, dateTaken);
        }

    public abstract long id();

    public abstract String mediaUri();

    public abstract String mimeType();

    public abstract long duration();

    public abstract long dateTaken();

    public boolean isVideo() {
        return mimeType().contains(FileUtils.VIDEO_MIME_TYPE);
    }

    @Override
    public int compareTo(@NonNull GalleryMedia another) {
        return Long.valueOf(another.dateTaken())
                .compareTo(this.dateTaken());
    }
}