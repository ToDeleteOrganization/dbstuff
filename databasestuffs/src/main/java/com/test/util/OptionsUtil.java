package com.test.util;

import org.apache.commons.cli.Options;

import com.test.model.CustomOption;
import com.test.model.CustomOptions;

public class OptionsUtil {

	private static Options mandatoryOptions = null;
	static {
		mandatoryOptions = new Options();
		mandatoryOptions.addOption(new CustomOption("h", "help", false, "Prints the help message.", true));
	}

	private static Options optionalOptions = null;
	static {
		optionalOptions = new Options();
		optionalOptions.addOption(new CustomOption("h", "help", false, "Prints the help message.", true));
	}

	public static final Options getOptionalOptions() {
		return optionalOptions;
	}

	public static final Options getMandatoryOptions() {
		return mandatoryOptions;
	}

	public static Options getAllOptions() {
		CustomOptions options = new CustomOptions();
		options.addOptions(mandatoryOptions);
		options.addOptions(optionalOptions);
		return options;
	}
}
