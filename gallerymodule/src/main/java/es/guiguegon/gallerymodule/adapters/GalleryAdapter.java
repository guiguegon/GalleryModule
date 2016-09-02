package es.guiguegon.gallerymodule.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import es.guiguegon.gallerymodule.R;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import es.guiguegon.gallerymodule.utils.ImageUtils;
import es.guiguegon.gallerymodule.utils.ScreenUtils;
import es.guiguegon.gallerymodule.utils.TextureCameraPreview;
import es.guiguegon.gallerymodule.utils.TimeUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    private final static int VIEW_HOLDER_TYPE_HEADER = 1;
    private final static int VIEW_HOLDER_TYPE_ITEM = 2;
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    ArrayList<GalleryMedia> galleryMedias;
    WeakReference<OnGalleryClickListener> onGalleryClickListenerWeak;
    boolean multiselection;
    int itemWidth;

    public GalleryAdapter(Context context, int columns) {
        galleryMedias = new ArrayList<>();
        itemWidth = ScreenUtils.getScreenWidth(context) / columns;
    }

    public void setMultiselection(boolean multiselection) {
        this.multiselection = multiselection;
    }

    public void setOnGalleryClickListener(OnGalleryClickListener onGalleryClickListener) {
        this.onGalleryClickListenerWeak = new WeakReference<>(onGalleryClickListener);
    }

    public void addGalleryImage(GalleryMedia galleryMedia) {
        this.galleryMedias.add(0, galleryMedia);
        notifySelectableAdapterItemInserted(1);
    }

    public void addGalleryImage(List<GalleryMedia> galleryMedias) {
        this.galleryMedias.addAll(galleryMedias);
        notifyItemRangeInserted(1, galleryMedias.size());
    }

    public ArrayList<GalleryMedia> getSelectedItems() {
        ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();
        for (Integer position : getSelectedItemsPosition()) {
            galleryMedias.add(this.galleryMedias.get(position - 1));
        }
        return galleryMedias;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_HOLDER_TYPE_HEADER:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery_header, viewGroup, false);
                return new GalleryHeaderViewHolder(v);
            case VIEW_HOLDER_TYPE_ITEM:
            default:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gallery, viewGroup, false);
                return new GalleryItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        try {
            final ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            StaggeredGridLayoutManager.LayoutParams sglayoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (viewHolder instanceof GalleryHeaderViewHolder) {
                sglayoutParams.setFullSpan(true);
                fill((GalleryHeaderViewHolder) viewHolder);
            } else {
                sglayoutParams.setFullSpan(false);
                fill((GalleryItemViewHolder) viewHolder, galleryMedias.get(position - 1), position);
            }
            viewHolder.itemView.setLayoutParams(sglayoutParams);
        } catch (Exception e) {
            Log.e(TAG, "[onBindViewHolder] ", e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_HOLDER_TYPE_HEADER;
            default:
                return VIEW_HOLDER_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return galleryMedias.size() + 1;
    }

    public void fill(GalleryItemViewHolder galleryItemViewHolder, final GalleryMedia galleryMedia, int position) {
        galleryItemViewHolder.galleryItemSelected.setSelected(isSelected(position));
        Context context = galleryItemViewHolder.itemView.getContext();
        ImageUtils.loadImageFromUri(context, galleryMedia.getMediaUri(), galleryItemViewHolder.galleryItem);
        if (galleryMedia.isVideo()) {
            galleryItemViewHolder.galleryItemVideoDuration.setText(
                    TimeUtils.getTimeFromVideoDuration(galleryMedia.getDuration()));
            galleryItemViewHolder.galleryItemVideoDuration.setVisibility(View.VISIBLE);
        } else {
            galleryItemViewHolder.galleryItemVideoDuration.setVisibility(View.GONE);
        }
        galleryItemViewHolder.galleryItemLayout.setOnClickListener(v -> {
            if (multiselection) {
                toggleSelection(position);
            }
            onGalleryClickListenerWeak.get().onGalleryClick(galleryMedia);
        });
    }

    public void fill(GalleryHeaderViewHolder galleryHeaderViewHolder) {
        galleryHeaderViewHolder.galleryCameraLayout.setOnClickListener(
                v -> onGalleryClickListenerWeak.get().onCameraClick());
    }

    public interface OnGalleryClickListener {
        void onGalleryClick(GalleryMedia galleryMedia);

        void onCameraClick();
    }

    public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

        View galleryItemSelected;
        ImageView galleryItem;
        ImageView galleryGradient;
        FrameLayout galleryItemLayout;
        TextView galleryItemVideoDuration;

        public GalleryItemViewHolder(View v) {
            super(v);
            galleryItem = (ImageView) v.findViewById(R.id.gallery_item);
            galleryGradient = (ImageView) v.findViewById(R.id.gallery_gradient);
            galleryItemVideoDuration = (TextView) v.findViewById(R.id.gallery_video_duration);
            galleryItemLayout = (FrameLayout) v.findViewById(R.id.gallery_item_layout);
            galleryItemSelected = v.findViewById(R.id.gallery_item_selected);
            galleryItem.getLayoutParams().width = itemWidth;
            galleryGradient.getLayoutParams().width = itemWidth;
        }
    }

    public class GalleryHeaderViewHolder extends RecyclerView.ViewHolder {

        FrameLayout galleryCameraLayout;
        TextureCameraPreview galleryCameraPreview;

        public GalleryHeaderViewHolder(View v) {
            super(v);
            galleryCameraLayout = (FrameLayout) v.findViewById(R.id.gallery_camera_layout);
            galleryCameraPreview = (TextureCameraPreview) v.findViewById(R.id.gallery_camera_preview);
        }
    }
}
