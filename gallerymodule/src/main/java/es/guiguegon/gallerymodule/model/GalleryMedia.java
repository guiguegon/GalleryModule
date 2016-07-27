package es.guiguegon.gallerymodule.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by guillermoguerrero on 21/4/16.
 */
public class GalleryMedia implements Comparable<GalleryMedia>, Parcelable {

    @SuppressWarnings("unused")
    public static final Creator<GalleryMedia> CREATOR = new Creator<GalleryMedia>() {
        @Override
        public GalleryMedia createFromParcel(Parcel in) {
            return new GalleryMedia(in);
        }

        @Override
        public GalleryMedia[] newArray(int size) {
            return new GalleryMedia[size];
        }
    };
    long id;
    String mediaUri;
    String mimeType;
    int duration;
    long dateTaken;

    public GalleryMedia() {
        // empty constructor
    }

    protected GalleryMedia(Parcel in) {
        id = in.readLong();
        mediaUri = in.readString();
        mimeType = in.readString();
        duration = in.readInt();
        dateTaken = in.readLong();
    }

    public long getId() {
        return id;
    }

    public GalleryMedia setId(long id) {
        this.id = id;
        return this;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public GalleryMedia setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public GalleryMedia setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public GalleryMedia setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public GalleryMedia setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
        return this;
    }

    @Override
    public int compareTo(@NonNull GalleryMedia another) {
        return Long.valueOf(another.dateTaken).compareTo(this.dateTaken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mediaUri);
        dest.writeString(mimeType);
        dest.writeInt(duration);
        dest.writeLong(dateTaken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GalleryMedia that = (GalleryMedia) o;
        if (id != that.id) {
            return false;
        }
        if (duration != that.duration) {
            return false;
        }
        if (dateTaken != that.dateTaken) {
            return false;
        }
        if (mediaUri != null ? !mediaUri.equals(that.mediaUri) : that.mediaUri != null) {
            return false;
        }
        return mimeType != null ? mimeType.equals(that.mimeType) : that.mimeType == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (mediaUri != null ? mediaUri.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (int) (dateTaken ^ (dateTaken >>> 32));
        return result;
    }
}