package edu.rosehulman.salenotifier;

import java.util.ArrayList;
import java.util.List;

public class MultipleMatchesExeption extends Exception {

	private ArrayList<String> matches;
	
	public MultipleMatchesExeption(ArrayList<String> matches) {
		this.setMatches(matches);
	}

	public ArrayList<String> getMatches() {
		return matches;
	}

	protected void setMatches(ArrayList<String> matches) {
		this.matches = matches;
	}
	
}
