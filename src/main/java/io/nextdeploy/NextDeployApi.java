package io.nextdeploy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Library for request with nextdeploy api
 *  @author Eric Fehr (ricofehr@nextdeploy.io, github: ricofehr)
 */
public class NextDeployApi {
    private static AsyncHttpClient aSyncClient;
    private static String USER_AGENT = "Android NextDeploy UA";

    public static String API_TOKEN = "";
    public static String EMAIL = "";

    /**
     * Init asynchttpclient object for Rest request.
     * Set global parameters for the connexion.
     *
     * @return  if true, request is ready to perform
     */
    private static Boolean init()
    {
        // setup asynchronous client
        aSyncClient = new AsyncHttpClient();
        aSyncClient.setUserAgent(USER_AGENT);
        aSyncClient.addHeader("Accept", "application/json");
        aSyncClient.addHeader("Content-Type", "application/json");
        if (!API_TOKEN.equals("")) {
            aSyncClient.addHeader("Authorization", String.format("Token token=%s", API_TOKEN));
        }
        aSyncClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        aSyncClient.getHttpClient().getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        aSyncClient.getHttpClient().getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        aSyncClient.setTimeout(60000);

        return true;
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
    public static Boolean signin(Context context, String email, String password, LoginActivity cur)
    {
        JSONObject jsonParams = new JSONObject();
        JSONObject jsonRoot = new JSONObject();
        StringEntity entity;
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/users/sign_in");

        if (!init()) {
            return false;
        }

        try {
            jsonParams.put("email", email);
            jsonParams.put("password", password);
            jsonRoot.put("user", jsonParams);
            entity = new StringEntity(jsonRoot.toString());
        } catch (Exception e) {
            return false;
        }

        responseHandler = new LoginHandler(cur);
        aSyncClient.post(context, absoluteUrl, entity, "application/json", responseHandler);

        return true;
    }


    /**
     * List projects available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listProjects(Context context, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/projects");

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new listContentHandler(cur, "projects");
        //Log.e("nextdeployapi", "get /api/v1/projects");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     * List vms available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listVms(Context context, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/vms");

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new listContentHandler(cur, "vms");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     * List users available for the users
     *
     * @param context   the android application context
     * @param cur   the android activity to forward the callback after processing
     * @return      false if exception occurs
     */
    public static Boolean listUsers(Context context, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/users");

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new listContentHandler(cur, "users");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
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
    public static Boolean createVm(final Context context, final NewVmActivity cur, final String projectId,
                                   final String flavorId, final String userId, final String commitId, final String osId)
    {
        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        aSyncClient.get(context, getAbsoluteUrl(context, "/api/v1/projects/" + projectId), null, new JsonHttpResponseHandler() {
        //mSyncClient.get(getAbsoluteUrl(context, "/api/v1/projects/" + projectId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject jsonParams = new JSONObject();
                JSONObject jsonRoot = new JSONObject();
                JSONObject projectJson;
                JSONArray technos;
                StringEntity entity;
                AsyncHttpResponseHandler responseHandler;
                String absoluteUrl = getAbsoluteUrl(context, "/api/v1/vms/short");
                Log.e("okproject", response.toString());

                // success logic here
                try {
                    projectJson = new JSONObject(response.toString()).getJSONObject("project");
                    technos = projectJson.getJSONArray("technos");
                    jsonParams.put("topic", commitId.split("-")[1]);
                    jsonParams.put("project_id", projectId);
                    jsonParams.put("vmsize_id", flavorId);
                    jsonParams.put("user_id", userId);
                    jsonParams.put("systemimage_id", osId);
                    jsonParams.put("technos", technos);
                    jsonParams.put("is_auth", true);
                    jsonParams.put("is_prod", false);
                    jsonParams.put("is_cached", false);
                    jsonParams.put("is_backup", false);
                    jsonParams.put("is_ci", false);
                    jsonParams.put("is_jenkins", false);
                    jsonParams.put("is_ro", false);
                    jsonParams.put("is_ht", false);
                    jsonParams.put("layout", "fr");
                    jsonParams.put("htlogin", projectJson.getString("login"));
                    jsonParams.put("htpassword", projectJson.getString("password"));
                    jsonParams.put("commit_id", commitId);
                    jsonRoot.put("vm", jsonParams);
                    Log.e("vmJson", jsonRoot.toString());
                    entity = new StringEntity(jsonRoot.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                responseHandler = new createVmHandler(cur);
                aSyncClient.post(context, absoluteUrl, entity, "application/json", responseHandler);
            }
            /*
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // handle failure here
            }*/

        });

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
    public static Boolean newVmHandler(Context context, NewVmActivity cur, JSONArray flavors,
                                       JSONArray users, JSONArray branchs, JSONArray os)
    {
        int i;
        String id = "";
        String absoluteUrlFlavors;
        String absoluteUrlUsers;
        String absoluteUrlBranches;
        String absoluteUrlSystemImages;

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        for(i = 0; i < flavors.length(); i++) {
            try {
                id = flavors.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            absoluteUrlFlavors = getAbsoluteUrl(context, "/api/v1/vmsizes/" + id);
            aSyncClient.get(context, absoluteUrlFlavors, new SpinnerHandler(cur, "flavor"));
        }

        for(i = 0; i < users.length(); i++) {
            try {
                id = users.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            absoluteUrlUsers = getAbsoluteUrl(context, "/api/v1/users/" + id);
            aSyncClient.get(context, absoluteUrlUsers, new SpinnerHandler(cur, "user"));
        }

        for(i = 0; i < branchs.length(); i++) {
            try {
                id = branchs.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            absoluteUrlBranches = getAbsoluteUrl(context, "/api/v1/branches/" + id);
            aSyncClient.get(context, absoluteUrlBranches, new SpinnerHandler(cur, "branche"));
        }

        for(i = 0; i < os.length(); i++) {
            try {
                id = os.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            absoluteUrlSystemImages = getAbsoluteUrl(context, "/api/v1/systemimages/" + id);
            aSyncClient.get(context, absoluteUrlSystemImages, new SpinnerHandler(cur, "systemimage"));
        }

        return true;
    }

    /**
     *  Fill commits spinner for newvm form
     *
     *  @param context   the android application context
     *  @param cur   the android activity to forward the callback after processing
     *  @return      false if exception occurs
     */
    public static Boolean newVmHandler2(Context context, NewVmActivity cur, String branch)
    {
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/branches/" + branch);

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        aSyncClient.get(context, absoluteUrl, new SpinnerHandler(cur, "commit"));

        return true;
    }

    /**
     *  Return details about one project
     *
     *  @param context   the android application context
     *  @param projectId    id for the project
     *  @param rId  id for the label on the android view
     *  @param cur   the android activity to forward the callback after processing
     *  @return  false if exception occurs
     */
    public static Boolean getProject(Context context, String projectId, int rId, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/projects/" + projectId);

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(rId, cur, "project");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     *  Return details about one user
     *
     *  @param context   the android application context
     *  @param userId    id for the user
     *  @param rId  id for the label on the android view
     *  @param cur   the android activity to forward the callback after processing
     *  @return  false if exception occurs
     */
    public static Boolean getUser(Context context, String userId, int rId, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/users/" + userId);

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(rId, cur, "user");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     *  Return details about one system
     *
     *  @param context   the android application context
     *  @param systemId    id for the system
     *  @param rId  id for the label on the android view
     *  @param cur   the android activity to forward the callback after processing
     *  @return  false if exception occurs
     */
    public static Boolean getSystem(Context context, String systemId, int rId, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/systemimages/" + systemId);

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(rId, cur, "systemimage");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     *  Return details about one group
     *
     *  @param context   the android application context
     *  @param groupId    id for the group
     *  @param rId  id for the label on the android view
     *  @param cur   the android activity to forward the callback after processing
     *  @return  false if exception occurs
     */
    public static Boolean getGroup(Context context, String groupId, int rId, NextDeployActivity cur)
    {
        AsyncHttpResponseHandler responseHandler;
        String absoluteUrl = getAbsoluteUrl(context, "/api/v1/groups/" + groupId);

        if (!init()) {
            Log.e("failure", "init false");
            return false;
        }

        responseHandler = new updateViewHandler(rId, cur, "group");
        aSyncClient.get(context, absoluteUrl, responseHandler);

        return true;
    }

    /**
     *  Concat preference endpoint with path of rest request
     *
     *  @param context activity context
     *  @param relativeUrl   path for the rest request
     *  @return  absolute path for rest request
     */
    private static String getAbsoluteUrl(Context context, String relativeUrl)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return "https://" + sharedPref.getString("endpoint", "") + relativeUrl;
    }
}

/**
 *  Handler for update label on android view
 */
class updateViewHandler extends AsyncHttpResponseHandler {
    private int rId;
    private NextDeployActivity cur;
    private String match;

    /**
     *  Constructor, init shared variables
     *
     *  @param rId  id of the label targetted on the android view
     *  @param cur   android activity object for callback handler
     *  @param match
     */
    public updateViewHandler(int rId, NextDeployActivity cur, String match)
    {
        this.rId = rId;
        this.cur = cur;
        this.match = match;
    }

    /**
     *  Success function for rest request
     *
     *  @param response  response from nextdeploy request
     */
    public void onSuccess(String response)
    {
        String ret = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject myResponse = jsonObject.getJSONObject(match);

            if (match.compareTo("user") == 0) {
                ret = myResponse.getString("email");
            }
            else if (match.compareTo("flavor") == 0) {
                ret = myResponse.getString("title");
            }
            else {
                ret = myResponse.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.cur.valueHandler(this.rId, ret);
    }

    /**
     *  Failure function. Log error. Raise exception ?
     *
     *  @param statusCode    http error code
     *  @param error     error object
     *  @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content)
    {
        Log.e("onFailure", error.getMessage());
    }
}

/**
 *  Handler for list datas from nextdeploy rest request
 */
class listContentHandler extends AsyncHttpResponseHandler {
    private NextDeployActivity cur;
    private String match = "";

    /**
     *  Constructor, init shared variables
     *
     *  @param match  filter for the content
     *  @param cur   android activity object for callback handler
     */
    public listContentHandler(NextDeployActivity cur, String match)
    {
        this.cur = cur;
        this.match = match;
    }

    /**
     *  Success function for rest request
     *
     *  @param response  response from nextdeploy request
     */
    public void onSuccess(String response)
    {
        //Log.e("onSuccess", response);
        String lastLog = null;
        JSONArray results = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            results = jsonObject.getJSONArray(match);
        } catch (Exception e) {
            lastLog = "Error Occured [Server's JSON response might be invalid]!";
            e.printStackTrace();
        }

        cur.listHandler(results, lastLog);
    }

    /**
     *  Failure function. Log error. Callback into the activity for displaying alert.
     *
     *  @param statusCode    http error code
     *  @param error     error object
     *  @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content) {
        Log.e("onFailure", error.getMessage());
        String lastLog;

        // When Http response code is '404'
        if (statusCode == 404){
            lastLog = "Requested resource not found";
        }
        // When Http response code is '500'
        else if (statusCode == 500){
            lastLog = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else {
            lastLog = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.listHandler(null, lastLog);
    }
}

/**
 *  Handler for flavor datas from nextdeploy rest request
 */
class SpinnerHandler extends AsyncHttpResponseHandler {
    private NewVmActivity cur;
    private String match;

    /**
     *  Constructor, init shared variables
     *
     *  @param cur   android activity object for callback handler
     */
    public SpinnerHandler(NewVmActivity cur, String match)
    {
        this.cur = cur;
        this.match = match;
    }

    /**
     *  Success function for rest request
     *
     *  @param response  response from nextdeploy request
     */
    public void onSuccess(String response)
    {
        cur.spinnerHandler(response, match);
    }

    /**
     *  Failure function. Log error. Callback into the activity for displaying alert.
     *
     *  @param statusCode    http error code
     *  @param error     error object
     *  @param content   error message
     */
    public void onFailure(int statusCode, Throwable error, String content)
    {
        Log.e("onFailure", error.getMessage() + match);
        String lastLog = null;

        // When Http response code is '404'
        if (statusCode == 404) {
            lastLog = "Requested resource not found";
        }
        // When Http response code is '500'
        else if (statusCode == 500) {
            lastLog = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else {
            lastLog = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.spinnerHandler(null, lastLog);
    }
}

/**
 *  Handler for authentification request
 */
class LoginHandler extends AsyncHttpResponseHandler {
    private LoginActivity cur;

    public LoginHandler(LoginActivity cur)
    {
        this.cur = cur;
    }

    public void onSuccess(String response)
    {
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

    public void onFailure(int statusCode, Throwable error, String content)
    {
        Log.e("onFailure", error.getMessage());
        String lastLog = "";
        NextDeployApi.API_TOKEN = null;
        NextDeployApi.EMAIL = null;

        // When Http response code is '404'
        if (statusCode == 404) {
            lastLog = "Requested resource not found";
        }
        // When Http response code is '500'
        else if (statusCode == 500) {
            lastLog = "Something went wrong at server end";
        }
        // When Http response code other than 404, 500
        else {
            lastLog = "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]";
        }

        cur.signinHandler(lastLog);
    }
}

class createVmHandler extends AsyncHttpResponseHandler {
    private NewVmActivity cur;

    public createVmHandler(NewVmActivity cur)
    {
        this.cur = cur;
    }

    public void onSuccess(String response)
    {
        try {
            cur.createVmHandler(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onFailure(int statusCode, Throwable error, String content)
    {
        cur.createVmHandler(false);
    }
}