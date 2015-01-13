package edu.rosehulman.salenotifier;

public class ApiException extends Exception {
	
	private Exception innerException;
	
	public ApiException(Exception e) {
		setInnerException(e);
	}

	public Exception getInnerException() {
		return innerException;
	}

	protected void setInnerException(Exception innerException) {
		this.innerException = innerException;
	}
	
}
