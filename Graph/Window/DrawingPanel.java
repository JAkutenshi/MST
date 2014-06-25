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
	//рабочий граф
	private Graph testGraph = null;	//вершины для отрисовки
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
	//ребра для отрисовки
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
	//рисуемый граф
	private DrawableVertex[] vertex;
	private DrawableEdge[] edge;	
	private boolean drawingWeight = false;	//рисовать ли веса
	private int draggedIndex = -1;	//индеекс перемещаемой точки
	private boolean isDragging = false;	//осуществляется ли перетаскивание вершины
	//перегружаем перерисовку панели
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
	
	//генерируем граф
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
	//ищем МОД
	public String findMST() {
		String mstOrder = "";
		//Если граф не загружен
		if (testGraph == null) {
			return "\nGraph is not loadded!\n";
		}
		//иначе ищем МОД
		if (testGraph.buildMST()) {
			//Если нашлось
			mstOrder += (testGraph.getMSTOrder() + 
					"Solution is find!\n");
			//делаем ребра из МОД красными
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
			//иначе выводим, что граф не связен
			mstOrder += (testGraph.getMSTOrder() + 
					"Graph have more then 1 connected components and haven't solution!\n");
		};
		return mstOrder;
	}
	//отрисовка вершины
	private void drawVertex(int i, Graphics g) {
		g.setColor(Color.WHITE);
		fillCircle(vertex[i].x, vertex[i].y, VERTEX_RADIUS, g);
		g.setColor(Color.BLACK);
		drawCircle(vertex[i].x, vertex[i].y, VERTEX_RADIUS, g);
		g.drawString(vertex[i].name, 
				vertex[i].x - 5, 
				vertex[i].y + 5);
	}
	//отрисовка ребра
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
	//Определение индекса передвигаемой вершины
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
	//передает координаты передвигаемой вершине
	public void setCoordinates(int x, int y) {
		vertex[draggedIndex].x = x;
		vertex[draggedIndex].y = y;
		this.repaint();
	}
	//передвижение вершины окончено
	public void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}
	public boolean isDragging() {
		return isDragging;
	}
	//отрисовка окружности
	private void drawCircle(int x, int y, int r, Graphics g) {
		g.drawOval(x - r, y - r, r * 2, r * 2);
	}
	//отрисовка круга
	private void fillCircle(int x, int y, int r, Graphics g) {
		g.fillOval(x - r, y - r, r * 2, r * 2);
	}
	//делать ли подписи весов рядом с ребрами
	public void setDrawingWeights(boolean in) {
		drawingWeight = in;
		this.repaint();
	}
	//очистка данных
	private void clearData() {
		vertex = null;
		edge = null;
		draggedIndex = -1;
		testGraph = null;
	}
	
}
