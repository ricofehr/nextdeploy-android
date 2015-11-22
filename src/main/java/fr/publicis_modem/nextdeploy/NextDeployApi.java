package fr.publicis_modem.nextdeploy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Library for request with nextdeploy api
 * @author Eric Fehr (eric.fehr@publicis-modem.fr, @github: ricofehr)
 */
public class NextDeployApi {
    private static AsyncHttpClient aSyncClient;
    private static String USER_AGENT = "Android NextDeploy UA";

    public static String API_TOKEN = "";
    public static String EMAIL = "" ;

    /**
     * Init asynchttpclient object for Rest request.
     * Set global parameters for the connexion.
     *
     * @return  if true, request is ready to perform
     */
    private static Boolean init() {
        // setup asynchronous client
        aSyncClient = new AsyncHttpClient();
        aSyncClient.setUserAgent(USER_AGENT);
        aSyncClient.addHeader("Accept", "application/json");
        aSyncClient.addHeader("Content-Type", "application/json");
        if (API_TOKEN != "") {
            aSyncClient.addHeader("Authorization", String.format("Token token=%s", API_TOKEN));
        }
        aSyncClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        aSyncClient.getHttpClient().getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        aSyncClient.getHttpClient().getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        aSyncClient.setTimeout(60000);

        return true ;
    }

    /**
     * Execute the signin post request into nextdeploy platform
     *
     * @param context   the android application context
     * @param email     the email who identify the user
     * @param password  his password for the credentials
     * @param cur       the android activity to forward the callback after processing
     * @return          false if exception occurs
     */
    public static Boolean signin(Context context, String email, String password, LoginActivity cur) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null ;
        AsyncHttpResponseHandler responseHandler = null ;

        if(!init()) {
            return false ;
        }

        try {
            jsonParams.put("email", email);
            jsonParams.put("password", password);
            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
            return false ;
        }

        responseHandler = new LoginHandler(cur) ;
        aSyncClient.post(context, getAbsoluteUrl(context, "/api/v1/users/sign_in"), entity, "application/json", responseHandler) ;

