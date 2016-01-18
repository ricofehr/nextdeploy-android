package io.nextdeploy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity who display a vm creation form
 * @author Eric Fehr (ricofehr@nextdeploy.io, @github: ricofehr)
 */
public class NewVmActivity extends NextDeployActivity {
    private String projectId = null ;
    private String flavorId = null ;
    private String commitId = null ;
    private String osId = null ;
    private String userId = null ;

    private View mVmFormView;
    private View mProgressView;

    //change projectid value
    public void setProjectId(String id) {
        projectId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vm);
        NextDeployApi.listProjects(getApplicationContext(), this);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void listHandler(JSONArray results, String last_log) {
        List<String> projects = new ArrayList<String>();
        String name = "", enabled = "" ;
        JSONObject jrow = null ;

        //reset all form values
        projectId = null ;
        flavorId = null ;
        userId = null ;
        commitId = null ;
        osId = null ;

        projects.add("Please select a Project") ;

        //create project spinner
        for (int i = 0; i < results.length(); i++) {
            try {
                jrow = results.getJSONObject(i);
                name = jrow.getString("name") ;
                enabled = jrow.getString("enabled") ;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (enabled.compareToIgnoreCase("false") == 0 || enabled.compareToIgnoreCase("0") == 0) {
                continue ;
            }

            projects.add(name) ;
        }

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.projectField);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, projects);

        // Attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new ProjectOnItemSelectedListener(this, results)) ;
    }

    public void spinnerHandler(String response, String match) {
        int i = 0 ;
        String name = "" ;
        String id = "" ;
        Spinner spinner = null ;
        ArrayAdapter<String> dataAdapter = null ;
        List<String> items = new ArrayList<String>() ;
        JSONObject jsonObject = null ;
        JSONArray jsonArray = null ;

        switch (match) {
            case "flavor":
                spinner = (Spinner) findViewById(R.id.flavorField);
                try {
                    jsonObject = new JSONObject(response).getJSONObject("vmsize");
                    id = jsonObject.getString("id");
                    name = id + "# " + jsonObject.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "user":
                spinner = (Spinner) findViewById(R.id.userField);
                try {
                    jsonObject = new JSONObject(response).getJSONObject(match);
                    id = jsonObject.getString("id");
                    name = id + "# " + jsonObject.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "branche":
                spinner = (Spinner) findViewById(R.id.branchField);
                try {
                    jsonObject = new JSONObject(response).getJSONObject(match);
                    name = jsonObject.getString("id");
                    //name = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                spinner.setOnItemSelectedListener(new BranchOnItemSelectedListener(this)) ;
                break;
            case "commit":
                spinner = (Spinner) findViewById(R.id.commitField);
                try {
                    jsonArray = new JSONObject(response).getJSONObject("branche").getJSONArray("commits");

                    for(i=0; i<jsonArray.length(); i++) {
                        id = jsonArray.getString(i);
                        items.add(id.substring(0,32)) ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items) ;
                spinner.setVisibility(View.GONE);
                break;
            case "systemimage":
                spinner = (Spinner) findViewById(R.id.osField);
                try {
                    jsonArray = new JSONObject(response).getJSONArray("systemimages");
                    for(i=0; i<jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i) ;
                        id = jsonObject.getString("id");
                        name = id + "# " + jsonObject.getString("name");
                        items.add(name) ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items) ;
                break;
        }

        if (dataAdapter == null) {
            dataAdapter = (ArrayAdapter<String>) spinner.getAdapter();
            if (dataAdapter != null) {
                for(i=0 ; i<dataAdapter.getCount() ; i++) {
                    items.add(dataAdapter.getItem(i));
                }
            }

            items.add(name);
            dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        }

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setEnabled(true);
    }

    /**
     * Execute submit of the form for create new vm
     */
    public void submitForm(View view) {
        flavorId = null ;
        userId = null ;
        commitId = null ;
        osId = null ;

        if (projectId == null) {
            Toast.makeText(getApplicationContext(), "Please select a project", Toast.LENGTH_LONG).show();
            return ;
        }

        //get flavor value
        Spinner spinner = (Spinner) findViewById(R.id.flavorField);
        flavorId = spinner.getSelectedItem().toString();
        if (flavorId == null || flavorId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select a flavor", Toast.LENGTH_LONG).show();
            return ;
        }
        flavorId = flavorId.replaceAll("[^0-9].*$", "");

        //get user value
        spinner = (Spinner) findViewById(R.id.userField);
        userId = spinner.getSelectedItem().toString();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select an user", Toast.LENGTH_LONG).show();
            return ;
        }
        userId = userId.replaceAll("[^0-9].*$", "");

        //get commit value
        spinner = (Spinner) findViewById(R.id.commitField);
        commitId = spinner.getSelectedItem().toString();
        if (commitId == null || commitId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select a commit", Toast.LENGTH_LONG).show();
            return;
        }

        //get os value
        spinner = (Spinner) findViewById(R.id.osField);
        osId = spinner.getSelectedItem().toString();
        if (osId == null || osId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select an operating system", Toast.LENGTH_LONG).show();
            return;
        }
        osId = osId.replaceAll("[^0-9].*$", "");

        showProgress(true);
        NextDeployApi.createVm(getApplicationContext(), this, projectId, flavorId, userId, commitId, osId) ;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        mVmFormView = findViewById(R.id.vm_form);
        mProgressView = findViewById(R.id.form_progress);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVmFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mVmFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Handler after post requesto to create new vm
     *
     * @param success is true if rest request returned 200
     */
    public void createVmHandler(boolean success) {
        showProgress(false);
        if (success) {
            startActivity(new Intent(getApplicationContext(), VmActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "An error occured, please try later or contact administrator", Toast.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * Reset form on the gui
     */
    public void resetForm() {
        //reset all form values
        projectId = null ;
        flavorId = null ;
        userId = null ;
        commitId = null ;
        osId = null ;

        //reset flavor spinner
        Spinner spinner = (Spinner) findViewById(R.id.flavorField);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);

        //reset user spinner
        spinner = (Spinner) findViewById(R.id.userField);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);

        //reset branch spinner
        spinner = (Spinner) findViewById(R.id.branchField);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);

        //reset commit spinner
        spinner = (Spinner) findViewById(R.id.commitField);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);

        //reset os spinner
        spinner = (Spinner) findViewById(R.id.osField);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);
    }
}

class ProjectOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {

    private NewVmActivity cur = null ;
    private JSONArray projects ;

    public ProjectOnItemSelectedListener(NewVmActivity cur, JSONArray projects) {
        this.cur = cur ;
        this.projects = projects ;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)  {
        JSONArray users = null, flavors = null, branchs = null ;
        String name = "", enabled = "", projectSelected, os = "" ;
        JSONObject jrow = null ;
        Button submit = (Button) cur.findViewById(R.id.button_submit) ;
        Spinner spinner = null ;

        cur.resetForm();

        //get project selected
        spinner = (Spinner) cur.findViewById(R.id.projectField);
        projectSelected = spinner.getSelectedItem().toString() ;

        //get details about project
        for (int i = 0; i < projects.length(); i++) {
            try {
                jrow = projects.getJSONObject(i);
                name = jrow.getString("name") ;
                users = jrow.getJSONArray("users") ;
                flavors = jrow.getJSONArray("vmsizes") ;
                branchs = jrow.getJSONArray("branches") ;
                os = jrow.getString("systemimagetype") ;
                cur.setProjectId(jrow.getString("id")) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (name.compareTo(projectSelected) != 0) {
                users = null ;
                flavors = null ;
                branchs = null ;
                os = "" ;
                cur.setProjectId(null) ;
                continue ;
            }

            break ;
        }

        if (users != null) {
            NextDeployApi.newVmHandler(cur.getApplicationContext(), cur, flavors, users, branchs, os);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}

class BranchOnItemSelectedListener extends Activity implements AdapterView.OnItemSelectedListener {

    private NewVmActivity cur = null ;
    private JSONArray branchs ;

    public BranchOnItemSelectedListener(NewVmActivity cur) {
        this.cur = cur ;
        this.branchs = branchs ;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)  {
        Spinner spinner = null ;
        String branchSelected = "" ;

        //reset commit spinner
        spinner = (Spinner) cur.findViewById(R.id.commitField);
        spinner.setAdapter(new ArrayAdapter<String>(cur, android.R.layout.simple_spinner_item, new ArrayList<String>()));
        spinner.setEnabled(false);

        //get branch selected
        spinner = (Spinner) cur.findViewById(R.id.branchField);
        branchSelected = spinner.getSelectedItem().toString() ;

        NextDeployApi.newVmHandler2(cur.getApplicationContext(), cur, branchSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}
