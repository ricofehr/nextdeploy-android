package fr.publicis_modem.mvmc;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;

/**
 * Parent of other activities
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public abstract class MvmcActivity extends ActionBarActivity {
    protected void listHandler(JSONArray results, String last_log) {

    }

    protected void valueHandler(int r_id, String value) {
        TextView txt = (TextView) this.findViewById(r_id) ;
        txt.setText(value) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Display the fragment as the main content.
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
