package fr.publicis_modem.mvmc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 * Main activity, globla entries for the app
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public class MainActivity extends MvmcActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t=(TextView)findViewById(R.id.textView1);
        t.setText("Welcome " + MvmcApi.EMAIL);
    }

    /** Called when the user clicks the Vm button */
    public void vmList(View view) {
        startActivity(new Intent(getApplicationContext(), VmActivity.class));
    }

    /** Called when the user clicks the NewVm button */
    public void vmNew(View view) {
        startActivity(new Intent(getApplicationContext(), NewVmActivity.class));
    }

    /** Called when the user clicks the project button */
    public void projectList(View view) {
        startActivity(new Intent(getApplicationContext(), ProjectActivity.class));
    }

    /** Called when the user clicks the project button */
    public void userList(View view) {
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
    }

    /** Called when the user clicks the project button */
    public void logout(View view) {
        MvmcApi.EMAIL = null ;
        MvmcApi.API_TOKEN = null ;
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}