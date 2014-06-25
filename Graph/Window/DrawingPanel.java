package Window;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.Math;
import javax.swing.JPanel;

import Graph.Graph; 

public class DrawingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final int VERTEX_RADIUS = 15;
	private final int CIRCLE_RADIUS = 240;
	private final int START_X = 300;
	private final int START_Y = 270;
	//������� ����
	private Graph testGraph = null;	//������� ��� ���������
	public class DrawableVertex {
		int x;
		int y;
		String name;

		public DrawableVertex(int x, int y, String name) {
			this.x = x;
			this.y = y;
			this.name = name;
		}
	}
	//����� ��� ���������
	public class DrawableEdge {
		int from;
		int to;
		int weight;
		Color color;
		public DrawableEdge(int from, int to, int weight, Color color) {
			this.from = from;
			this.to = to;
			this.weight = weight;
			this.color = color;
		}
	}
	//�������� ����
	private DrawableVertex[] vertex;
	private DrawableEdge[] edge;	
	private boolean drawingWeight = false;	//�������� �� ����
	private int draggedIndex = -1;	//������� ������������ �����
	private boolean isDragging = false;	//�������������� �� �������������� �������
	//����������� ����������� ������
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (testGraph != null && vertex != null && vertex.length > 0) {
			for (int i = 0; i < edge.length; i++) {
				this.drawEdge(i, g);
			}
			for (int i = 0; i < vertex.length; i++) {
				this.drawVertex(i, g);
			}
		}
	}
	
	//���������� ����
	public String setGraph(int vertexCount, int edgeCount, int minRand, int maxRand) {
		this.clearData();
		this.repaint();
		String genOrder = "";
		if (0 < edgeCount && edgeCount <= vertexCount * (vertexCount - 1) / 2 
				&& minRand > 0 
				&& vertexCount > 0) {
			testGraph = new Graph(vertexCount, edgeCount, minRand, maxRand);
			Graph.Vertex currentVertex;
			Graph.Vertex[] tempVert = testGraph.getVertex();
			vertex = new DrawableVertex[tempVert.length]; 
			float dAlpha = (float) (2 * Math.PI / vertex.length);
			float alpha = 0f;
			int x;
			int y;
			for (int i = 0; i < tempVert.length; i++) {
				currentVertex = tempVert[i];
				x = (int) (START_X + Math.sin(alpha)*CIRCLE_RADIUS);
				y = (int) (START_Y + Math.cos(alpha)*CIRCLE_RADIUS);
				vertex[i] = new DrawableVertex(x, y, currentVertex.name);
				alpha += dAlpha;
			}
			Graph.Edge currentEdge;
			Graph.Edge[] tempEdge = testGraph.getEdge();
			edge = new DrawableEdge[tempEdge.length];
			for (int i = 0; i < tempEdge.length; i++) {
				currentEdge = tempEdge[i];
				edge[i] = new DrawableEdge(currentEdge.from, 
						currentEdge.to, 
						currentEdge.weight,
						Color.BLACK);
			}	
			this.repaint();
			genOrder = testGraph.getGenOrder();
		} else {
			genOrder += ("------------------------------------------------\n"
					+ "Generation error!\n"
					+ "Check edges count (0 < count <= n(n - 1)/2)\n,"
					+ "vertex count > 0"
					+ "and minimal weight > 0\n");
		}
		return genOrder;
	}
	//���� ���
	public String findMST() {
		String mstOrder = "";
		//���� ���� �� ��������
		if (testGraph == null) {
			return "\nGraph is not loadded!\n";
		}
		//����� ���� ���
		if (testGraph.buildMST()) {
			//���� �������
			mstOrder += (testGraph.getMSTOrder() + 
					"Solution is find!\n");
			//������ ����� �� ��� ��������
			Graph.Edge[] checkMST = testGraph.getMST();
			for (int i = 0; i < edge.length; i++) {
				for (int j = 0; j < checkMST.length; j++) {
					if (edge[i].from == checkMST[j].from &&
							edge[i].to == checkMST[j].to) {
						edge[i].color = Color.RED;
					}
				}
			}
			this.printComponent(this.getGraphics());
		} else {
			//����� �������, ��� ���� �� ������
			mstOrder += (testGraph.getMSTOrder() + 
					"Graph have more then 1 connected components and haven't solution!\n");
		};
		return mstOrder;
	}
	//��������� �������
	private void drawVertex(int i, Graphics g) {
		g.setColor(Color.WHITE);
		fillCircle(vertex[i].x, vertex[i].y, VERTEX_RADIUS, g);
		g.setColor(Color.BLACK);
		drawCircle(vertex[i].x, vertex[i].y, VERTEX_RADIUS, g);
		g.drawString(vertex[i].name, 
				vertex[i].x - 5, 
				vertex[i].y + 5);
	}
	//��������� �����
	private void drawEdge(int i, Graphics g) {
		int fromX = vertex[edge[i].from].x;
		int fromY = vertex[edge[i].from].y;
		int toX = vertex[edge[i].to].x;
		int toY = vertex[edge[i].to].y;
		g.setColor(edge[i].color);
		g.drawLine(fromX, fromY, toX, toY);
		if (drawingWeight) {
			int middleX = (fromX + toX) / 2;
			int middleY = (fromY + toY) / 2;
			g.drawString(Integer.toString(edge[i].weight), 
					middleX, middleY);
		}
		
	}
	//����������� ������� ������������� �������
	public boolean isSelecting(int x, int y) {
		boolean isSelecting = false;
		for (int i = 0; i < vertex.length; i++) {
			if ( Math.pow(x - vertex[i].x, 2) + Math.pow(y - vertex[i].y, 2) < Math.pow(VERTEX_RADIUS, 2)) {
				draggedIndex = i;
				isSelecting = true;
				break;
			}
		}
		return isSelecting;
	}
	//�������� ���������� ������������� �������
	public void setCoordinates(int x, int y) {
		vertex[draggedIndex].x = x;
		vertex[draggedIndex].y = y;
		this.repaint();
	}
	//������������ ������� ��������
	public void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}
	public boolean isDragging() {
		return isDragging;
	}
	//��������� ����������
	private void drawCircle(int x, int y, int r, Graphics g) {
		g.drawOval(x - r, y - r, r * 2, r * 2);
	}
	//��������� �����
	private void fillCircle(int x, int y, int r, Graphics g) {
		g.fillOval(x - r, y - r, r * 2, r * 2);
	}
	//������ �� ������� ����� ����� � �������
	public void setDrawingWeights(boolean in) {
		drawingWeight = in;
		this.repaint();
	}
	//������� ������
	private void clearData() {
		vertex = null;
		edge = null;
		draggedIndex = -1;
		testGraph = null;
	}
	
}
