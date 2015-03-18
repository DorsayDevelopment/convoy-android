package com.dorsaydevelopment.convoy;

import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by brycen on 15-03-11.
 */
public class ApplicationController extends android.app.Application {

    private static ApplicationController singletonInstance;

    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "kpAPxWNrBwAomp48pDgQNFFXrpzsubCRpblNlLFd", "9On6nF8dkMmsMNnAgQvqwJDYAyF55hDyX4lXbnmv");

        singletonInstance = this;
    }

    public static synchronized ApplicationController getInstance() {
        return singletonInstance;
    }

    public void getFacebookInfo() {
        final ParseUser user = ParseUser.getCurrentUser();
        new Request(
            ParseFacebookUtils.getSession(),
            "/me",
            null,
            HttpMethod.GET,
            new Request.Callback() {
                public void onCompleted(Response response) {
                    try {
                        GraphObject graphObject = response.getGraphObject();
                        JSONObject jsonObject = graphObject.getInnerJSONObject();
                        user.put("firstName", jsonObject.getString("first_name"));
                        user.put("lastName", jsonObject.getString("last_name"));
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("MyApp", "User logged in with Facebook as " + ParseUser.getCurrentUser().get("firstName"));
                            }
                        });
                    } catch (JSONException je) {
                        Log.e("FBLogin", je.toString());
                    }
                }
            }
        ).executeAsync();
    }

}
