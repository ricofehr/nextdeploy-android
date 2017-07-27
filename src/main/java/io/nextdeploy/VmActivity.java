package io.nextdeploy;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

/**
 *  Vm activity
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public class VmActivity extends NextDeployActivity {

    /**
     *  Activity creation trigger
     *  @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm);
        NextDeployApi.listVms(getApplicationContext(), this);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     *  Format vms array for displaying into the android app
     *  @param results  Json array
     *  @param lastLog
     */
    public void listHandler(JSONArray results, String lastLog)
    {
        TableLayout vmTable=(TableLayout)findViewById(R.id.vm_table);
        TableRow row;
        TextView t1, t2, t3;
        JSONObject jrow = null;
        String project = "", user = "", name = "", topic = "", scheme = "http", htlogin = "", htpassword = "";
        int rId, rId2;
        Random r = new Random();

        if (lastLog != null && lastLog.length() != 0) {
            Toast.makeText(getApplicationContext(), lastLog, Toast.LENGTH_LONG).show();
        }

        if (results != null) {
            row = new TableRow(this);

            t1 = new TextView(this);
            t2 = new TextView(this);
            t3 = new TextView(this);

            t1.setText("Topic");
            t2.setText("Project");
            t3.setText("User");

            t1.setTypeface(null, Typeface.BOLD);
            t2.setTypeface(null, Typeface.BOLD);
            t3.setTypeface(null, Typeface.BOLD);

            t1.setTextSize(14);
            t2.setTextSize(14);
            t3.setTextSize(14);

            t1.setPadding(20, 10, 0, 0);
            t2.setPadding(20, 10, 0, 0);
            t3.setPadding(20, 10, 0, 0);

            row.addView(t1);
            row.addView(t2);
            row.addView(t3);

            vmTable.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < results.length(); i++) {
                try {
                    jrow = results.getJSONObject(i);
                    // Exclude jenkins vms
                    if (jrow.getBoolean("is_jenkins")) {
                        continue;
                    }
                    Log.e("log listvm", jrow.toString());
                    project = jrow.getString("project");
                    user = jrow.getString("user");
                    name = jrow.getString("name");
                    topic = jrow.getString("topic");
                    htlogin = jrow.getString("htlogin");
                    htpassword = jrow.getString("htpassword");
                } catch (Exception e) {
                    ;
                }

                row = new TableRow(this);

                t1 = new TextView(this);

                t2 = new TextView(this);
                rId = r.nextInt(200000);
                t2.setId(rId);

                t3 = new TextView(this);
                rId2 = r.nextInt(200000);
                t3.setId(rId2);

                t1.setMovementMethod(LinkMovementMethod.getInstance());
                t1.setClickable(true);
                t1.setText(Html.fromHtml("<a href=\"" + scheme + "://" + htlogin + ":" +
                                         htpassword + "@" + name + "\">" + topic + "</a>"));
                t2.setText(project);
                t3.setText(user);

                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);
                t3.setTypeface(null, 1);

                t1.setTextSize(10);
                t2.setTextSize(10);
                t3.setTextSize(10);

                t1.setPadding(20, 10, 0, 0);
                t2.setPadding(20, 10, 0, 0);
                t3.setPadding(20, 10, 0, 0);

                row.addView(t1);
                row.addView(t2);
                row.addView(t3);

                vmTable.addView(row,
                                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                                             TableLayout.LayoutParams.WRAP_CONTENT));

                NextDeployApi.getProject(getApplicationContext(), project, rId, this);
                NextDeployApi.getUser(getApplicationContext(), user, rId2, this);
            }
        }

        return;
    }
}
