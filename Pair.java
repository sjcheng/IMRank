
public class Pair {

	int rank;
	int number;
	
	public Pair(int rank, int number){
		this.rank = rank;
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public boolean equals(Pair temp){
		if(temp.rank == this.rank && temp.number == this.rank)
			return true;
		return false;
	}
}
