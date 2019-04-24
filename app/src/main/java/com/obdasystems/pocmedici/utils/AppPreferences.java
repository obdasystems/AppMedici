package com.obdasystems.pocmedici.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class AppPreferences {
    public static final String AUTHORIZATION_TOKEN = "authorization_token";
    public static final String AUTHORIZATION_ISSUE = "authorization_issue";
    public static final String AUTHORIZATION_ISSUE_DESCRIPTION = "authorization_issue_descr";
    public static final String LAST_TIME_STEP_COUNT_SENT = "last_time_step_counter_sent";
    public static final String LAST_TIME_QUEST_REQ = "last_time_quest_requested";
    private static final String DEFAULT_FILENAME = "itco.preferences";

    /**
     * keep a reference to the context
     */
    private final Context context;
    /**
     * The preferences file name
     */
    private final String filename;

    /**
     * Creates a new {@code AppPreferences} instance, using the given context
     * and a default filename of {@code itco.preferences}.
     *
     * @param context the context
     */
    private AppPreferences(@NonNull Context context) {
        this(context, DEFAULT_FILENAME);
    }

    /**
     * Creates a new {@code AppPreferences} instance, using the given context and file name.
     *
     * @param context  the context
     * @param filename the preferences file name
     */
    private AppPreferences(@NonNull Context context, @NonNull String filename) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.filename = Objects.requireNonNull(filename, "filename must not be null");
    }

    /**
     * Returns a new instance of {@code AppPreferences} for the specified context.
     *
     * @param context the context
     * @return a new instance of {@code AppPreferences}
     */
    public static AppPreferences with(@NonNull Context context) {
        return new AppPreferences(context);
    }

    /**
     * Returns a new instance of {@code AppPreferences} for the specified context and
     * preferences file name.
     *
     * @param context  the context
     * @param filename the preferences file name
     * @return a new instance of {@code AppPreferences}
     */
    public static AppPreferences with(@NonNull Context context, @NonNull String filename) {
        return new AppPreferences(context, filename);
    }

    /**
     * Returns true if this activity contains a preference with the given name.
     * Equivalent to has(name).
     */
    public boolean contains(@NonNull String name) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns false.
     */
    public boolean getBoolean(@NonNull String name) {
        return getBoolean(name, /* defaultValue */ false);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public boolean getBoolean(@NonNull String name, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getBoolean(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.0.
     */
    public double getDouble(@NonNull String name) {
        return getDouble(name, /* defaultValue */ 0.0);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public double getDouble(@NonNull String name, double defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getFloat(name, (float) defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public int getInt(@NonNull String name) {
        return getInt(name, /* defaultValue */ 0);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public int getInt(@NonNull String name, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getInt(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns 0.
     */
    public long getLong(@NonNull String name) {
        return getLong(name, /* defaultValue */ 0L);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public long getLong(@NonNull String name, long defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getLong(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns an empty string.
     */
    public String getString(@NonNull String name) {
        return getString(name, /* defaultValue */ "");
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public String getString(@NonNull String name, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getString(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns an empty set.
     */
    public Set<String> getStringSet(@NonNull String name) {
        return getStringSet(name, /* defaultValue */ new LinkedHashSet<String>());
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    public Set<String> getStringSet(@NonNull String name, Set<String> defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs == null ? defaultValue : prefs.getStringSet(name, defaultValue);
    }

    /**
     * Returns the preference with the given name and value from the app's global preferences.
     * If there is no such shared preference, returns the given default value.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull String name, T defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        if (prefs == null || !prefs.contains(name)) {
            return defaultValue;
        } else {
            return (T) prefs.getAll().get(name);
        }
    }

    /**
     * Returns true if this activity contains a shared preference with the given name
     * in the given shared preference filename.
     * Equivalent to containsShared(filename, name).
     */
    public boolean has(@NonNull String filename, @NonNull String name) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs != null && prefs.contains(name);
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(name, value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, double value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putFloat(name, (float) value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, int value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(name, value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, long value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putLong(name, value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, String value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(name, value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, Iterable<String> value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Set<String> set = new LinkedHashSet<>();
        for (String s : value) {
            set.add(s);
        }
        prefsEditor.putStringSet(name, set);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Sets a shared preference with the given name and value.
     */
    public AppPreferences set(@NonNull String name, Set<String> value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putStringSet(name, value);
        prefsEditor.apply(); // or commit();
        return this;
    }

    /**
     * Removes the shared preference with the given name.
     *
     * @param name the shared preference name
     */
    public AppPreferences remove(@NonNull String name) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(name);
        prefsEditor.apply(); // or commit();
        return this;
    }

}
