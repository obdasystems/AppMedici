package com.obdasystems.pocmedici.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    public static final String AUTHORIZATION_TOKEN_PREF = "authorization_token";
    public static final String AUTHORIZATION_ISSUE_PREF = "authorization_issue";
    public static final String AUTHORIZATION_ISSUE_DESCRIPTION_PREF = "authorization_issue_descr";
    public static final String LAST_TIME_STEP_COUNTER_SENT_PREF = "last_time_step_counter_sent";
    public static final String LAST_TIME_QUEST_REQ_PREF = "last_time_quest_requested";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the authorization token
     * @param context
     * @param dateString
     */
    public static void setLastTimeQuestionnairesRequested(Context context, String dateString) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LAST_TIME_QUEST_REQ_PREF, dateString);
        editor.apply();
    }

    /**
     * Get the authorization token
     * @param context
     * @return String: string representing last date questionnaires have been downloaded from server
     */
    public static String getLastTimeQuestionnairesRequested(Context context) {
        return getPreferences(context).getString(LAST_TIME_QUEST_REQ_PREF, null);
    }

    /**
     * Set the authorization token
     * @param context
     * @param dateString
     */
    public static void setLastTimeStepcountersSent(Context context, String dateString) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LAST_TIME_STEP_COUNTER_SENT_PREF, dateString);
        editor.apply();
    }

    /**
     * Get the authorization token
     * @param context
     * @return String: string representing last date step counters have been sent to server
     */
    public static String getLastTimeStepcountersSent(Context context) {
        return getPreferences(context).getString(LAST_TIME_STEP_COUNTER_SENT_PREF, null);
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
