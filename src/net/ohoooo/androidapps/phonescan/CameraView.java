/**
 * 
 */
package net.ohoooo.androidapps.phonescan;

import java.io.IOException;

import android.content.Context;
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
		try {
			camera.setPreviewDisplay(holder);
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
					camera.takePicture(null, null, null, this);
				}
			}
		}
		
		return super.onTouchEvent(event);
	}
}
