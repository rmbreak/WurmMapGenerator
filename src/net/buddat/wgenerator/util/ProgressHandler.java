package net.buddat.wgenerator.util;

import javax.swing.*;

public class ProgressHandler {

	JProgressBar progressBar;
	JLabel lblMemory;
	
	public ProgressHandler(JProgressBar progress, JLabel memory) {
		progressBar = progress;
		lblMemory = memory;
	}
	
	private void setMemoryUsage() {
		double totalMemory = (int)((Runtime.getRuntime().maxMemory())/1024.0/1024/1024*100)/100.0;
		int usedMemory = (int)(((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())*100/Runtime.getRuntime().maxMemory())); 
		lblMemory.setText("Mem: "+usedMemory+"% used of "+totalMemory+"gb");
	}
	
	public void update(int value) {
		update(value,getText());
	}
	
	public void update(int value, String text) {
		progressBar.setValue(value);
		progressBar.setString(text);
		setMemoryUsage();
	}
	
	public String getText() {
		return progressBar.getString();
	}
}
