package com.test.model;

import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

@SuppressWarnings("serial")
public class CustomOptions extends Options {

	public void addOptions(Options optionsToAdd) {
		System.out.println("add options");
	}
	
}
