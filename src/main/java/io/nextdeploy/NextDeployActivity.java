package io.nextdeploy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;

/**
 *  Parent of other activities
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public abstract class NextDeployActivity extends ActionBarActivity {
    /**
     *  Handler callback from api request for items list
     *  @param results
     *  @param lastLog
     */
    protected void listHandler(JSONArray results, String lastLog) { }

    /**
     *  Handler callback from api request for value setted
     *  @param rId
     *  @param value
     */
    protected void valueHandler(int rId, String value)
    {
        TextView txt = (TextView) this.findViewById(rId);
        txt.setText(value);
    }

    /**
     *  Trigger on menu creation
     *  @param menu
     *  @return Boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *  Trigger when an item is selected into the menu
     *  @param item
     *  @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
