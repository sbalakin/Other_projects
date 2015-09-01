package com.example.graphic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ZoomControls;

public class MainActivity extends Activity {	
	ZoomControls zoomControlsRotateX;
	ZoomControls zoomControlsRotateY;
	ZoomControls zoomControlsRotateZ;
	ZoomControls zoomControlsX;
	ZoomControls zoomControlsY;
	ZoomControls zoomControlsZ; 
	ZoomControls zoomScale;

	MyDrawView drawView;

    boolean isPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		getID();
		drawView = (MyDrawView) findViewById(R.id.drawLayout);
	}

	private void getID() {		
		zoomControlsRotateX = (ZoomControls) findViewById(R.id.zoomControlsRotateX);
		zoomControlsRotateY = (ZoomControls) findViewById(R.id.zoomControlsRotateY);
		zoomControlsRotateZ = (ZoomControls) findViewById(R.id.zoomControlsRotateZ);
		zoomControlsX = (ZoomControls) findViewById(R.id.zoomControlsX);
		zoomControlsY = (ZoomControls) findViewById(R.id.zoomControlsY);
		zoomControlsZ = (ZoomControls) findViewById(R.id.zoomControlsZ);
		zoomScale = (ZoomControls) findViewById(R.id.zoomScalePlus);
		
		zoomControlsRotateX.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotX_P();
			}
		});
		
		zoomControlsRotateX.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotX_M();
			}
		});
		
		zoomControlsRotateY.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotY_P();
			}
		});
		
		zoomControlsRotateY.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotY_M();
			}
		});
		
		zoomControlsRotateZ.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotZ_P();
			}
		});
		
		zoomControlsRotateZ.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doRotZ_M();
			}
		});
		
		//////////////////////////////////////////////////////////////////////////
		
		zoomControlsX.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransX_P();
			}
		});
		
		zoomControlsX.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransX_M();
			}
		});
		
		zoomControlsY.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransY_P();
			}
		});
		
		zoomControlsY.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransY_M();
			}
		});
		
		zoomControlsZ.setOnZoomInClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransZ_P();
			}
		});
		
		zoomControlsZ.setOnZoomOutClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				drawView.doTransZ_M();
			}
		});
		
		//////////////////////////////////////////////////////////////////////////
		
		zoomScale.setOnZoomInClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawView.doScaleP();
			}
		});
		
        zoomScale.setOnZoomOutClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawView.doScaleM();
			}
		});
	}

}
