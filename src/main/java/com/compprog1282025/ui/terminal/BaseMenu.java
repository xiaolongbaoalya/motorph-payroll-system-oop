package com.compprog1282025.ui.terminal;

public abstract class BaseMenu {
	
	public void displayHeader(String header) {
		System.out.println("\n" + "=".repeat(header.length()));
        System.out.println(header);
        System.out.println("=".repeat(header.length()));
	}
	
	public void displaySubHeader(String subheader) {
		System.out.println("\n" + "-".repeat(subheader.length()));
        System.out.println(subheader);
        System.out.println("-".repeat(subheader.length()));
	}
}
