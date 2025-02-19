import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class KMeans_NearestNeighbor extends JFrame implements MouseListener,
		MouseMotionListener {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new KMeans_NearestNeighbor();
	}
	protected int windowXLength = 800;
	protected int windowYLength = 600;
	
	protected Cluster[] clusters;
	protected ArrayList <Point2D>  points= new ArrayList<Point2D>();
	protected ArrayList <Point2D>  tempPoints= new ArrayList<Point2D>();
	protected ArrayList <Point2D>  lines= new ArrayList<Point2D>();
	protected Point2D currentPoint;
	
	protected JPanel panel;
	protected JButton addButton;
	protected JButton moveButton;
	protected JButton removeButton;
	protected JButton randomPointsButton;
	protected JButton kMeansButton;
	protected JButton kMeansNextButton;
	protected JButton nearestNeighborButton;
	protected JButton nearestNeighborNextButton;
	protected JButton clearButton;
	protected JTextField randomText;
	protected JTextField kCenter;
	protected JLabel randomPointsLabel;
	protected JLabel numberOfClustersLabel;
	protected JLabel space;
	
	protected int pointMode = 1;

	public KMeans_NearestNeighbor() {
		super("K-Means & Nearest Neighbor");

		panel =  new JPanel();
		
		currentPoint = null;
		
		addButton = new JButton("Add");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				pointMode = 1;
			}
		});

		moveButton = new JButton("Move");
		moveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				pointMode = 3;
			}
		});
		
		removeButton = new JButton("Remove");
		removeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				pointMode = 0;
			}
		});
		
		randomPointsLabel = new JLabel("      Number of Points:  ");
		randomText = new JTextField("25");
		
		randomPointsButton = new JButton("Random Points");
		randomPointsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				Point randomPoint;
				Random randomGenerator = new Random();
				int randomX;
				int randomY;
				int randomPointsNumber = Integer.parseInt(randomText.getText());
				
				for (int i = 0 ; i<randomPointsNumber; i++){
					randomPoint = new Point();
					randomX = randomGenerator.nextInt(windowXLength-1);
					randomY = randomGenerator.nextInt(windowYLength-100);
					randomPoint.setLocation(randomX, randomY+100);
					points.add(randomPoint);
				}
				repaint();
			}
		});
		
		numberOfClustersLabel = new JLabel("Number of Clusters:");
		kCenter = new JTextField("3");
		
		kMeansButton = new JButton("K-Means");
		kMeansButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				
				addButton.setEnabled(false);
				removeButton.setEnabled(false);
				kMeansNextButton.setEnabled(true);
				
				Random randomGenerator = new Random();
				Point randomPoint;
				int randomX;
				int randomY;
				
				int kCenterPoints = Integer.parseInt(kCenter.getText());
				clusters = new Cluster[kCenterPoints];
				
				for (int i = 0 ; i<kCenterPoints; i++){
					randomPoint = new Point();
					randomX = randomGenerator.nextInt(windowXLength-1);
					randomY = randomGenerator.nextInt(windowYLength-100);
					randomPoint.setLocation(randomX, randomY+100);
					clusters[i] = new Cluster(randomPoint, randomColor());		
				}
				
			createCluster();
			
			repaint();
			}

			private void createCluster() {
				double distanceFromCenter = windowXLength+windowYLength;
				double pointDistance;
				int whichCluster=0;
				
				int numberOfPoints = points.size();
				
				for(int i = 0; i<numberOfPoints; i++){
					distanceFromCenter = windowXLength+windowYLength;
					for(int j = 0 ;j<clusters.length; j++){
						pointDistance = Point2D.distance(points.get(i).getX(), points.get(i).getY(), clusters[j].center.getX(), clusters[j].center.getY());
						if(pointDistance < distanceFromCenter){
							distanceFromCenter = pointDistance;
							whichCluster = j;
						}
					}
					clusters[whichCluster].clusterPoints.add(points.get(i));
					
				}
				points.clear();
			}
		});
		
		kMeansNextButton = new JButton("Next");
		kMeansNextButton.setEnabled(false);
		kMeansNextButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				int totalX = 0, totalY=0, arraySize=0, changed = 3;
				
				for(int i = 0; i <clusters.length; i++){
					if(clusters[i].clusterPoints.size() != 0){
						totalX = 0;
						totalY = 0;
						arraySize = clusters[i].clusterPoints.size();

						for(int j=0; j<arraySize; j++ ){
							totalX += clusters[i].clusterPoints.get(j).getX();
							totalY += clusters[i].clusterPoints.get(j).getY();
						}
						totalX = totalX/arraySize;
						totalY = totalY/arraySize;
						if(totalX == clusters[i].center.getX() && totalY == clusters[i].center.getY()){
							changed -= 1;
						}
						else
							clusters[i].center.setLocation(totalX, totalY);
						
						for(int k=0; k<arraySize; k++){
							points.add(clusters[i].clusterPoints.get(k));
						}
						clusters[i].clusterPoints.clear();
					}
					else
						changed -= 1;	
				}
				if(changed == 0){
					kMeansNextButton.setEnabled(false);
					points.clear();	
				}
				else{
					createCluster();
					repaint();
				}		
			}
			
			private void createCluster() {
				double distanceFromCenter = windowXLength+windowYLength;
				double pointDistance;
				int whichCluster=0;
				
				int numberOfPoints = points.size();

				for(int i = 0; i<numberOfPoints; i++){
					distanceFromCenter = windowXLength+windowYLength;
					for(int j = 0 ;j<clusters.length; j++){
						pointDistance = Point2D.distance(points.get(i).getX(), points.get(i).getY(), clusters[j].center.getX(), clusters[j].center.getY());
						if(pointDistance < distanceFromCenter){
							distanceFromCenter = pointDistance;
							whichCluster = j;
						}
					}
					clusters[whichCluster].clusterPoints.add(points.get(i));	
				}
				points.clear();
			}
		});
		
		nearestNeighborButton = new JButton("Nearest Neighbor");
		nearestNeighborButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				
				Point2D startPoint = null;
				int  nextPoint = 0;
				pointMode = 2;
				double minimumDistance = windowXLength+windowYLength;
				double pointDistance;
				
				if(currentPoint!=null){
					
					for(int l=0; l<points.size(); l++){
						tempPoints.add(points.get(l));
					}
					for (int k = 0; k< points.size();k++) {
						if (tempPoints.get(k).equals(currentPoint))
						{
							startPoint = tempPoints.get(k);
							lines.add(startPoint);
							tempPoints.remove(k);
							break;
						}
					}
				}
				
				if(startPoint !=null){
					minimumDistance = windowXLength+windowYLength;
					for (int i = 0; i < tempPoints.size(); i++) {
						pointDistance = Point2D.distance(tempPoints.get(i).getX(), tempPoints.get(i).getY(), startPoint.getX(), startPoint.getY());
						if (pointDistance != 0 && pointDistance < minimumDistance ){
							minimumDistance = pointDistance;
							nextPoint = i;
						}
					}
					
					lines.add(tempPoints.remove(nextPoint));
					nearestNeighborNextButton.setEnabled(true);
				}
				
				repaint();
			}
		});
		
		nearestNeighborNextButton = new JButton("Next");
		nearestNeighborNextButton.setEnabled(false);
		nearestNeighborNextButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				
				Point2D startPoint = lines.get(lines.size()-1);
				int nextPoint = 0;
				
				double minimumDistance = windowXLength+windowYLength;
				double pointDistance;
				
				if(startPoint !=null){
					minimumDistance = windowXLength+windowYLength;
					for (int i = 0; i < tempPoints.size(); i++) {
						pointDistance = Point2D.distance(tempPoints.get(i).getX(), tempPoints.get(i).getY(), startPoint.getX(), startPoint.getY());
						if (pointDistance != 0 && pointDistance < minimumDistance ){
							minimumDistance = pointDistance;
							nextPoint = i;
						}
					}
					lines.add(tempPoints.remove(nextPoint));
					
					if(lines.size() == points.size())
						nearestNeighborNextButton.setEnabled(false);
				}
				
				repaint();
			}
		});
		
		clearButton = new JButton("Clear All");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent eve) {
				
				points.clear();
				lines.clear();
				currentPoint = null;
				
				if(clusters!=null){
					for(int i = 0; i<clusters.length; i++){
						clusters[i].clusterPoints.clear();
						clusters[i].center = null;
					}
				}
				
				repaint();
			}
		});
		
		space = new JLabel("    |    ");
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		panel.add(addButton);
		panel.add(moveButton);
		panel.add(removeButton);
		panel.add(clearButton);
		panel.add(randomPointsLabel);
		panel.add(randomText);
		panel.add(randomPointsButton);
		panel.add(numberOfClustersLabel);
		panel.add(kCenter);
		panel.add(kMeansButton);
		panel.add(kMeansNextButton);
		panel.add(space);
		panel.add(nearestNeighborButton);
		panel.add(nearestNeighborNextButton);
		
		add(panel);
		
		setSize(windowXLength, windowYLength);
		setVisible(true);
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		super.paint(g2);

		if(lines.size()>1){
			for(int m=0; m<lines.size()-1; m++){
				g2.setPaint(randomColor());
				Line2D line = new Line2D.Double(lines.get(m), lines.get(m+1));
				g2.draw(line);
			}
		}
		
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i) == currentPoint)
				g2.setPaint(Color.red);
			else
				g2.setPaint(Color.blue);
			g2.fill(getControlPoint(points.get(i),4));
		}
		
		if(clusters!=null){
			for(int j = 0; j< clusters.length; j++){	
				g2.setPaint(clusters[j].getColor());
				if(clusters[j].center != null)
					g2.fill(getControlPoint(clusters[j].getCenter(),8));
				for(int k=0; k<clusters[j].clusterPoints.size(); k++){
					g2.fill(getControlPoint(clusters[j].clusterPoints.get(k),4));
				}
			}
		}
	}
	
	protected Color randomColor (){
		int red=(int)(Math.random()*255);
        int green=(int)(Math.random()*255);
        int blue=(int)(Math.random()*255);
		Color randomColor = new Color(red, green, blue);
		
		return randomColor;
	}
	
	protected Shape getControlPoint(Point2D p, int size) {
		int kenar = size;
		return new Rectangle2D.Double(p.getX() - kenar / 2, p.getY() - kenar
				/ 2, kenar, kenar);
	}

	public void mouseClicked(MouseEvent me) {
		if(pointMode == 1)
			points.add(me.getPoint());
		else if(pointMode == 0)
		{
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).equals(currentPoint))
				{
					points.remove(i);
					break;
				}
			}
		}
		else if(pointMode == 2){
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).equals(currentPoint))
				{
					currentPoint.setLocation(me.getPoint());
					break;
				}
			}
		}	
		repaint();
	}

	public void mousePressed(MouseEvent me) {
		currentPoint = null;
		for (int i = 0; i < points.size(); ++i) {
			Shape s = getControlPoint(points.get(i),4);
			if (s.contains(me.getPoint())) {
				currentPoint = points.get(i);
				break;
			}
		}
		repaint();
	}

	public void mouseReleased(MouseEvent me) {
	}

	public void mouseMoved(MouseEvent me) {
	}

	public void mouseDragged(MouseEvent me) {
		if (pointMode == 3 &&currentPoint != null) {
			currentPoint.setLocation(me.getPoint());
			repaint();
		}
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public class Cluster{
		protected Point2D center;
		protected Color color;
		protected ArrayList <Point2D>  clusterPoints = new ArrayList<Point2D>();
		
		public Cluster(Point2D center, Color color){
			this.center = center;
			this.color = color;
		}
		
		public Point2D getCenter(){
			return this.center;
		}
		
		public Color getColor(){
			return this.color;
		}
		
		public ArrayList <Point2D> getClusterPoints(){
			return this.clusterPoints;
		}	
	}	
}