package ledger.user_interface.utils;

import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by gert on 3/23/17.
 */
public  class PreferenceHandler {
    public final static String LAST_DATABASE_FILE_KEY = "LAST_DATABASE_FILE_KEY";
    private final static String LEDGER_SOFTWARE_NODE_NAME = "LedgerSoftware";

    public static String getStringPreference(String key){
        Preferences preferences = Preferences.userRoot().node(LEDGER_SOFTWARE_NODE_NAME);
        String prefString = preferences.get(key,null);
        return prefString;
    }
    public static void setStringPreference(String key, String value){

        Preferences preferences = Preferences.userRoot().node(LEDGER_SOFTWARE_NODE_NAME);
        preferences.put(key, value);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            System.err.println("Error Saving Last File used");
        }
    }
}
