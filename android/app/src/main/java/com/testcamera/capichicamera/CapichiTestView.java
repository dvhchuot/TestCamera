package com.testcamera.capichicamera;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.ConstraintTableLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.image.ReactImageView;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.filter.Filters;
import com.testcamera.R;

import java.io.File;
import java.util.ArrayList;

public class CapichiTestView extends ConstraintLayout implements LifecycleEventListener, View.OnClickListener {
    private Context context;

    private final static CameraLogger LOG = CameraLogger.create("DemoApp");
    private final static boolean USE_FRAME_PROCESSOR = false;
    private final static boolean DECODE_BITMAP = true;

    private ImageView btnRecord;
    private ImageView btnClose;
    private ImageView btnFlip;
    private ImageView btnFilter;
    private ImageView btnFlash;
    private ImageView btnGallerySelect;
    private ImageView btnEraseSegment;
    private ImageView btnSavedSelect;
    private ImageView btnDone;
    private CameraView camera;
    private ViewGroup controlPanel;
    private long mCaptureTime;

    private int mCurrentFilter = 0;
    private final Filters[] mAllFilters = Filters.values();
    private ArrayList<File> mListFile = new ArrayList<File>();
    private File mCurrentFile;

    public CapichiTestView(Context context, String message) {
        super(context);
        this.context = context;
        init();
    }

    public void init() {
        inflate(this.context, R.layout.activity_camera, this);

        btnRecord = findViewById(R.id.imv_record);
        btnClose = findViewById(R.id.imv_close);
        btnFlip = findViewById(R.id.imv_flip_camera);
        btnFilter = findViewById(R.id.imv_filter);
        btnFlash = findViewById(R.id.imv_flash);
        btnGallerySelect = findViewById(R.id.imv_gallery_select);
        btnEraseSegment = findViewById(R.id.imv_erase_segment);
        btnSavedSelect = findViewById(R.id.imv_save_selected);
        btnDone = findViewById(R.id.imv_done);

        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(getLifecycleOwner());
        camera.addCameraListener(new Listener());

        /*********/
        btnRecord.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnFlip.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnGallerySelect.setOnClickListener(this);
        btnEraseSegment.setOnClickListener(this);
        btnSavedSelect.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    private LifecycleOwner getLifecycleOwner() {
        while (!(this.context instanceof LifecycleOwner)) {
            this.context = ((ContextWrapper) this.context).getBaseContext();
        }
        return (LifecycleOwner) this.context;
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_close:
                onClose();
                break;
            case R.id.imv_record:
                captureVideo();
                break;
            case R.id.imv_flip_camera:
                onFlipCamera();
                break;
            case R.id.imv_filter:
                onFilter();
                break;
            case R.id.imv_flash:
                onChangeFlash();
                break;
            case R.id.imv_gallery_select:
                onGallerySelect();
                break;
            case R.id.imv_erase_segment:
                onEraseSegment();
                break;
            case R.id.imv_save_selected:
                onSavedSelect();
                break;
            case R.id.imv_done:
                onDoneRecord();
                break;
        }
    }

    private void onFilter() {
        if (camera.getPreview() != Preview.GL_SURFACE) {
            message("Filters are supported only when preview is Preview.GL_SURFACE.", true);
            return;
        }
        if (mCurrentFilter < mAllFilters.length - 1) {
            mCurrentFilter++;
        } else {
            mCurrentFilter = 0;
        }
        Filters filter = mAllFilters[mCurrentFilter];
        message(filter.toString(), false);

        camera.setFilter(filter.newInstance());
    }

    private void onFlipCamera() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;

            case FRONT:
                message("Switched to front camera!", false);
                break;
        }
    }

    private void captureVideo() {
        Log.d("Capichi", "captureVideo");
        if (camera.getMode() == Mode.PICTURE) {
            message("Can't record HQ videos while in PICTURE mode.", false);
            return;
        }
//        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        message("Recording for 15 seconds...", true);
        if (camera.isTakingVideo()){
            Log.d("Capichi", "stop record");
            camera.stopVideo();}
        else{
            Log.d("Capichi", "start record");
            mCurrentFile = VideoUtils.getOutputMediaFile();
            camera.takeVideo(new File(this.context.getFilesDir(), "video.mp4"), 15000);
        }
    }

    class MergeVideoTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            return VideoUtils.mergeVideo(getCurrentListFile(),false);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            onReceiveNativeEvent(result);

        }
    }

    public void onReceiveNativeEvent(String result) {
        WritableMap event = Arguments.createMap();
        event.putString("url", result);
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onDone",
                event);
    }


    private void onDoneRecord() {
        new MergeVideoTask().execute();
    }

    private void onSavedSelect() {

    }

    private void onEraseSegment() {

    }

    private void onGallerySelect() {

    }

    private void onChangeFlash() {
        Log.d("Capichi", "onChangeFlash");
    }

    protected ArrayList<File> getCurrentListFile() {
        return mListFile;
    }

    private void onClose() {
        /** Close function */
    }

    private class  Listener extends CameraListener {
        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            Log.e("onCameraOpened", " 1111111");
//            ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
//            for (int i = 0; i < group.getChildCount(); i++) {
//                OptionView view = (OptionView) group.getChildAt(i);
//                view.onCameraOpened(camera, options);
//            }
        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            Log.d("Capichi", "Camera Error");
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (camera.isTakingVideo()) {
                message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }

            // This can happen if picture was taken with a gesture.
            long callbackTime = System.currentTimeMillis();
            if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
            LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - mCaptureTime);
//            PicturePreviewActivity.setPictureResult(result);
//            Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
//            intent.putExtra("delay", callbackTime - mCaptureTime);
//            startActivity(intent);
            mCaptureTime = 0;
            LOG.w("onPictureTaken called! Launched activity.");
        }
        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            Log.e("onVideoTaken", " 22222222");
            super.onVideoTaken(result);
            mListFile.add(result.getFile());
//            LOG.w("onVideoTaken called! Launching activity.");
//            VideoPreviewActivity.setVideoResult(result);
//            Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
//            startActivity(intent);
//            LOG.w("onVideoTaken called! Launched activity.");
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
        }

        @Override
        public void onAutoFocusStart(@NonNull PointF point) {
            super.onAutoFocusStart(point);
        }

        @Override
        public void onAutoFocusEnd(boolean successful, @NonNull PointF point) {
            super.onAutoFocusEnd(successful, point);
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
            message("Exposure correction:" + newValue, false);
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            message("Zoom:" + newValue, false);
        }
        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            LOG.e("onVideoRecordingStart!");
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            message("Video taken. Processing...", false);
            LOG.e("onVideoRecordingEnd!");
        }
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }

    //PART 3: Added Receive Event.
    public void callNativeEvent() {
        Log.i("Call Native Event", "ANDROID_SAMPLE_UI");
        //This output a message to Javascript as an event.
        WritableMap event = Arguments.createMap();
        event.putString("customNativeEventMessage", "Emitted an event"); //Emmitting an event to Javascript

        //Create a listener where if there method topChange, send the event to the js.
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "nativeClick",    //name hast to be same as getExportedCustomDirectEventTypeConstants
                event);
    }

    private void message(@NonNull String content, boolean important) {
        if (important) {
            LOG.w(content);
//            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
        } else {
            LOG.i(content);
//            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }
}
