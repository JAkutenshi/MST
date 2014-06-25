package Graph;

import java.util.ArrayList;
import java.util.Random;
/**
 * 
 * @author Akutenshi
 * Неориентрованный, связный, взвешенный граф
 *
 */
public class Graph {
	//Вершина
	public final class Vertex {
		public String name;
		public int numComponent;
		
		public Vertex() {
			name = "default";
			numComponent = 0;
		}
		
		public Vertex(String name, int numComponent) {
			setVertex(name, numComponent);
		}
		
		public void setVertex(String name, int numComponent) {
			this.name = name;
			this.numComponent = numComponent;
		}
	}
	//Ребро
	public final class Edge {
		public int from;
		public int to;
		public int weight;
		
		public Edge() {
			from = 0;
			to = 0;
			weight = 0;
		}
		
		public Edge(int from, int to, int weigth) {
			setEdge(from, to, weigth);
		}
		
		public void setEdge(int from, int to, int weigth) {
			this.from = from;
			this.to = to;
			this.weight = weigth;
		}
	
		public void swap() {
			int buffer = to;
			to = from;
			from = buffer;
		}
	}
	//Компонента связности
	public final class Component {
		public int number;
		public ArrayList<Vertex> vertex;
		public Edge minEdge;
		
		public Component() {
			number = 0;
			vertex = new ArrayList<Vertex>(0);
		}
		
		public Component(int number, ArrayList<Vertex> vertex) {
			this.setComponent(number, vertex);
		}
		
		public void setComponent(int number, ArrayList<Vertex> vertex) {
			this.number = number;
			this.vertex = vertex;
		}
		
		public void addVertex(Vertex vertex) {
			this.vertex.add(vertex);
		}
	
		public void union(Component in) {
			//Все вершины присоединяемого множества помечаем членами этого
			// и добавляем в конец списка вершин этого компонента
			for (int i = 0; i < in.vertex.size(); i++) {
				in.vertex.get(i).numComponent = this.number;
				this.vertex.add(in.vertex.get(i));
			}
		}
	}
	//Граф
	private Vertex[] vertex = null;
	private Edge[] edge = null;
	//Генератор случайных чисел для конструктора
	private Random rand = new Random();
	//Контейнеры для алгоритма Боровки:
	// 1) Компоненты связности
	private ArrayList<Component> component = null;
	// 2) МОД
	private ArrayList<Edge> minSpanningTree = new ArrayList<Edge>(0);
	//отчеты:
	// 1) генерации графа
	private String genOrder = "------------------------------------------------\n";
	// 2) нахождения МОД
	private String mstOrder = "\nFinding MST:\n";
	/**
	 * Конструктор с рандомизированием графа по заданным параметрам:
	 * 1) Количество вершин
	 * 2) Количество ребер
	 * Вес ребер и их инцедентность определяется случайно. Первое в 
	 * интервале [minRand, maxRand], второе - любая вариация ребер, в количестве 
	 * не менее 0 и не более кол-ва ребер полного графа
	 */	
	public Graph(int vertexCount, int edgeCount, int minRand, int maxRand) {
		//проверка, что ребер <= чем в полном графе и вес положительный
		int maxEdge = vertexCount * (vertexCount - 1) / 2;
		if (0 < edgeCount && edgeCount <= maxEdge && minRand > 0 && vertexCount > 0) {
			vertex = new Vertex[vertexCount];
			edge = new Edge[edgeCount];

			//заполнение вершин
			for (int i = 0; i < vertex.length; i++) {
				vertex[i] = new Vertex(Integer.toString(i), i);
			}
			//заполнение ребер
			//Делаем до тех пор, пока граф не связен
			//строим ребра для полного графа
			ArrayList<Edge> full = new ArrayList<Edge>(0);
			int f = 0;	//from
			while (f != vertex.length - 1) {
				//j - to
				for (int j = f + 1; j <= vertex.length - 1; j++) {
					full.add(new Edge(f, j, rand.nextInt(maxRand - minRand) + minRand));
				}
				f++;
			}
			for (int i = 1; i <= maxEdge - edgeCount; i++) {
				full.remove(rand.nextInt(full.size()));
			}
			for (int i = 0; i < edge.length; i++) {
				edge[i] = full.get(i);
			}
			full.clear();
			//пишем отчет
			writeGenOrder();
		}
	}
	//Алгоритм Боровки по поиску МОД
	// Ответ хранится в списке minSpanningTree	
	public boolean buildMST() {
		//Если решение уже найдено - выходим
		if (minSpanningTree.size() != 0) {
			return true;
		}
		//Инициируем пустое МОД
		minSpanningTree.clear();
		//Инициализируем список компонент связности - каждая вершина
		// есть отдельная компонента связности
		component = new ArrayList<Component>(0);
		ArrayList<Vertex> singleComp;
		for (int i = 0; i < vertex.length; i++) {
			singleComp = new ArrayList<Vertex>(0);
			singleComp.add(vertex[i]);
			component.add(new Component(i, singleComp));
		}
		
		Component attach;									//объединяющий
		int attachIndex;
		Component attachable;								//включаемое
		int attachableIndex;
		Component current;									//Текущий компонент
		int[] minCompWeight = new int[component.size()];	//минимальные веса		
		Edge[] minEdge; 									//Массив минимальных ребер
		int step = 1;										//Номер шага главного цикла
		//Главный цикл
		while (component.size() != 1) {
			//минимумы для компонент ставим максимально большими
			for (int i = 0; i< component.size(); i++) {
				minCompWeight[i] = Integer.MAX_VALUE;
			}
			//чистим массив минимальных ребер
			minEdge = new Edge[component.size()];
			
			//Перебираем ребра и проверяем на вхождение их в компоненты
			for (int i = 0; i < edge.length; i++) {
				for (int j = 0; j < component.size(); j++) {
					current = component.get(j);
					//Если ребро лежит одной вершиной в j-й компоненте, то сравниваем с его минимумом
					if (isEdgeEndInComp(edge[i], current)) {
						if (edge[i].weight < minCompWeight[j]) {
							minCompWeight[j] = edge[i].weight;
							minEdge[j] = edge[i];
						}
					}
				}
			}
			//записываем компоненты связности в отчет
			writeCompsToOrder(step);
			//Объединяем множества	
			for (int i = 0; i < minEdge.length; i++) {
				if (minEdge[i] == null) {
					mstOrder += (" Component #" + i + " is isolated!\n");
					return false;
				}
				if (!inComp(minEdge[i])) {
					minSpanningTree.add(minEdge[i]);
					attachIndex = findCompIndex(vertex[minEdge[i].from].numComponent);
					attach = component.get(attachIndex);
					attachableIndex = findCompIndex(vertex[minEdge[i].to].numComponent);
					attachable = component.get(attachableIndex);
					
					//пишем объединение в лог
					mstOrder += (" " + attach.number + " with " + attachable.number);
					mstOrder += (" by edge " + vertex[minEdge[i].from].name + 
							"-" + vertex[minEdge[i].to].name +
							" weight = " + minEdge[i].weight + "\n");
				
					attach.union(attachable);
					component.remove(attachableIndex);
				}
			}
			//переходим к следующему шагу
			step++;
		}
		
		
		mstOrder += ("MST:\n");
		for (int i = 0; i < minSpanningTree.size(); i++) {
			mstOrder += (minSpanningTree.get(i).from + "-" +
					minSpanningTree.get(i).to + " weight = " + minSpanningTree.get(i).weight + "\n");
		}
		
		return true;
	}
	//Лежит ли ребро е в одной комопненте связности
	private boolean inComp(Edge e) {
		return (vertex[e.from].numComponent == vertex[e.to].numComponent);
	}
	//is the Edge's end in Component
	private boolean isEdgeEndInComp(Edge e, Component c) {
		if (!inComp(e)
			&& (vertex[e.from].numComponent == c.number 
			|| vertex[e.to].numComponent == c.number)) {
			return true;
		} else {
			return false;
		}
	}
	//Найти индекс в списке компонент связности с данной меткой num
	private int findCompIndex(int num) {
		for (int i = 0; i < component.size(); i++) {
			if (component.get(i).number == num) {
				return i;
			}
		}
		return -1;
	}
	
