package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Properties;

/**
 * Abstract base class for application activities that adds
 * a collection of utility methods to {@link AppCompatActivity}.
 */
public abstract class AppActivity extends AppCompatActivity {
    /**
     * Request code for Intent to request app permissions.
     */
    protected static final int REQ_CODE_REQUEST_PERMISSIONS = 0x193d & 0xff;
    /**
     * Request code for Intent to take a photo.
     */
    protected static final int REQ_CODE_TAKE_PICTURE = 0x193a;

    /**
     * Request code for Intent to launch photo gallery.
     */
    protected static final int REQ_CODE_PHOTO_GALLERY = 0x193c;

    /* ******************************************
     * Instance fields
     ********************************************/

    protected Properties properties = new Properties(System.getProperties());
    protected PowerManager.WakeLock wakeLock = null;

    /* ******************************************
     * Lifecycle methods
     ********************************************/

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try (InputStream inStream = getAssets().open("app.properties")) {
            Log.i(tag(), "Loading app config from app.properties");
            properties.load(inStream);
        } catch (IOException e) {
            Log.e(tag(), "Error loading app config", e);
        }

        if (Boolean.valueOf(properties.getProperty("app.allowNetworkingOnMainThread"))) {
            Log.w(tag(), "app is configured to allow networking on the main thread, "
                    + "this can potentially lead to UI lock up."
                    + "  Remember to disable this feature in production.");
            allowNetworkingOnMainThread();
        }
    }

    /* ******************************************
     * Context utilities
     ********************************************/

    /**
     * Returns this activity, as a context instance.
     * @return this activity, as context instance
     */
    public Context context() {
        return this;
    }

    /**
     * Returns this activity.
     * @return this activity
     */
    public Activity activity() {
        return this;
    }

    /* ******************************************
     * View utilities
     ********************************************/

    /**
     * Finds a view that was identified by the {@code android:id} XML attribute
     * that was processed in {@link #onCreate}.
     * <p>
     * <strong>Note:</strong> In most cases -- depending on compiler support --
     * the resulting view is automatically cast to the target class type. If
     * the target class type is unconstrained, an explicit cast may be
     * necessary.
     *
     * This method is simply a shortcut for {@link View#findViewById(int)}.
     *
     * @param id the ID to search for
     * @return a view with given ID if found, or {@code null} otherwise
     * @see View#findViewById(int)
     */
    @Nullable
    public <T extends View> T find(@IdRes int id) {
        return super.findViewById(id);
    }

    /* ******************************************
     * String resources utilities
     ********************************************/

    /**
     * Return a localized, styled CharSequence from the application's package's
     * default string table. This methods is simply a shortcut for
     * {@link Context#getString(int)}.
     *
     * @param resId Resource id for the CharSequence text
     */
    @NonNull
    public CharSequence t(@StringRes int resId) {
        return super.getString(resId);
    }

    /**
     * Returns a localized formatted string from the application's package's
     * default string table, substituting the format arguments as defined in
     * {@link java.util.Formatter} and {@link java.lang.String#format}.
     * This method is simply a shortcut for {@link Context#getString(int, Object...)}.
     *
     * @param resId Resource id for the format string
     * @param formatArgs The format arguments that will be used for
     *                   substitution.
     * @return The string data associated with the resource, formatted and
     *         stripped of styled text information.
     */
    @NonNull
    public String t(@StringRes int resId, Object... formatArgs) {
        return super.getString(resId, formatArgs);
    }

    /* ******************************************
     * Resources utilities
     ********************************************/

    /**
     * Returns the short name of the resource with the given ID,
     * such as R.drawable.foobar = "foobar".
     */
    public String getResourceName(@IdRes int id) {
        return getResources().getResourceEntryName(id);
    }

    /**
     * Returns the full name of the resource with the given ID,
     * such as R.drawable.foobar = "drawable/foobar".
     * Full resource names take the form "package:type/entry".
     */
    public String getResourceFullName(@IdRes int id) {
        return getResources().getResourceName(id);
    }

    /**
     * Returns true if the given resource ID maps to a resource in this app.
     * @param id a resource ID such as R.drawable.foo
     */
    public boolean hasResource(@IdRes int id) {
        String resourceName = getResourceName(id);
        return resourceName != null && !resourceName.isEmpty();
    }

    /**
     * Returns a the ID of the resource with the given name and type, such as "foobar", "drawable" = R.drawable.foobar.
     */
    public int getResourceId(@NonNull String name, @NonNull String type) {
        return getResources().getIdentifier(name, type, getPackageName());
    }

    /**
     * Returns true if the given resource name/type maps to a resource in this app.
     * @param name a resource name such as "foo" for R.drawable.foo
     * @param type a resource type such as "drawable" for R.drawable.foo
     */
    public boolean hasResource(@NonNull String name, @NonNull String type) {
        int id = getResourceId(name, type);
        return id >= 0;
    }

    /* ******************************************
     * Properties utilities
     ********************************************/

    /**
     * Searches for the property with the specified key in the application properties.
     * This method returns null if the property is not found.
     * @param name the property name
     * @return the property value, or {@code null} if no such property value exists
     */
    public String prop(String name) {
        return properties.getProperty(name);
    }

    /**
     * Searches for the property with the specified key in the application properties.
     * This method returns {@code defval} if the property is not found.
     * @param name the property name
     * @param defval the property default value
     * @return the property value, or {@code defval} if no such property value exists
     */
    public String prop(String name, String defval) {
        return properties.getProperty(name, defval);
    }

    /**
     * Sets the value of the specified property in the application property list.
     * @param name the property name
     * @param value the property value
     */
    public void setProp(String name, String value) {
        properties.setProperty(name, value);
    }

    /* ******************************************
     * Logging utilities
     ********************************************/

    /**
     * Returns a default tag for the current activity logging.
     * By default this method return a tag consisting of the
     * activity class name, without the qualifying package name.
     */
    public String tag() {
        return getLocalClassName();
    }

    /* ******************************************
     * Toast utilities
     ********************************************/

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastLength {

    }

    /**
     * Pops up a short Toast notification to display the given text.
     */
    public void toast(Object text) {
        toast(String.valueOf(text));
    }

    /**
     * Pops up a short Toast notification to display the given text.
     */
    public void toast(String text) {
        toast(text, /* length */ Toast.LENGTH_SHORT);
    }

    /**
     * Pops up a Toast notification of the given time length to display the given text.
     * The length must be Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    public void toast(Object text, @ToastLength int length) {
        toast(String.valueOf(text), length);
    }

    /**
     * Pops up a Toast notification of the given time length to display the given text.
     * The length must be Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    public void toast(String text, @ToastLength int length) {
        Toast.makeText(this, text, length).show();
        Log.i(getLocalClassName(), text);
    }

    /**
     * Pops up a Toast notification of the given time length to display
     * text from the specified string resource.
     */
    public void toast(@StringRes int resId) {
        toast(t(resId));
    }

    /**
     * Pops up a Toast notification of the given time length to display
     * text from the specified string resource.
     * The length must be Toast.LENGTH_SHORT or Toast.LENGTH_LONG.
     */
    public void toast(@StringRes int resId, @ToastLength int length) {

    }

    /* ******************************************
     * Snackbar utilities
     ********************************************/

    @IntDef({Snackbar.LENGTH_INDEFINITE, Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SnackbarLength {

    }

    /**
     * Pops up a short {@link Snackbar} notification without any action
     * to display a textual representation of the specified object.
     * This method delegates to the object's {@code toString()} method.
     * @param obj the object to display
     */
    public void snack(Object obj) {
        snack(String.valueOf(obj), Snackbar.LENGTH_SHORT);
    }

    /**
     * Pops up a short {@link Snackbar} notification without any action
     * to display the specified text.
     * @param text the text to display
     */
    public void snak(String text) {
        snack(text);
    }

    /**
     * Pops up a short {@link Snackbar} notification without any action
     * to display text from the specified string resource.
     * @param resId the string resource id
     */
    public void snack(@StringRes int resId) {
        snack(resId, Snackbar.LENGTH_SHORT);
    }

    /**
     * Pops up a {@link Snackbar} notification without any action
     * of the specified {@code length} to display a textual
     * representation of the specified object.
     * This method delegates to the object's {@code toString()} method.
     * @param obj the object to display
     * @param length the notification length
     */
    public void snack(Object obj, @SnackbarLength int length) {
        snack(getWindow().getDecorView().getRootView(), obj, length);
    }

    /**
     * Pops up a {@link Snackbar} notification without any action
     * of the specified {@code length} to display text from the
     * specified string resource.
     * @param resId the string resource id
     * @param length the notification length
     */
    public void snack(@StringRes int resId, @SnackbarLength int length) {
        snack(getWindow().getDecorView().getRootView(), resId, length);
    }

    /**
     * Pops up a {@link Snackbar} notification without any action
     * of the specified {@code length} attached to the given
     * {@link View} to display a textual representation of the specified object.
     * This method delegates to the object's {@code toString()} method.
     * @param view the view
     * @param obj the object to display
     * @param length the notification length
     */
    public void snack(View view, Object obj, @SnackbarLength int length) {
        Snackbar.make(view, String.valueOf(obj), length).show();
    }

    /**
     * Pops up a {@link Snackbar} notification without any action
     * of the specified {@code length} attached to the given
     * {@link View} to display text from the specified string resource.
     * @param view the view
     * @param resId the string resource id
     * @param length the notification length
     */
    public void snack(View view, @StringRes int resId, @SnackbarLength int length) {
        Snackbar.make(view, resId, length).show();
    }

    /* ******************************************
     * Power-management utilities
     ********************************************/

    /**
     * Returns true if the wake lock is currently enabled.
     */
    public boolean wakeLockIsEnabled() {
        return wakeLock != null;
    }

    /**
     * Sets wake lock to be enabled (true) or disabled (false).
     * If true, turns on a wake lock so that your device's screen will not lock until
     * further notice while your app is running.
     * If false, turns off wake lock if it was previously enabled;
     * if wake lock is not enabled, there is no effect.
     * Note that your app needs the permission android.permission.WAKE_LOCK
     * to request a wake lock.
     */
    // @RequiresPermission(Manifest.permission.WAKE_LOCK)
    public void setWakeLock(boolean wakeLockEnabled) {
        if (wakeLockEnabled) {
            if (wakeLock == null) {
                PowerManager pwr = (PowerManager) getSystemService(POWER_SERVICE);
                wakeLock = pwr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "appactivity:wakelock");
                wakeLock.acquire();
            }
        } else {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }

    /**
     * Returns the width of the current Android device's screen, in pixels.
     */
    public int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Returns the height of the current Android device's screen, in pixels.
     */
    public int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Returns the current device's screen density in dots-per-inch (DPI), rounded down
     * to the nearest integer.
     */
    public int getScreenDpi() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

    /**
     * Returns true if our application has the given kind of permission,
     * such as Manifest.permission.CAMERA.
     */
    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Pops up a request for our application to acquire the given kind of permission(s),
     * such as Manifest.permission.CAMERA.
     * The activity uses a request code of REQ_CODE_REQUEST_PERMISSIONS.
     */
    public void requestPermission(String... permissions) {
        ActivityCompat.requestPermissions(this,
                permissions, REQ_CODE_REQUEST_PERMISSIONS);
    }

    /**
     * Checks whether the current app has all of the given permissions;
     * if it does not have any, pops up a request for our application to
     * acquire them.
     * The activity uses a request code of REQ_CODE_REQUEST_PERMISSIONS.
     */
    public void ensurePermission(String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                ActivityCompat.requestPermissions(this,
                        permissions, REQ_CODE_REQUEST_PERMISSIONS);
                break;
            }
        }
    }

    /* ******************************************
     * Service utilities
     ********************************************/

    /**
     * Returns whether the specified service is running.
     * @param serviceClass the service class
     * @return whether the specified service is running
     */
    public boolean isServiceRunning(Class<? extends Service> serviceClass) {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* ******************************************
     * Location utilities
     ********************************************/

    /**
     * Returns the device location. Requires at least coarse location permission
     * to be granted.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    })
    public Location getLocation() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        REQ_CODE_REQUEST_PERMISSIONS);
            }
            return null;
        }
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            // fall back to network if GPS is not available
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
    }

    /* ******************************************
     * Debugging utilities
     ********************************************/

    /**
     * Instructs Android to allow your app to perform networking operations on the main thread.
     * Normally trying to do networking on the main thread would cause an exception to be
     * thrown because it can lock up the app's UI. Calling this method disables that restriction.
     */
    public void allowNetworkingOnMainThread() {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

}
