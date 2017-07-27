package io.nextdeploy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 *  Main activity, globla entries for the app
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public class MainActivity extends NextDeployActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t=(TextView)findViewById(R.id.textView1);
        t.setText("Welcome " + NextDeployApi.EMAIL);
    }

    /**
     *  Called when the user clicks the Vm button
     *  @param view The current Android View
     */
    public void vmList(View view)
    {
        startActivity(new Intent(getApplicationContext(), VmActivity.class));
    }

    /**
     *  Called when the user clicks the NewVm button
     *  @param view The current Android View
     */
    public void vmNew(View view)
    {
        startActivity(new Intent(getApplicationContext(), NewVmActivity.class));
    }

    /**
     *  Called when the user clicks the project button
     *  @param view The current Android View
     */
    public void projectList(View view)
    {
        startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
    }

    /**
     *  Called when the user clicks the user button
     *  @param view The current Android View
     */
    public void userList(View view)
    {
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
    }

    /**
     *  Called when the user clicks the logout button
     *  @param view The current Android View
     */
    public void logout(View view)
    {
        NextDeployApi.EMAIL = null;
        NextDeployApi.API_TOKEN = null;

        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}