package com.obdasystems.pocmedici.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    public static final String AUTHORIZATION_TOKEN_PREF = "authorization_token";
    public static final String AUTHORIZATION_ISSUE_PREF = "authorization_issue";
    public static final String AUTHORIZATION_ISSUE_DESCRIPTION_PREF = "authorization_issue_descr";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the authorization token
     * @param context
     * @param authorizationToken
     */
    public static void setAuthorizationToken(Context context, String authorizationToken) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(AUTHORIZATION_TOKEN_PREF, authorizationToken);
        editor.apply();
    }

    /**
     * Get the authorization token
     * @param context
     * @return String: authorization token
     */
    public static String getAuthorizationToken(Context context) {
        return getPreferences(context).getString(AUTHORIZATION_TOKEN_PREF, null);
    }


    /**
     * Set the authorization issue description
     * @param context
     * @param issueDescr
     */
    public static void setAuthorizationIssueDescription(Context context, String issueDescr) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(AUTHORIZATION_ISSUE_DESCRIPTION_PREF, issueDescr);
        editor.apply();
    }

    /**
     * Get the authorization issue description
     * @param context
     * @return String: authorization issue description
     */
    public static String getAuthorizationIssueDescription(Context context) {
        return getPreferences(context).getString(AUTHORIZATION_ISSUE_DESCRIPTION_PREF, null);
    }

    /**
     * Set the Login Status
     * @param context
     * @param foundIssue
     */
    public static void setAuthorizationIssue(Context context, boolean foundIssue) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(AUTHORIZATION_ISSUE_PREF, foundIssue);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: encountered issue while getting auth token
     */
    public static boolean getAuthorizationIssue(Context context) {
        return getPreferences(context).getBoolean(AUTHORIZATION_ISSUE_PREF, false);
    }

}
