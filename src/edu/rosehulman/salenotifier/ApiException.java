package edu.rosehulman.salenotifier;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ApiException extends Exception {
	
	private Exception innerException;
	
	@Override
	public void printStackTrace() {
		innerException.printStackTrace();
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		innerException.printStackTrace(s);
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		innerException.printStackTrace(s);
	}
	
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
