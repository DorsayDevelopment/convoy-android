package com.dorsaydevelopment.convoy;

import com.parse.Parse;

/**
 * Created by brycen on 15-03-11.
 */
public class ApplicationController extends android.app.Application {

    private static android.app.Application singletonInstance;

    public void onCreate() {
        Parse.initialize(this, "kpAPxWNrBwAomp48pDgQNFFXrpzsubCRpblNlLFd", "9On6nF8dkMmsMNnAgQvqwJDYAyF55hDyX4lXbnmv");
    }

    public static synchronized android.app.Application getInstance() {
        return singletonInstance;
    }

}
