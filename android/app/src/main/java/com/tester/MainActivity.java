package com.tester;

import android.os.Bundle;
import com.facebook.react.ReactActivity;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript. This is
     * used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "tester";
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // A obj = new A();
        // obj.helloWorld(this);
    }
}
