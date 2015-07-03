package com.fevi.fadong;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by 1000742 on 15. 7. 3..
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        parseInit();
    }

    private void parseInit() {
        Parse.initialize(this, "2iC7nc2t8jrm8DFPtV03BT1TiXODEkNy6cUomq95", "N8HSWDLfQqDKQ1d5okDbYuIHJtgg7yjugSdffDo1");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
