//  Created by react-native-create-bridge

package com.testcamera.capichicamera;

import android.util.Log;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;

public class CapichiCameraManager extends ViewGroupManager<CapichiTestView> {
    public static final String REACT_CLASS = "CapichiCamera";
    private String message = "NOT SET";
    @Override
    public String getName() {
        // Tell React the name of the module
        // https://facebook.github.io/react-native/docs/native-components-android.html#1-create-the-viewmanager-subclass
        return REACT_CLASS;
    }

    @Override
    public CapichiTestView createViewInstance(ThemedReactContext context){
        // Create a view here
        // https://facebook.github.io/react-native/docs/native-components-android.html#2-implement-method-createviewinstance
        return new CapichiTestView(context, this.message);
    }

//    @Override
//    public void onDropViewInstance(CapichiCameraView view) {
//        view.onHostDestroy();
//        super.onDropViewInstance(view);
//    }

    @ReactProp(name = "message")
    public void setMessage(CapichiTestView view, @Nullable String message) {
        Log.i("Set Message", "ANDROID_SAMPLE_UI");
//        view.setMessage(message);
    }

    //PART 3: Added Receive Event.
    @javax.annotation.Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onDone", //Same as name registered with receiveEvent
                        MapBuilder.of("registrationName", "onDone"))
                .build();
    }

}
