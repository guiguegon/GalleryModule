package es.guiguegon.gallery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import es.guiguegon.gallery.R;
import es.guiguegon.gallery.model.GalleryMedia;
import es.guiguegon.gallery.utils.ImageUtils;
import es.guiguegon.gallery.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_HOLDER_TYPE_HEADER = 1;
    private final static int VIEW_HOLDER_TYPE_ITEM = 2;
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    ArrayList<GalleryMedia> galleryMedias;
    OnGalleryClickListener onGalleryClickListener;
    Context context;
    int itemWidth;

    public GalleryAdapter(Context context, int columns) {
        this.context = context;
        galleryMedias = new ArrayList<>();
        itemWidth = ScreenUtils.getScreenWidth(context) / columns;
    }

    public OnGalleryClickListener getOnGalleryClickListener() {
        return onGalleryClickListener;
    }

    public void setOnGalleryClickListener(OnGalleryClickListener onGalleryClickListener) {
        this.onGalleryClickListener = onGalleryClickListener;
    }

    public void addGalleryImage(GalleryMedia galleryMedia) {
        this.galleryMedias.add(0, galleryMedia);
        notifyItemInserted(1);
    }

    public void addGalleryImage(List<GalleryMedia> galleryMedias) {
        this.galleryMedias.addAll(galleryMedias);
        notifyItemRangeInserted(1, galleryMedias.size());
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
                fill((GalleryItemViewHolder) viewHolder, galleryMedias.get(position - 1));
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

    public void fill(GalleryItemViewHolder galleryItemViewHolder, final GalleryMedia galleryMedia) {
        ImageUtils.loadImageFromUri(context, galleryMedia.getMediaUri(), galleryItemViewHolder.galleryItem);
        galleryItemViewHolder.galleryItemLayout.setOnClickListener(
                v -> onGalleryClickListener.onGalleryClick(galleryMedia));
    }

    public void fill(GalleryHeaderViewHolder galleryHeaderViewHolder) {
        galleryHeaderViewHolder.galleryCameraLayout.setOnClickListener(v -> onGalleryClickListener.onCameraClick());
    }

    public interface OnGalleryClickListener {
        void onGalleryClick(GalleryMedia galleryMedia);

        void onCameraClick();
    }

    public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryItem;
        ImageView galleryGradient;
        FrameLayout galleryItemLayout;

        public GalleryItemViewHolder(View v) {
            super(v);
            galleryItem = (ImageView) v.findViewById(R.id.gallery_item);
            galleryGradient = (ImageView) v.findViewById(R.id.gallery_gradient);
            galleryItemLayout = (FrameLayout) v.findViewById(R.id.gallery_item_layout);
            galleryItem.getLayoutParams().width = itemWidth;
            galleryGradient.getLayoutParams().width = itemWidth;
        }
    }

    public class GalleryHeaderViewHolder extends RecyclerView.ViewHolder {

        FrameLayout galleryCameraLayout;

        public GalleryHeaderViewHolder(View v) {
            super(v);
            galleryCameraLayout = (FrameLayout) v.findViewById(R.id.gallery_camera_layout);
        }
    }
}
