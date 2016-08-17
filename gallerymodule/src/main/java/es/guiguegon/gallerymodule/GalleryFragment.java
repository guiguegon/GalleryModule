package es.guiguegon.gallerymodule;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import es.guiguegon.gallerymodule.adapters.GalleryAdapter;
import es.guiguegon.gallerymodule.helpers.CameraHelper;
import es.guiguegon.gallerymodule.helpers.GalleryHelper;
import es.guiguegon.gallerymodule.helpers.PermissionsManager;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import es.guiguegon.gallerymodule.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment
        implements GalleryAdapter.OnGalleryClickListener, GalleryHelper.GalleryHelperListener {

    private static final String ARGUMENT_MULTISELECTION = "argument_multiselection";
    private static final String KEY_GALLERY_MEDIA = "key_gallery_media";
    private static final String KEY_SELECTED_POSITION = "key_selected_position";

    Toolbar toolbar;
    RecyclerView galleryRecyclerView;
    ProgressBar loadingProgressBar;
    Button btnRetry;
    TextView emptyTextview;

    ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();
    ArrayList<Integer> selectedPositions = new ArrayList<>();

    GalleryAdapter galleryAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    CameraHelper cameraHelper;
    GalleryHelper galleryHelper;

    MenuItem checkItem;

    boolean multiselection;

    static GalleryFragment newInstance(boolean multiselection) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(ARGUMENT_MULTISELECTION, multiselection);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            multiselection = getArguments().getBoolean(ARGUMENT_MULTISELECTION, false);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryHelper = GalleryHelper.getInstance();
        cameraHelper = CameraHelper.getInstance();
        galleryHelper.onCreate(getContext(), this);
        cameraHelper.onCreate(getContext());
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        galleryHelper.onDestroy();
        cameraHelper.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        galleryRecyclerView = (RecyclerView) view.findViewById(R.id.gallery_recycler_view);
        loadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);
        btnRetry = (Button) view.findViewById(R.id.btn_retry);
        emptyTextview = (TextView) view.findViewById(R.id.empty_textview);
        setupUi();
        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_GALLERY_MEDIA, galleryMedias);
        outState.putIntegerArrayList(KEY_SELECTED_POSITION, galleryAdapter.getSelectedItemsPosition());
        outState.putBoolean(ARGUMENT_MULTISELECTION, multiselection);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            galleryMedias = savedInstanceState.getParcelableArrayList(KEY_GALLERY_MEDIA);
            selectedPositions = savedInstanceState.getIntegerArrayList(KEY_SELECTED_POSITION);
            multiselection = savedInstanceState.getBoolean(ARGUMENT_MULTISELECTION);
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
        galleryAdapter.setMultiselection(multiselection);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        galleryRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        galleryRecyclerView.setAdapter(galleryAdapter);
        galleryAdapter.setOnGalleryClickListener(this);
        btnRetry.setOnClickListener(this::onButtonRetryClick);
    }

    protected void init() {
        getGalleryMedia();
    }

    protected void afterConfigChange() {
        galleryAdapter.setMultiselection(multiselection);
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
            galleryAdapter.setSelectedPositions(selectedPositions);
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
        btnRetry.setVisibility(View.GONE);
    }

    public void showRetry() {
        btnRetry.setVisibility(View.VISIBLE);
    }

    protected void setToolbar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onGalleryClick(GalleryMedia galleryMedia) {
        if (multiselection) {
            handleToolbarState();
        } else {
            onGalleryMediaSelected(galleryMedia);
        }
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
        showLoading();
    }

    public void showError(String message) {
        hideLoading();
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

    public void onGalleryMediaSelected(ArrayList<GalleryMedia> galleryMedias) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(GalleryActivity.RESULT_GALLERY_MEDIA_LIST, galleryMedias);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().supportFinishAfterTransition();
    }

    public void onGalleryMediaSelected(GalleryMedia galleryMedia) {
        ArrayList<GalleryMedia> galleryMedias = new ArrayList<>();
        galleryMedias.add(galleryMedia);
        onGalleryMediaSelected(galleryMedias);
    }

    void onButtonRetryClick(View view) {
        init();
    }

    @Override
    public void onGalleryReady(List<GalleryMedia> galleryMedias) {
        hideLoading();
        onGalleryMedia(galleryMedias);
    }

    @Override
    public void onGalleryError() {
        hideLoading();
        showError(getString(R.string.gallery_something_went_wrong));
        showRetry();
    }

    /** Menu */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
        checkItem = menu.findItem(R.id.action_check);
        handleToolbarState();
    }

    private void handleToolbarState() {
        int selectedItemCount = galleryAdapter.getSelectedItemCount();
        if (selectedItemCount > 0) {
            checkItem.setVisible(true);
            toolbar.setTitle(String.format(getString(R.string.gallery_toolbar_title_selected),
                    String.valueOf(selectedItemCount)));
        } else {
            checkItem.setVisible(false);
            toolbar.setTitle(R.string.gallery_toolbar_title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {
            onGalleryMediaSelected(galleryAdapter.getSelectedItems());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}