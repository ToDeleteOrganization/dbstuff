package com.test.model;

import org.apache.commons.cli.Option;

@SuppressWarnings("serial")
public class CustomOption extends Option {

	public CustomOption(String opt, String description, boolean required) throws IllegalArgumentException {
		this(opt, null, false, description, required);
	}

	public CustomOption(String opt, boolean hasArg, String description, boolean required) throws IllegalArgumentException {
		this(opt, null, hasArg, description, required);
	}

	public CustomOption(String opt, String longOpt, boolean hasArg, String description, boolean required) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		setRequired(required);
	}

}
