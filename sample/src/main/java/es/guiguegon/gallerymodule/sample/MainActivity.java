package es.guiguegon.gallerymodule.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import es.guiguegon.gallerymodule.GalleryActivity;
import es.guiguegon.gallerymodule.model.GalleryMedia;

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
        startActivityForResult(GalleryActivity.getCallingIntent(this), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            //ArrayList<GalleryMedia> galleryMedias = data.getParcelableArrayListExtra(GalleryFragment.RESULT_GALLERY_MEDIA);
            GalleryMedia galleryMedia = data.getParcelableExtra(GalleryActivity.RESULT_GALLERY_MEDIA);
            Toast.makeText(this, "Gallery Media " + galleryMedia.getMediaUri(), Toast.LENGTH_LONG).show();
        }
    }
}
