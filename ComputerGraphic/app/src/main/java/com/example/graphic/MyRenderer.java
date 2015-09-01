package com.example.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MyRenderer {

	private float distanceToCamera;
	private Vertex startPoint;
	private WireShape axesXYZ, copyaxesXYZ, cube, copycube;
	private Paint paint;

	private final float[][] mirrorZ = getIdentityMatrix();
	private final float[][] mirrorY = getIdentityMatrix();
	private float[][] matrixProjection = getIdentityMatrix();
	private float[][] matrixFreeProjection = getIdentityMatrix();
	private final float[][] translate = getIdentityMatrix();
	private final float[][] rX = getIdentityMatrix();
	private final float[][] rY = getIdentityMatrix();
	private final float[][] rZ = getIdentityMatrix();
	private final float[][] scale = getIdentityMatrix();

	private float[][] getIdentityMatrix() {
		float[][] identity = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (i == j) {
					identity[i][j] = 1;
				} else {
					identity[i][j] = 0;
				}
			}
		}
		return identity;
	}

	public MyRenderer(Vertex startPoint, Paint paint, float distanceToCamera) {
		this.distanceToCamera = distanceToCamera;
		this.startPoint = startPoint;
		this.paint = paint;
		float axisLenght = 51000;

		matrixFreeProjection = getIdentityMatrix();
		matrixFreeProjection[2][0] = (float) Math.cos(Math.PI / 4);
		matrixFreeProjection[2][1] = (float) Math.cos(Math.PI / 4);
		matrixFreeProjection[2][2] = 0;
		cube = createShape();
		axesXYZ = new WireShape();
		axesXYZ.addEdge(new Edge(new Vertex(0, 0, 0), new Vertex(axisLenght, 0,
				0)));
		axesXYZ.addEdge(new Edge(new Vertex(0, 0, 0), new Vertex(0, axisLenght,
				0)));
		axesXYZ.addEdge(new Edge(new Vertex(0, 0, 0), new Vertex(0, 0,
				axisLenght)));
		mirrorY[1][1] = -1;
		mirrorZ[2][2] = -1;
		setMatrixProjection();
	}

	private WireShape createShape() {
		WireShape shape = new WireShape();
		Vertex[] frontSide = new Vertex[16];		

		frontSide[0] = new Vertex(20f, 0f, -20f);
		frontSide[1] = new Vertex(20f, 40f, -20f);
		frontSide[2] = new Vertex(0f, 40f, -20f);
		frontSide[3] = new Vertex(0f, 60f, -20f);
		frontSide[4] = new Vertex(60f, 60f, -20f);
		frontSide[5] = new Vertex(60f, 40f, -20f);
		frontSide[6] = new Vertex(40f, 40f, -20f);
		frontSide[7] = new Vertex(40f, 0f, -20f);
		

		frontSide[8] = new Vertex(40f, 20f, 0f);
		frontSide[9] = new Vertex(40f, 60f, 0f);
		frontSide[10] = new Vertex(20f, 60f, 0f);
		frontSide[11] = new Vertex(20f, 80f, 0f);
		frontSide[12] = new Vertex(80f, 80f, 0f);
		frontSide[13] = new Vertex(80f, 60f, 0f);
		frontSide[14] = new Vertex(60f, 60f, 0f);
		frontSide[15] = new Vertex(60f, 20f, 0f);
		

		shape.addEdge(new Edge(frontSide[0], frontSide[1]));
		shape.addEdge(new Edge(frontSide[1], frontSide[2]));
		shape.addEdge(new Edge(frontSide[2], frontSide[3]));
		shape.addEdge(new Edge(frontSide[3], frontSide[4]));
		shape.addEdge(new Edge(frontSide[4], frontSide[5]));
		shape.addEdge(new Edge(frontSide[5], frontSide[6]));
		shape.addEdge(new Edge(frontSide[6], frontSide[7]));
		shape.addEdge(new Edge(frontSide[7], frontSide[0]));
		
		shape.addEdge(new Edge(frontSide[8], frontSide[9]));
		shape.addEdge(new Edge(frontSide[9], frontSide[10]));
		shape.addEdge(new Edge(frontSide[10], frontSide[11]));
		shape.addEdge(new Edge(frontSide[11], frontSide[12]));
		shape.addEdge(new Edge(frontSide[12], frontSide[13]));
		shape.addEdge(new Edge(frontSide[13], frontSide[14]));
		shape.addEdge(new Edge(frontSide[14], frontSide[15]));
		shape.addEdge(new Edge(frontSide[15], frontSide[8]));

		shape.addEdge(new Edge(frontSide[0], frontSide[8]));
		shape.addEdge(new Edge(frontSide[7], frontSide[15]));
		shape.addEdge(new Edge(frontSide[1], frontSide[9]));
		shape.addEdge(new Edge(frontSide[2], frontSide[10]));
		shape.addEdge(new Edge(frontSide[3], frontSide[11]));
		shape.addEdge(new Edge(frontSide[4], frontSide[12]));
		shape.addEdge(new Edge(frontSide[5], frontSide[13]));
		shape.addEdge(new Edge(frontSide[6], frontSide[14]));

		return shape;
	}

	private void setMatrixProjection() {
		matrixProjection = getIdentityMatrix();
		matrixProjection[2][3] = 1 / distanceToCamera;
	}

	public void addCameraDistance(float distanceToCamera) {
		this.distanceToCamera += distanceToCamera;
		setMatrixProjection();
	}

	public void out() {

		copycube = new WireShape(cube);
		copyaxesXYZ = new WireShape(axesXYZ);
		setMatrixProjection();
		copycube.doMultibyMatrix(matrixProjection);
		copycube.doMultibyMatrix(matrixFreeProjection);
		copyaxesXYZ.doMultibyMatrix(matrixProjection);
		copyaxesXYZ.doMultibyMatrix(matrixFreeProjection);
		copycube.doMultibyMatrix(mirrorY);
		copyaxesXYZ.doMultibyMatrix(mirrorY);

	}

	public void translate(float x, float y, float z) {
		translate[3][0] = x;
		translate[3][1] = y;
		translate[3][2] = z;
		cube.doMultibyMatrix(translate);

	}

	public void rotateAroundAxisX(float angle) {
		angle = (float) (Math.PI / 45 * angle);
		rX[1][1] = rX[2][2] = (float) Math.cos(angle);
		rX[1][2] = (float) Math.sin(angle);
		rX[2][1] = -rX[1][2];
		cube.doMultibyMatrix(rX);
	}

	public void rotateAroundAxisY(float angle) {
		angle = (float) (Math.PI / 45 * angle);
		rY[0][0] = rY[2][2] = (float) Math.cos(angle);
		rY[0][2] = -(float) Math.sin(angle);
		rY[2][0] = -rY[0][2];
		cube.doMultibyMatrix(rY);
	}

	public void rotateAroundAxisZ(float angle) {
		angle = (float) (Math.PI / 45 * angle);
		rZ[0][0] = rZ[1][1] = (float) Math.cos(angle);
		rZ[0][1] = (float) Math.sin(angle);
		rZ[1][0] = -rZ[0][1];
		cube.doMultibyMatrix(rZ);
	}

	public void scale(float kx, float ky, float kz) {
		scale[0][0] = kx;
		scale[1][1] = ky; 
		scale[2][2] = kz;
		cube.doMultibyMatrix(scale);
	}

	public void Draw(Canvas canvas) {
		out();

		canvas.drawText("Y", 120, 100, paint);
		canvas.drawText("Z", 600, 400, paint);
		canvas.drawText("X", 1000, 920, paint);
		canvas.drawText(String.valueOf(cube.getEdges().get(0).startVertex.x),
				800, 200, paint);
		canvas.drawText(String.valueOf(cube.getEdges().get(0).startVertex.y),
				800, 260, paint);
		canvas.drawText(String.valueOf(cube.getEdges().get(0).startVertex.z),
				800, 320, paint);
		paint.setColor(Color.BLUE);
		copycube.DrawShape(paint, canvas, startPoint);
		paint.setColor(Color.BLACK);
		copyaxesXYZ.DrawShape(paint, canvas, startPoint);


        canvas.drawLine(5f,3f,7f,5f,paint);
	}

}
