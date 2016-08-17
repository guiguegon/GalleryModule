package es.guiguegon.gallerymodule.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import es.guiguegon.gallerymodule.GalleryActivity;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::openGallery);
    }

    public void openGallery(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Select Gallery mode");
        dialog.setPositiveButton("Single", (dialogInterface, id) -> {
            openGallery(false);
        });
        dialog.setNegativeButton("Multiple", (dialogInterface, id) -> {
            openGallery(true);
        });
        dialog.show();
    }

    public void openGallery(boolean multiselection) {
        startActivityForResult(GalleryActivity.getCallingIntent(this, multiselection), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            List<GalleryMedia> galleryMedias =
                    data.getParcelableArrayListExtra(GalleryActivity.RESULT_GALLERY_MEDIA_LIST);
            Toast.makeText(this, "Gallery Media selected: " + galleryMedias.size(), Toast.LENGTH_LONG).show();
        }
    }
}
