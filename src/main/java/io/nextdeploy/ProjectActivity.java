package io.nextdeploy;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *  Project activity
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public class ProjectActivity extends NextDeployActivity {

    /**
     *  Trigger on activity creation
     *  @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        NextDeployApi.listProjects(getApplicationContext(), this);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     *  Handler who lists all projects for current user
     *  @param results list of projects
     *  @param lastLog output log from the rest request
     */
    public void listHandler(JSONArray results, String lastLog)
    {
        TableLayout project_table=(TableLayout)findViewById(R.id.project_table);
        TableRow row;
        TextView t1, t2, t3, t4;
        JSONObject jrow = null;
        String name = "", login = "", password = "";

        if (lastLog != null && lastLog.length() != 0) {
            Toast.makeText(getApplicationContext(), lastLog, Toast.LENGTH_LONG).show();
        }

        if (results != null) {
            row = new TableRow(this);

            t1 = new TextView(this);
            t3 = new TextView(this);
            t4 = new TextView(this);

            t1.setText("Name");
            t3.setText("Login");
            t4.setText("Pass");

            t1.setTypeface(null, Typeface.BOLD);
            t3.setTypeface(null, Typeface.BOLD);
            t4.setTypeface(null, Typeface.BOLD);

            t1.setTextSize(14);
            t3.setTextSize(14);
            t4.setTextSize(14);

            t1.setPadding(20, 10, 0, 0);
            t3.setPadding(20, 10, 0, 0);
            t4.setPadding(20, 10, 0, 20);

            row.addView(t1);
            row.addView(t3);
            row.addView(t4);

            project_table.addView(row,
                                  new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                                               TableLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < results.length(); i++) {
                try {
                    jrow = results.getJSONObject(i);
                    name = jrow.getString("name");
                    login = jrow.getString("login");
                    password = jrow.getString("password");
                } catch (Exception e) {
                    ;
                }

                row = new TableRow(this);

                t1 = new TextView(this);
                t3 = new TextView(this);
                t4 = new TextView(this);

                t1.setText(name);
                t3.setText(login);
                t4.setText(password);

                t1.setTypeface(null, 1);
                t3.setTypeface(null, 1);
                t4.setTypeface(null, 1);

                t1.setTextSize(12);
                t3.setTextSize(12);
                t4.setTextSize(12);

                t1.setPadding(20, 10, 0, 0);
                t3.setPadding(20, 10, 0, 0);
                t4.setPadding(20, 10, 0, 20);

                row.addView(t1);
                row.addView(t3);
                row.addView(t4);

                project_table.addView(row, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }

        return;
    }
}