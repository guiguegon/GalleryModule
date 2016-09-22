package es.guiguegon.gallerymodule;

import android.content.Context;
import android.content.Intent;

/**
 * Created by guiguegon on 22/09/2016.
 */

public class GalleryHelper {

    private boolean multiselection;
    private int maxSelectedItems;

    public GalleryHelper() {
    }

    public GalleryHelper setMultiselection(boolean multiselection) {
        this.multiselection = multiselection;
        return this;
    }

    public GalleryHelper setMaxSelectedItems(int maxSelectedItems) {
        this.maxSelectedItems = maxSelectedItems;
        return this;
    }

    public Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXTRA_MULTISELECTION, multiselection);
        intent.putExtra(GalleryActivity.EXTRA_MAX_SELECTED_ITEMS, maxSelectedItems);
        return intent;
    }
}
