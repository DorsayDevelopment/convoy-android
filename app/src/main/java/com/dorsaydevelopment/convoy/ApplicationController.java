package com.dorsaydevelopment.convoy;

import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by brycen on 15-03-11.
 */
public class ApplicationController extends android.app.Application {

    private static android.app.Application singletonInstance;

    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "kpAPxWNrBwAomp48pDgQNFFXrpzsubCRpblNlLFd", "9On6nF8dkMmsMNnAgQvqwJDYAyF55hDyX4lXbnmv");
    }

    public boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public static synchronized android.app.Application getInstance() {
        return singletonInstance;
    }

}
