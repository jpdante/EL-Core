package com.ellisiumx.elcore.database;

public class DatabaseRunnable<T> {
	private Runnable runnable;
	private boolean success;
	
	public DatabaseRunnable(Runnable runnable)
	{
		runnable = runnable;
	}
	
	public void run(T data)
	{
		runnable.run();
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
