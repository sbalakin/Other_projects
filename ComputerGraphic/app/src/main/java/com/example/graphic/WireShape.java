package com.example.graphic;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

public class WireShape {
	private final List<Edge> listEdges;
	private final List<Vertex> listVertices;

	public WireShape() {
		listEdges = new ArrayList<Edge>();
		listVertices = new ArrayList<Vertex>();
	}

	public void DrawShape(Paint paint, Canvas canvas, Vertex startPoint) {

		for (Edge edge : listEdges) {
			edge.Draw(canvas, paint, startPoint);
		}
	}

	public void doMultibyMatrix(float[][] matrix) {
		for (Vertex vertex : listVertices) {
			vertex.multiByMatrix(matrix);
		}
	}

	public WireShape(WireShape shapeToCopy) {
		listEdges = new ArrayList<Edge>();
		listVertices = new ArrayList<Vertex>();
		for (Edge edge : shapeToCopy.getEdges()) {
			this.addEdge(new Edge(edge));
		}
	}

	public List<Edge> getEdges() {
		return listEdges;
	}

	public void addEdge(Edge edge) {
		this.listEdges.add(edge);
		listVertices.add(edge.startVertex);
		listVertices.add(edge.finishVertex);
	}
}
