package com.example.graphic;

public class Vertex {

	public float x;
	public float y;
	public float z;

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vertex(Vertex vertexToCopy) {
		this.x = vertexToCopy.x;
		this.y = vertexToCopy.y;
		this.z = vertexToCopy.z;
	}

	public void multiByMatrix(float[][] matrix) {
		float x1, y1, z1, w;
		x1 = x * matrix[0][0] + y * matrix[1][0] + z * matrix[2][0]
				+ matrix[3][0];
		y1 = x * matrix[0][1] + y * matrix[1][1] + z * matrix[2][1]
				+ matrix[3][1];
		z1 = x * matrix[0][2] + y * matrix[1][2] + z * matrix[2][2]
				+ matrix[3][2];
		w = x * matrix[0][3] + y * matrix[1][3] + z * matrix[2][3]
				+ matrix[3][3];
		this.x = x1 / w;
		this.y = y1 / w;
		this.z = z1 / w;
	}

}