	//Заносит в отчет поиска МОД текущие компоненты связности
	private void writeCompsToOrder(int step) {
		mstOrder += ("Step #" + step + ":\n");
		mstOrder += ("Count of connected sets = " + component.size() + "\n");
		Component current;
		for (int i = 0; i < component.size(); i++) {
			current = component.get(i);
			mstOrder += (" " + i + ") Mark of component: " + current.number + " | Vertex:");
			for (int j = 0; j < current.vertex.size(); j++) {
				mstOrder += (" " + current.vertex.get(j).name);
			}
			mstOrder += (";\n");
		}
		mstOrder += ("Unions:\n");
	}
	
	//Пишет отчет генерации графа
	private void writeGenOrder() {
		//информация о вершинах
		genOrder += ("New graph generated!\n");
		genOrder += ("Vertexes count = " + vertex.length + "\n");
		genOrder += ("Vertexes: ");
		for (int i = 0; i < vertex.length; i++) {
			genOrder += (vertex[i].name + ", ");
		}
		//информация о ребрах
		genOrder += ("\n Edges count = " + edge.length + "\n");
		genOrder += ("Edges:\n");
		for (int i = 0; i < edge.length; i++) {
			genOrder += (vertex[edge[i].from].name + "-" +
					vertex[edge[i].to].name + " weight = " + edge[i].weight + "\n");
		}
		
	}
	
	//Возвращает отчет поиска МОД
	public String getMSTOrder() {
		return mstOrder;
	}
	
	//Возвращает отчет генерации
	public String getGenOrder() {
		return genOrder;
	}
	
	//Возвращает вершины
	public Vertex[] getVertex() {
		return vertex;
	}
	
	//Возвращает ребро
	public Edge[] getEdge() {
		return edge;
	}
	
	//Возвращает МОД
	public Edge[] getMST() {
		Edge[] mst = new Edge[minSpanningTree.size()];
		for (int i = 0; i < minSpanningTree.size(); i++) {
			mst[i] = minSpanningTree.get(i);
		}
		return mst;
	}
}
