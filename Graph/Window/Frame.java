package Window;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Rectangle;
import java.awt.Color;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField vertexCount;
	private JTextField edgeCount;
	private JTextField minRand;
	private JTextField maxRand;
	private DrawingPanel graphPanel;
	private JTextArea log;
	private JButton btnClrLog;
	private String genOrder;
	private String mstOrder;
	private JCheckBox showWeights;

	public Frame() {
		setTitle("Course Work");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1051, 589);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JButton btnRand = new JButton("Rand");
		btnRand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				genOrder = graphPanel.setGraph(Integer.parseInt(vertexCount.getText()), 
						Integer.parseInt(edgeCount.getText()),
						Integer.parseInt(minRand.getText()), 
						Integer.parseInt(maxRand.getText()));
				log.append(genOrder);
			}
		});
		btnRand.setBounds(958, 427, 75, 23);
		contentPane.add(btnRand);
		
		vertexCount = new JTextField();
		vertexCount.setText("5");
		vertexCount.setBounds(758, 442, 86, 20);
		contentPane.add(vertexCount);
		vertexCount.setColumns(10);
		
		edgeCount = new JTextField();
		edgeCount.setText("7");
		edgeCount.setBounds(854, 442, 86, 20);
		contentPane.add(edgeCount);
		edgeCount.setColumns(10);
		
		JLabel lblVertexCount = new JLabel("Vertexes count:");
		lblVertexCount.setBounds(758, 417, 86, 14);
		contentPane.add(lblVertexCount);
		
		JLabel lblEdgeCount = new JLabel("Edges count:");
		lblEdgeCount.setBounds(854, 417, 86, 14);
		contentPane.add(lblEdgeCount);
		
		graphPanel = new DrawingPanel();
		graphPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (graphPanel.isDragging()) {
					graphPanel.setCoordinates(e.getX(), e.getY());
				}
			}
		});
		graphPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				graphPanel.setDragging(false);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				//Если попали в вершину, то двигаем
				if (graphPanel.isSelecting(e.getX(), e.getY())) {
					graphPanel.setDragging(true);
				}
			}
		});
		graphPanel.setBounds(10, 11, 738, 541);
		//contentPane.add(graphPanel);
		JScrollPane panelScroll = new JScrollPane(graphPanel);
		panelScroll.setBounds(graphPanel.getBounds());
		contentPane.add(panelScroll);
		
		log = new JTextArea();
		log.setEditable(false);
		log.setLineWrap(true);
		log.setBounds(758, 11, 257, 395);
		
		JScrollPane textScroll = new JScrollPane(log);
		textScroll.setBounds(new Rectangle(758, 11, 275, 395));
		contentPane.add(textScroll);
		
		JButton btnNewButton = new JButton("MST");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mstOrder = graphPanel.findMST();
				log.append(mstOrder);
			}
		});
		btnNewButton.setBounds(958, 461, 75, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblMinWeight = new JLabel("min Weight >0:");
		lblMinWeight.setBounds(758, 470, 86, 14);
		contentPane.add(lblMinWeight);
		
		JLabel lblMaxWeight = new JLabel("max Weight");
		lblMaxWeight.setBounds(854, 473, 86, 14);
		contentPane.add(lblMaxWeight);
		
		minRand = new JTextField();
		minRand.setText("1");
		minRand.setBounds(758, 495, 86, 20);
		contentPane.add(minRand);
		minRand.setColumns(10);
		
		maxRand = new JTextField();
		maxRand.setText("10");
		maxRand.setBounds(854, 495, 86, 20);
		contentPane.add(maxRand);
		maxRand.setColumns(10);
		
		btnClrLog = new JButton("Clr log");
		btnClrLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.setText("");
			}
		});
		btnClrLog.setBounds(958, 494, 75, 23);
		contentPane.add(btnClrLog);
		
		showWeights = new JCheckBox("Show weights");
		showWeights.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				graphPanel.setDrawingWeights(showWeights.isSelected());
			}
		});
		
		
		showWeights.setBounds(758, 522, 106, 23);
		contentPane.add(showWeights);
		
		JLabel lblAutorEfremovMa = new JLabel("Author: Efremov M.A #2304 LETI 2014");
		lblAutorEfremovMa.setForeground(Color.LIGHT_GRAY);
		lblAutorEfremovMa.setBounds(824, 547, 221, 14);
		contentPane.add(lblAutorEfremovMa);
	}
}
