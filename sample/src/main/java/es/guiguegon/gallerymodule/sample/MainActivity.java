package es.guiguegon.gallerymodule.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import es.guiguegon.gallerymodule.GalleryActivity;
import es.guiguegon.gallerymodule.GalleryHelper;
import es.guiguegon.gallerymodule.model.GalleryMedia;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GALLERY = 1;

    private AlertDialog alertDialog;
    private Switch showVideosSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showVideosSwitch = (Switch) findViewById(R.id.switch_show_videos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::showChoiceDialog);
    }

    public void showChoiceDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Select Gallery mode");
        dialog.setPositiveButton("Single", (dialogInterface, id) -> {
            openGallerySingleSelection();
        });
        dialog.setNegativeButton("Multiple", (dialogInterface, id) -> {
            showMaxElementsDialog(view);
        });
        dialog.show();
    }

    public void showMaxElementsDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Select Max elements number");
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_max_number, null);
        Button button = (Button) dialogView.findViewById(R.id.max_number_button_ok);
        button.setOnClickListener((buttonView) -> {
            EditText editText = (EditText) dialogView.findViewById(R.id.max_number_edit_text);
            String maxSelectedItems = editText.getText()
                    .toString();
            openGalleryMultiplSelection(TextUtils.isEmpty(maxSelectedItems) ? Integer.MAX_VALUE
                    : Integer.valueOf(maxSelectedItems));
            alertDialog.dismiss();
        });
        dialog.setView(dialogView);
        alertDialog = dialog.show();
    }

    private void openGallerySingleSelection() {
        startActivityForResult(new GalleryHelper().setShowVideos(showVideosSwitch.isChecked())
                .getCallingIntent(this), REQUEST_CODE_GALLERY);
    }

    private void openGalleryMultiplSelection(int maxSelectedItems) {
        startActivityForResult(new GalleryHelper().setMultiselection(true)
                .setMaxSelectedItems(maxSelectedItems)
                .setShowVideos(showVideosSwitch.isChecked())
                .getCallingIntent(this), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            List<GalleryMedia> galleryMedias =
                    data.getParcelableArrayListExtra(GalleryActivity.RESULT_GALLERY_MEDIA_LIST);
            Toast.makeText(this, "Gallery Media selected: " + galleryMedias.size(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
