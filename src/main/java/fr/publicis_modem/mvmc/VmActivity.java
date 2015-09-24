package fr.publicis_modem.mvmc;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

/**
 * Vm activity
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public class VmActivity extends MvmcActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm);
        MvmcApi.listVms(getApplicationContext(), this) ;
    }

    public void listHandler(JSONArray results, String last_log) {
        TableLayout vm_table=(TableLayout)findViewById(R.id.vm_table);
        TableRow row;
        TextView t1, t2, t3, t4, t5, t6;
        JSONObject jrow = null ;
        String commit = "", project = "", user = "", system = "", ip = "", date = "" ;
        int r_id, r_id2, r_id3 ;
        Random r = new Random();

        if (last_log != null && last_log.length() != 0) {
            Toast.makeText(getApplicationContext(), last_log, Toast.LENGTH_LONG).show();
        }

        if (results != null) {
            row = new TableRow(this);

            t1 = new TextView(this);
            t2 = new TextView(this);
            t3 = new TextView(this);
            //t4 = new TextView(this);
            //t5 = new TextView(this);

            t1.setText("Commit");
            t2.setText("Project");
            t3.setText("User");
            //t4.setText("System");
            //t5.setText("Ip");

            t1.setTypeface(null, Typeface.BOLD);
            t2.setTypeface(null, Typeface.BOLD);
            t3.setTypeface(null, Typeface.BOLD);
            //t4.setTypeface(null, Typeface.BOLD);
            //t5.setTypeface(null, Typeface.BOLD);

            t1.setTextSize(14);
            t2.setTextSize(14);
            t3.setTextSize(14);
            //t4.setTextSize(14);
            //t5.setTextSize(14);

            t1.setPadding(20, 10, 0, 0);
            t2.setPadding(20, 10, 0, 0);
            t3.setPadding(20, 10, 0, 0);
            //t4.setPadding(20, 10, 0, 0);
            //t5.setPadding(20, 10, 0, 10);

            row.addView(t1);
            row.addView(t2);
            row.addView(t3);
            //row.addView(t4);
            //row.addView(t5);

            vm_table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < results.length(); i++) {
                try {
                    jrow = results.getJSONObject(i);
                    Log.e("log listvm", jrow.toString());
                    commit = jrow.getString("commit").replaceAll("^.*-", "").substring(0,5) ;
                    project = jrow.getString("project") ;
                    user = jrow.getString("user") ;
                    system = jrow.getString("systemimage") ;
                    ip = jrow.getString("floating_ip") ;
                } catch (Exception e) {
                    ;
                }

                row = new TableRow(this);

                t1 = new TextView(this);

                t2 = new TextView(this);
                //r_id = View.generateViewId();
                r_id = r.nextInt(200000) ;
                t2.setId(r_id);

                t3 = new TextView(this);
                //r_id2 = View.generateViewId();
                r_id2 = r.nextInt(200000) ;
                t3.setId(r_id2);

                /*
                t4 = new TextView(this);
                //r_id3 = View.generateViewId();
                r_id3 = r.nextInt(200000) ;
                t4.setId(r_id3);

                t5 = new TextView(this);
                */
                //t1.setTextColor(getResources().getColor(R.color.yellow));
                //t2.setTextColor(getResources().getColor(R.color.dark_red));

                t1.setText(commit);
                t2.setText(project);
                t3.setText(user);
                //t4.setText(system);
                //t5.setText(ip);

                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);
                t3.setTypeface(null, 1);
                //t4.setTypeface(null, 1);
                //t5.setTypeface(null, 1);

                t1.setTextSize(10);
                t2.setTextSize(10);
                t3.setTextSize(10);
                //t4.setTextSize(10);
                //t5.setTextSize(10);

                t1.setPadding(20, 10, 0, 0);
                t2.setPadding(20, 10, 0, 0);
                t3.setPadding(20, 10, 0, 0);
                //t4.setPadding(20, 10, 0, 0);
                //t5.setPadding(20, 10, 0, 10);

                row.addView(t1);
                row.addView(t2);
                row.addView(t3);
                //row.addView(t4);
                //row.addView(t5);

                vm_table.addView(row, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                MvmcApi.getProject(getApplicationContext(), project, r_id, this) ;
                MvmcApi.getUser(getApplicationContext(), user, r_id2, this) ;
                //MvmcApi.getSystem(getApplicationContext(), system, r_id3, this) ;
            }
        }

        return ;
    }
}
