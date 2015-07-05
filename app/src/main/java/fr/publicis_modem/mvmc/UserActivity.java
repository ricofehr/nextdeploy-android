package fr.publicis_modem.mvmc;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;


/**
 * User activity
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public class UserActivity extends MvmcActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        MvmcApi.listUsers(getApplicationContext(), this) ;
    }

    public void listHandler(JSONArray results, String last_log) {
        TableLayout user_table=(TableLayout)findViewById(R.id.user_table);
        TableRow row;
        TextView t1, t2, t3, t4;
        JSONObject jrow = null ;
        String email="", group="", company="", quotavm="" ;
        Random r = new Random();
        int r_id = 0 ;

        if (last_log != null && last_log.length() != 0) {
            Toast.makeText(getApplicationContext(), last_log, Toast.LENGTH_LONG).show();
        }

        if (results != null) {
            row = new TableRow(this);

            t1 = new TextView(this);
            t2 = new TextView(this);
            t3 = new TextView(this);
            //t4 = new TextView(this);

            t1.setText("Email");
            t2.setText("Group");
            t3.setText("Company");
            //t4.setText("Quota");

            t1.setTypeface(null, Typeface.BOLD);
            t2.setTypeface(null, Typeface.BOLD);
            t3.setTypeface(null, Typeface.BOLD);
            //t4.setTypeface(null, Typeface.BOLD);

            t1.setTextSize(14);
            t2.setTextSize(14);
            t3.setTextSize(14);
            //t4.setTextSize(14);

            t1.setPadding(20, 10, 0, 0);
            t2.setPadding(20, 10, 0, 0);
            t3.setPadding(20, 10, 0, 0);
            //t4.setPadding(20, 10, 0, 20);

            row.addView(t1);
            row.addView(t2);
            row.addView(t3);
            //row.addView(t4);

            user_table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < results.length(); i++) {
                try {
                    jrow = results.getJSONObject(i);
                    email = jrow.getString("email") ;
                    group = jrow.getString("group") ;
                    company = jrow.getString("company") ;
                    quotavm = jrow.getString("quotavm") ;
                } catch (Exception e) {
                    ;
                }

                row = new TableRow(this);

                t1 = new TextView(this);
                t2 = new TextView(this);
                //r_id = View.generateViewId() ;
                r_id = r.nextInt(200000) ;
                t2.setId(r_id) ;
                t3 = new TextView(this);
                //t4 = new TextView(this);

                //t1.setTextColor(getResources().getColor(R.color.yellow));
                //t2.setTextColor(getResources().getColor(R.color.dark_red));

                t1.setText(email);
                t2.setText(group);
                t3.setText(company);
                //t4.setText(quotavm);

                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);
                t3.setTypeface(null, 1);
                //t4.setTypeface(null, 1);

                t1.setTextSize(12);
                t2.setTextSize(12);
                t3.setTextSize(12);
                //t4.setTextSize(12);

                t1.setPadding(20, 10, 0, 0);
                t2.setPadding(20, 10, 0, 0);
                t3.setPadding(20, 10, 0, 0);
                //t4.setPadding(20, 10, 0, 20);

                row.addView(t1);
                row.addView(t2);
                row.addView(t3);
                //row.addView(t4);

                user_table.addView(row, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                MvmcApi.getGroup(getApplicationContext(), group, r_id, this) ;
            }
        }

        return ;
    }


}
