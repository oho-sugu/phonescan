/**
 * 
 */
package net.ohoooo.androidapps.phonescan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author suguru
 *
 */
public class CameraView extends SurfaceView
	implements SurfaceHolder.Callback, PictureCallback {

	private SurfaceHolder holder = null;
	private Camera camera = null;
	
	private Boolean taking = false;
	private CameraView cameraView = null;
	
	/**
	 * @param context
	 */
	public CameraView(Context context) {
		super(context);
		subConstructer();
	}
	
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		subConstructer();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		subConstructer();
	}

	private void subConstructer(){
		holder = getHolder();
		holder.addCallback(this);
		//holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		cameraView = this;
	}

	@Override
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		synchronized (taking) {
			Log.d("PhoneScan", "length of data" + arg0.length);
			camera.startPreview();
			taking = false;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("PhoneScan", "format="+format+", width="+width+", height="+height);
		Camera.Parameters params = camera.getParameters();
		params.setPreviewSize(width, height);
		camera.setParameters(params);
		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(camera != null) {
			return;
		}
		camera = Camera.open();
		Log.d("PhoneScan","FocusMode"+camera.getParameters().getFocusMode());
		Log.d("PhoneScan","MaxNumFocusArea"+camera.getParameters().getMaxNumFocusAreas());
		Log.d("PhoneScan","MaxNumMeteringArea"+camera.getParameters().getMaxNumMeteringAreas());
		try {
			camera.setPreviewDisplay(holder);
			camera.setDisplayOrientation(90);
		} catch (IOException e){
			if(camera != null) {
				camera.release();
				camera = null;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (taking) {
			if(!taking) {
				taking = true;
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
					focusAreas.add(new Camera.Area(new Rect(-50, 50, 50, -50), 1000));
					camera.getParameters().setFocusAreas(focusAreas);
					camera.getParameters().setMeteringAreas(focusAreas);
					camera.autoFocus(autoFocusListener);
					//camera.takePicture(null, null, null, this);
				}
			}
		}
		
		return super.onTouchEvent(event);
	}
	private Camera.AutoFocusCallback autoFocusListener = new Camera.AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			camera.takePicture(null, null, cameraView);
		}
	};
}
