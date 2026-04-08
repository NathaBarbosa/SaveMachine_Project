package com.savemachine.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SaveMachineSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME  = "name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token, String name, String email) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getString(KEY_TOKEN, null) != null;
    }

    public String getToken()  { return prefs.getString(KEY_TOKEN, ""); }
    public String getName()   { return prefs.getString(KEY_NAME, ""); }
    public String getEmail()  { return prefs.getString(KEY_EMAIL, ""); }

    public void logout() {
        editor.clear().apply();
    }
}
