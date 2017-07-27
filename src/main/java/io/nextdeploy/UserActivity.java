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

import java.util.Random;


/**
 *  User activity
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public class UserActivity extends NextDeployActivity {
    /**
     *  Triggers on activity creation
     *  @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        NextDeployApi.listUsers(getApplicationContext(), this);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     *  Format projects array for displaying into the android app
     *  @param results  Json array
     *  @param lastLog
     */
    public void listHandler(JSONArray results, String lastLog)
    {
        TableLayout user_table=(TableLayout)findViewById(R.id.user_table);
        TableRow row;
        TextView t1, t2, t3, t4;
        JSONObject jrow = null ;
        String email = "", group = "", company = "";
        Random r = new Random();
        int rId = 0 ;

        if (lastLog != null && lastLog.length() != 0) {
            Toast.makeText(getApplicationContext(), lastLog, Toast.LENGTH_LONG).show();
        }

        if (results != null) {
            row = new TableRow(this);

            t1 = new TextView(this);
            t2 = new TextView(this);
            t3 = new TextView(this);

            t1.setText("Email");
            t2.setText("Group");
            t3.setText("Company");

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

            user_table.addView(row,
                               new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                                            TableLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < results.length(); i++) {
                try {
                    jrow = results.getJSONObject(i);
                    email = jrow.getString("email") ;
                    group = jrow.getString("group") ;
                    company = jrow.getString("company") ;
                } catch (Exception e) {
                    ;
                }

                row = new TableRow(this);

                t1 = new TextView(this);
                t2 = new TextView(this);

                rId = r.nextInt(200000) ;
                t2.setId(rId) ;
                t3 = new TextView(this);

                t1.setText(email);
                t2.setText(group);
                t3.setText(company);

                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);
                t3.setTypeface(null, 1);

                t1.setTextSize(12);
                t2.setTextSize(12);
                t3.setTextSize(12);

                t1.setPadding(20, 10, 0, 0);
                t2.setPadding(20, 10, 0, 0);
                t3.setPadding(20, 10, 0, 0);

                row.addView(t1);
                row.addView(t2);
                row.addView(t3);

                user_table.addView(row,
                                   new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                                                                TableLayout.LayoutParams.WRAP_CONTENT));

                NextDeployApi.getGroup(getApplicationContext(), group, rId, this) ;
            }
        }
        return ;
    }
}
