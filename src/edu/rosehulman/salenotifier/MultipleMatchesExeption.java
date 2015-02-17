package edu.rosehulman.salenotifier;

import java.util.ArrayList;

public class MultipleMatchesExeption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5671255427988414366L;
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
