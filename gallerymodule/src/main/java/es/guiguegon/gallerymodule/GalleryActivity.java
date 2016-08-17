package es.guiguegon.gallerymodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.karumi.dexter.Dexter;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    public static final String EXTRA_MULTISELECTION = "extra_multiselection";
    public static final String RESULT_GALLERY_MEDIA_LIST = "result_gallery_media_list";
    boolean multiselection;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, GalleryActivity.class);
    }

    public static Intent getCallingIntent(Context context, boolean multiselection) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(EXTRA_MULTISELECTION, multiselection);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.initialize(this);
        if (savedInstanceState == null) {
            multiselection = getIntent().getBooleanExtra(EXTRA_MULTISELECTION, false);
        }
        setContentView(R.layout.activity_gallery);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            replaceFragment(R.id.fragment_content, GalleryFragment.newInstance(multiselection));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }
}
