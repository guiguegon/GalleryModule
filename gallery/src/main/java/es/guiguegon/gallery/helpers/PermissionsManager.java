package es.guiguegon.gallery.helpers;

import android.view.ViewGroup;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import es.guiguegon.gallery.R;
import java.util.List;

public class PermissionsManager {
    public static void requestMultiplePermissions(ViewGroup rootView,
            final OnAllPermissionsGrantedListener onAllPermissionsGrantedListener, String... permissions) {
        requestMultiplePermissions(rootView, onAllPermissionsGrantedListener, null, permissions);
    }

    public static void requestMultiplePermissions(ViewGroup rootView,
            final OnAllPermissionsGrantedListener onAllPermissionsGrantedListener,
            final OnPermissionsDeniedListener onPermissionsDeniedListener, String... permissions) {
        MultiplePermissionsListener multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    onAllPermissionsGrantedListener.onAllPermissionsGranted();
                } else {
                    if (onPermissionsDeniedListener != null) {
                        onPermissionsDeniedListener.onPermissionsDenied();
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };
        MultiplePermissionsListener deniedMultiplePermissionsListener =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.
                        with(rootView, R.string.gallery_exception_necessary_permissions)
                        .withOpenSettingsButton("Settings")
                        .build();
        Dexter.checkPermissions(new CompositeMultiplePermissionsListener(multiplePermissionsListener,
                deniedMultiplePermissionsListener), permissions);
    }

    public interface OnAllPermissionsGrantedListener {
        void onAllPermissionsGranted();
    }

    public interface OnPermissionsDeniedListener {
        void onPermissionsDenied();
    }
}