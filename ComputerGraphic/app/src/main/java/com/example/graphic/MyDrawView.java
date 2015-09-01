package com.example.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyDrawView extends View {
	Context c;
	Paint paint = new Paint();
	private MyRenderer renderer;
	private final float increment = 1;

	public MyDrawView(Context context, AttributeSet attribs) {
		super(context, attribs);
		c = context;
		paint.setColor(Color.BLACK);
		int scaledSize = getResources().getDimensionPixelSize(
				R.dimen.myFontSize);
		int lineSize = getResources().getDimensionPixelSize(R.dimen.myLineSize);
		paint.setTextSize(scaledSize);
		paint.setStrokeWidth(lineSize);
		renderer = new MyRenderer(new Vertex(100, 450, 0), paint, 500);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		renderer.Draw(canvas);
	}

	public void doTransX_P() {

		renderer.translate(increment, 0, 0);

		invalidate();
	}

	public void doTransX_M() {

		renderer.translate(-increment, 0, 0);
		invalidate();
	}

	public void doTransY_P() {
		renderer.translate(0, increment, 0);
		invalidate();
	}

	public void doTransY_M() {
		renderer.translate(0, -increment, 0);
		invalidate();
	}

	public void doTransZ_P() {
		renderer.translate(0, 0, increment);
		invalidate();
	}

	public void doTransZ_M() {
		renderer.translate(0, 0, -increment);
		invalidate();
	}

	public void doRotX_P() {
		renderer.rotateAroundAxisX(increment);
		invalidate();
	}

	public void doRotX_M() {
		renderer.rotateAroundAxisX(-increment);
		invalidate();
	}

	public void doRotY_P() {
		renderer.rotateAroundAxisY(increment);
		invalidate();
	}

	public void doRotY_M() {
		renderer.rotateAroundAxisY(-increment);
		invalidate();
	}

	public void doRotZ_P() {
		renderer.rotateAroundAxisZ(increment);
		invalidate();
	}

	public void doRotZ_M() {
		renderer.rotateAroundAxisZ(-increment);
		invalidate();
	}

	public void doScaleP() {
		renderer.scale(1.01f, 1.01f, 1.01f);
		invalidate();
	}

	public void doScaleM() {
		renderer.scale(0.99f, 0.99f, 0.99f);
		invalidate();
	}
}
