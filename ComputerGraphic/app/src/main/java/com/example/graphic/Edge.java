package com.example.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Edge {

	public Vertex startVertex;
	public Vertex finishVertex;

	public Edge(Vertex startVertex, Vertex finishVertex) {
		this.startVertex = startVertex;
		this.finishVertex = finishVertex;
	}

	public Edge(Edge edgeToCopy) {
		this.startVertex = new Vertex(edgeToCopy.startVertex);
		this.finishVertex = new Vertex(edgeToCopy.finishVertex);
	}

	public void Draw(Canvas canvas, Paint paint, Vertex startPoint) {
		canvas.drawLine((int) (startVertex.x + startPoint.x),
				(int) (startVertex.y + startPoint.y),
				(int) (finishVertex.x + startPoint.x),
				(int) (finishVertex.y + startPoint.y), paint);
	}
}

