package fr.publicis_modem.mvmc;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Settings activity
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
