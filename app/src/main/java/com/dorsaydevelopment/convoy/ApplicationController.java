package com.dorsaydevelopment.convoy;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by brycen on 15-03-11.
 */
public class ApplicationController extends Application {

    private static Application singletonInstance;

    public void onCreate() {
        Parse.initialize(this, "kpAPxWNrBwAomp48pDgQNFFXrpzsubCRpblNlLFd", "9On6nF8dkMmsMNnAgQvqwJDYAyF55hDyX4lXbnmv");
    }

    public static synchronized Application getInstance() {
        return singletonInstance;
    }

}
