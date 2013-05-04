

import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SourceStrategy {

	public static int RANDOM = 0;
	public static int MAXDEGREE = 1;
	public static int MAXBC = 2;
	public static int MAXCORENESS = 3;
	
	public Graph graph;
	public int strategyKind;
	public double percent;
	
	public SourceStrategy(int strategyKind, double percent){
		this.strategyKind = strategyKind;
		this.percent = percent;
	}
	
	public SourceStrategy(Graph graph, int strategyKind, double percent){
		this.graph = graph;
		this.strategyKind = strategyKind;
		this.percent = percent;
	}
	
    public List<Node> sourceNodes(Graph graph, int strategyKind, double percent){
		
		if(strategyKind == RANDOM){
			return getSelectedNodesByRandom(graph, percent);
		}else if(strategyKind == MAXDEGREE){
			return getSelectedNodesByDegree(graph, percent);
		}else if(strategyKind == MAXBC){
			return getSelectedNodesByBC(graph, percent);
		}else if(strategyKind == MAXCORENESS){
			return getSelectedNodesByCoreness(graph, percent);
		}
		return null;
	}
    
    public List<Node> getSourceNodes(){
    	return sourceNodes(graph, strategyKind, percent);
    }
    
    public List<Node> getSelectedNodesByRandom(Graph graph, double percent){
		List<Node> selected = new ArrayList<Node>();
		List<Node> candidate = new ArrayList<Node>();
		for(Node n: graph.getNodes()){
			candidate.add(n);
		}
		
		int immunityNum = (int)(graph.getGraphSize()*percent);
		
		for(int i = 0; i < immunityNum; i++){
			int index = (int)(candidate.size()*Math.random());
			selected.add(candidate.get(index));
			candidate.remove(index);
		}
		candidate = null;
		return selected;
	}

    public List<Node> getSelectedNodesByBC(Graph graph, double percent){
		List<Node> selected = new ArrayList<Node>();
		
		Element[] elements = new Element[graph.getGraphSize()];
		int vn = graph.getGraphSize();
		for(int i = 0; i < vn; i++)
		{
			elements[i] = new Element(i,graph.getNodes().get(i).getBC());
		}
		Arrays.sort(elements);
		
		int immunityNum = (int)(graph.getGraphSize()*percent);
		
		for(int i = 0; i < immunityNum; i++){
			int index = elements[i].index;
			selected.add(graph.getNodes().get(index));
		}
		
		return selected;
	}
    
	public List<Node> getSelectedNodesByCoreness(Graph graph, double percent){
		List<Node> selected = new ArrayList<Node>();
		
		Element[] elements = new Element[graph.getGraphSize()];
		int vn = graph.getGraphSize();
		for(int i = 0; i < vn; i++)
		{
			elements[i] = new Element(i,graph.getNodes().get(i).getNewCoreness());
		}
		Arrays.sort(elements);
		
		int immunityNum = (int)(graph.getGraphSize()*percent);
		
		for(int i = 0; i < immunityNum; i++){
			int index = elements[i].index;
			selected.add(graph.getNodes().get(index));
		}
		return selected;
	}
	
    public List<Node> getSelectedNodesByDegree(Graph graph, double percent){
		List<Node> selected = new ArrayList<Node>();
		
		Element[] elements = new Element[graph.getGraphSize()];
		int vn = graph.getGraphSize();
		for(int i = 0; i < vn; i++)
		{
			elements[i] = new Element(i,graph.getNodes().get(i).getDegree());
		}
		Arrays.sort(elements);
		
		int immunityNum = (int)(graph.getGraphSize()*percent);
		
		for(int i = 0; i < immunityNum; i++){
			int index = elements[i].index;
			selected.add(graph.getNodes().get(index));
		}
		
		return selected;
	}
    
	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public int getStrategyKind() {
		return strategyKind;
	}

	public void setStrategyKind(int strategyKind) {
		this.strategyKind = strategyKind;
	}
}