        return true ;
    }


    /**
     * List projects available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listProjects(Context context, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null ;

        if(!init()) {
            Log.e("failure", "init false") ;
            return false ;
        }

        responseHandler = new listContentHandler(cur, "projects") ;
        Log.e("nextdeployapi", "get /api/v1/projects") ;
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/projects"), responseHandler) ;

        return true ;
    }

    /**
     * List vms available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listVms(Context context, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new listContentHandler(cur, "vms");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/vms"), responseHandler);

        return true ;
    }

    /**
     * List users available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listUsers(Context context, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null ;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new listContentHandler(cur, "users");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/users"), responseHandler);

        return true ;
    }

    /**
     * Send post rest request for create a new vm
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @param projectId project parameter for the post request
     * @param flavorId  flavor parameter for the post request
     * @param userId   user parameter for the post request
     * @param commitId  commit parameter for the post request
     * @param osId  systemimage parameter for the post request
     * @return  false if exception occurs
     */
    public static Boolean createVm(Context context, NewVmActivity cur, String projectId, String flavorId, String userId, String commitId, String osId) {
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        try {
            jsonParams.put("project_id", projectId);
            jsonParams.put("vmsize_id", flavorId);
            jsonParams.put("user_id", userId);
            jsonParams.put("systemimage_id", osId);
            jsonParams.put("commit_id", commitId);

            entity = new StringEntity(jsonParams.toString());
        } catch (Exception e) {
            return false;
        }

        responseHandler = new createVmHandler(cur);
        aSyncClient.post(context, getAbsoluteUrl(context, "/api/v1/vms"), entity, "application/json", responseHandler) ;

        return true;
    }

    /**
     * Fill spinners for newvm form
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @param flavors the list of flavors id associated with the project checked
     * @param users the list of users id associated with the project checked
     * @param branchs the list of branchs id associated with the project checked
     * @param os the list of systemimages id associated with the project checked
     * @return      false if exception occurs
     */
    public static Boolean newVmHandler(Context context, NewVmActivity cur, JSONArray flavors, JSONArray users, JSONArray branchs, String os) {
        AsyncHttpResponseHandler responseHandler = null;
        int i = 0 ;
        String id = "";

        if(!init()) {
            Log.e("failure", "init false");
            return false ;
        }

        for(i=0; i<flavors.length(); i++) {
            try {
                id = flavors.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/vmsizes/"+id), new SpinnerHandler(cur, "flavor"));
        }

        for(i=0; i<users.length(); i++) {
            try {
                id = users.getString(i) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/users/"+id), new SpinnerHandler(cur, "user"));
        }

        for(i=0; i<branchs.length(); i++) {
            try {
                id = branchs.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/branches/"+id), new SpinnerHandler(cur, "branche"));
        }

        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/systemimages/type/"+os), new SpinnerHandler(cur, "systemimage"));

        return true;
    }

    /**
     * Fill commits spinner for newvm form
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean newVmHandler2(Context context, NewVmActivity cur, String branch) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/branches/"+branch), new SpinnerHandler(cur, "commit"));

        return true;
    }

    /**
     * Return details about one project
     *
     * @param context   the android application context
     * @param project_id    id for the project
     * @param r_id  id for the label on the android view
     * @param cur   the android activity to forward the callback after processing
     * @return  false if exception occurs
     */
    public static Boolean getProject(Context context, String project_id, int r_id, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(r_id, cur, "project");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/projects/" + project_id), responseHandler);

        return true;
    }

    /**
     * Return details about one user
     *
     * @param context   the android application context
     * @param user_id    id for the user
     * @param r_id  id for the label on the android view
     * @param cur   the android activity to forward the callback after processing
     * @return  false if exception occurs
     */
    public static Boolean getUser(Context context, String user_id, int r_id, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        Log.e("getuser", getAbsoluteUrl(context, "/api/v1/users/" + user_id)) ;

        responseHandler = new updateViewHandler(r_id, cur, "user");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/users/" + user_id), responseHandler);

        return true;
    }

    /**
     * Return details about one system
     *
     * @param context   the android application context
     * @param system_id    id for the system
     * @param r_id  id for the label on the android view
     * @param cur   the android activity to forward the callback after processing
     * @return  false if exception occurs
     */
    public static Boolean getSystem(Context context, String system_id, int r_id, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(r_id, cur, "systemimage");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/systemimages/" + system_id), responseHandler);

        return true;
    }

    /**
     * Return details about one group
     *
     * @param context   the android application context
     * @param group_id    id for the group
     * @param r_id  id for the label on the android view
     * @param cur   the android activity to forward the callback after processing
     * @return  false if exception occurs
     */
    public static Boolean getGroup(Context context, String group_id, int r_id, NextDeployActivity cur) {
        AsyncHttpResponseHandler responseHandler = null;

        if(!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(r_id, cur, "group");
        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/groups/" + group_id), responseHandler);

        return true;
    }

    /**
     * Concat preference endpoint with path of rest request
     *
     * @param context activity context
     * @param relativeUrl   path for the rest request
     * @return  absolute path for rest request
     */
    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return "https://" + sharedPref.getString("endpoint", "") + relativeUrl;
    }
}

/**
 * Handler for update label on android view
 */
class updateViewHandler extends AsyncHttpResponseHandler {
    private int r_id;
    private NextDeployActivity cur;
    private String match;

    /**
     * Constructor, init shared variables
     *
     * @param r_id  id of the label targetted on the android view
     * @param cur   android activity object for callback handler
     */
    public updateViewHandler( int r_id, NextDeployActivity cur, String match ) {
        this.r_id = r_id;
        this.cur = cur;
        this.match = match;
    }

    /**
     * Success function for rest request
     *
     * @param response  response from nextdeploy request
     */
    public void onSuccess(String response) {
        String ret = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject myResponse = jsonObject.getJSONObject(match);
            if (match.compareTo("user") == 0) ret = myResponse.getString("email");
            else if (match.compareTo("flavor") == 0) ret = myResponse.getString("title");
            else ret = myResponse.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.cur.valueHandler(this.r_id, ret);
    }

    /**
     * Failure function. Log error. Raise exception ?
     *
     * @param statusCode    http error code
     * @param error     error object
     * @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content) {
        Log.e("onFailure", error.getMessage());
    }
}

/**
 * Handler for list datas from nextdeploy rest request
 */
class listContentHandler extends AsyncHttpResponseHandler {
    private NextDeployActivity cur;
    private String match = "";

    /**
     * Constructor, init shared variables
     *
     * @param match  filter for the content
     * @param cur   android activity object for callback handler
     */
    public listContentHandler( NextDeployActivity cur, String match ) {
        this.cur = cur;
        this.match = match;
    }

    /**
     * Success function for rest request
     *
     * @param response  response from nextdeploy request
     */
    public void onSuccess(String response) {
        Log.e("onSuccess", response);
        String last_log = null;
        JSONArray results = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            results = jsonObject.getJSONArray(match);
        } catch (Exception e) {
            last_log = "Error Occured [Server's JSON response might be invalid]!" ;
            e.printStackTrace();
        }

        cur.listHandler(results, last_log);
    }

    /**
     * Failure function. Log error. Callback into the activity for displaying alert.
     *
     * @param statusCode    http error code
     * @param error     error object
     * @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content) {
        Log.e("onFailure", error.getMessage());
        String last_log = null;

        // When Http response code is '404'
        if(statusCode == 404){
            last_log = "Requested resource not found";
        }
        // When Http response code is '500'
        else if(statusCode == 500){
            last_log = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else{
            last_log = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.listHandler(null, last_log);
    }
}

/**
 * Handler for flavor datas from nextdeploy rest request
 */
class SpinnerHandler extends AsyncHttpResponseHandler {
    private NewVmActivity cur;
    private String match;

    /**
     * Constructor, init shared variables
     *
     * @param cur   android activity object for callback handler
     */
    public SpinnerHandler(NewVmActivity cur, String match) {
        this.cur = cur;
        this.match = match;
    }

    /**
     * Success function for rest request
     *
     * @param response  response from nextdeploy request
     */
    public void onSuccess(String response) {
        cur.spinnerHandler(response, match);
    }

    /**
     * Failure function. Log error. Callback into the activity for displaying alert.
     *
     * @param statusCode    http error code
     * @param error     error object
     * @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content) {
        Log.e("onFailure", error.getMessage() + match);
        String last_log = null;

        // When Http response code is '404'
        if(statusCode == 404){
            last_log = "Requested resource not found";
        }
        // When Http response code is '500'
        else if(statusCode == 500){
            last_log = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else{
            last_log = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.spinnerHandler(null, match);
    }
}

/**
 * Handler for authentification request
 */
class LoginHandler extends AsyncHttpResponseHandler {
    private LoginActivity cur;

    public LoginHandler(LoginActivity cur) {
        this.cur = cur;
    }

    public void onSuccess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject myResponse = jsonObject.getJSONObject("user");

            if (myResponse.getString("authentication_token").isEmpty()) {
                return;
            }
            else {
                NextDeployApi.EMAIL = myResponse.getString("email");
                NextDeployApi.API_TOKEN = myResponse.getString("authentication_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cur.signinHandler("Authentification is ok");
    }

    public void onFailure(int statusCode, Throwable error, String content) {
        Log.e("onFailure", error.getMessage());
        String lastLog = "";
        NextDeployApi.API_TOKEN = null;
        NextDeployApi.EMAIL = null;
        // When Http response code is '404'
        if(statusCode == 404){
            lastLog = "Requested resource not found";
        }
        // When Http response code is '500'
        else if(statusCode == 500){
            lastLog = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else{
            lastLog = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.signinHandler(lastLog);
    }
}

class createVmHandler extends AsyncHttpResponseHandler {
    private NewVmActivity cur;

    public createVmHandler(NewVmActivity cur) {
        this.cur = cur;
    }

    public void onSuccess(String response) {
        try {
            cur.createVmHandler(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onFailure(int statusCode, Throwable error, String content) {
        cur.createVmHandler(false);
    }
}