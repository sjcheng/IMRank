

public class DirectedEdge {

	public Node startN;
	public Node endN;
	
	public DirectedEdge(Node start, Node end)
	{
		startN = start;
		endN = end;
	}

	public Node getEndN() {
		return endN;
	}

	public void setEndN(Node endN) {
		this.endN = endN;
	}

	public Node getStartN() {
		return startN;
	}

	public void setStartN(Node startN) {
		this.startN = startN;
	}
	
	
}
