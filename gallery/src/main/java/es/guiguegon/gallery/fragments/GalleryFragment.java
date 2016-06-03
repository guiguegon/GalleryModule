package es.guiguegon.gallery.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import es.guiguegon.gallery.GalleryActivity;
import es.guiguegon.gallery.R;
import es.guiguegon.gallery.adapters.GalleryAdapter;
import es.guiguegon.gallery.helpers.CameraHelper;
import es.guiguegon.gallery.helpers.GalleryHelper;
import es.guiguegon.gallery.helpers.PermissionsManager;
import es.guiguegon.gallery.model.GalleryMedia;
import es.guiguegon.gallery.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements GalleryAdapter.OnGalleryClickListener, GalleryHelper.GalleryHelperListener {

    private static final String KEY_GALLERY_MEDIAS = "key_gallery_medias";
    private final String TAG = "[" + this.getClass().getSimpleName() + "]";
    Toolbar toolbar;
    RecyclerView galleryRecyclerView;
    ProgressBar loadingProgressBar;
    Button btnRetry;
    TextView emptyTextview;

    ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();

    GalleryAdapter galleryAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    CameraHelper cameraHelper;
    GalleryHelper galleryHelper;

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        galleryRecyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
        loadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);
        btnRetry = (Button) view.findViewById(R.id.btn_retry);
        emptyTextview = (TextView) view.findViewById(R.id.empty_textview);
        cameraHelper = new CameraHelper(getContext());
        galleryHelper = new GalleryHelper(getContext(), this);
        setupUi();
        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_GALLERY_MEDIAS, galleryMedias);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            galleryMedias = savedInstanceState.getParcelableArrayList(KEY_GALLERY_MEDIAS);
            afterConfigChange();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GalleryMedia galleryMedia = cameraHelper.onGetPictureIntentResults(requestCode, resultCode, data);
        if (galleryMedia != null) {
            onGalleryMedia(galleryMedia);
        }
    }

    protected void setupUi() {
        setToolbar(toolbar);
        int columns = getMaxColumns();
        galleryAdapter = new GalleryAdapter(getContext(), columns);
        galleryAdapter.setOnGalleryClickListener(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        galleryRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        galleryRecyclerView.setAdapter(galleryAdapter);
        btnRetry.setOnClickListener(this::onButtonRetryClick);
    }

    protected void init() {
        getGalleryMedia();
    }

    protected void afterConfigChange() {
        fillGalleryMedia();
    }

    public int getMaxColumns() {
        int widthRecyclerViewMediaFiles = ScreenUtils.getScreenWidth(getContext());
        int sizeItemsRecyclerView = getResources().getDimensionPixelSize(R.dimen.gallery_item_min_width);
        return widthRecyclerViewMediaFiles / sizeItemsRecyclerView;
    }

    private void fillGalleryMedia() {
        if (!galleryMedias.isEmpty()) {
            galleryAdapter.addGalleryImage(galleryMedias);
            hideEmptyList();
        } else {
            showEmptyList();
        }
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void showEmptyList() {
        galleryRecyclerView.setVisibility(View.GONE);
        emptyTextview.setVisibility(View.VISIBLE);
    }

    private void hideEmptyList() {
        galleryRecyclerView.setVisibility(View.VISIBLE);
        emptyTextview.setVisibility(View.GONE);
    }

    public void showRetry() {
        btnRetry.setVisibility(View.VISIBLE);
    }

    public void hideRetry() {
        btnRetry.setVisibility(View.GONE);
    }

    protected void setToolbar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.gallery_toolbar_title);
    }

    /**
     * OnGalleryClickListener interface
     */
    @Override
    public void onGalleryClick(GalleryMedia galleryMedia) {
        onGalleryMediaSelected(galleryMedia);
    }

    @Override
    public void onCameraClick() {
        try {
            PermissionsManager.requestMultiplePermissions((ViewGroup) getView(), () -> camera(getActivity()),
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            showError(getString(R.string.gallery_exception_necessary_permissions));
        }
    }

    public void getGalleryMedia() {
        try {
            PermissionsManager.requestMultiplePermissions((ViewGroup) getView(), this::getGalleryImages,
                    this::showRetry, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } catch (Exception e) {
            showError(getString(R.string.gallery_exception_necessary_permissions));
            showRetry();
        }
    }

    public void camera(Activity activity) {
        cameraHelper.dispatchGetPictureIntent(activity);
    }

    public void getGalleryImages() {
        galleryHelper.getGalleryAsync();
    }

    public void showError(String message) {
        //Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void onGalleryMedia(GalleryMedia galleryMedia) {
        this.galleryMedias.add(0, galleryMedia);
        galleryAdapter.addGalleryImage(galleryMedia);
        hideEmptyList();
    }

    public void onGalleryMedia(List<GalleryMedia> galleryMedias) {
        this.galleryMedias.addAll(0, galleryMedias);
        galleryAdapter.addGalleryImage(galleryMedias);
        hideEmptyList();
    }

    public void onGalleryMediaSelected(GalleryMedia galleryMedia) {
        Intent intent = new Intent();
        intent.putExtra(GalleryActivity.RESULT_GALLERY_MEDIA, galleryMedia);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().supportFinishAfterTransition();
    }

    void onButtonRetryClick(View view) {
        init();
    }

    @Override
    public void onGalleryReady(List<GalleryMedia> galleryMedias) {
        onGalleryMedia(galleryMedias);
    }
}