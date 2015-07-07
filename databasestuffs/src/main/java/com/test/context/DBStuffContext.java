package com.test.context;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.test.loaders.ClassLoaderProperties;
import com.test.loaders.FileLoaderProperties;
import com.test.util.OptionsUtil;

public class DBStuffContext {

	private static final String OPTIONAL_COMMANDS_FILE = "options" + File.separator + "optional_commands.properties";

	private static final String MANDATORY_COMMANDS_FILE = "options" + File.separator + "mandatory_commands.properties";

	private static DBStuffContext context;

	private Options commandsOptions;

	private String[] commandLineArguments;

	private DBStuffContext(String[] args) {
		this.commandLineArguments = args;
	}

	public static DBStuffContext initialize(final String[] args) throws ParseException {
		if (context == null) {
			initContext(args);
		}
		return context;
	}

	private static void initContext(String[] args) throws ParseException {
		context = new DBStuffContext(args);
		context.initAvailableOptions();
		context.initCommandLineOptions();
	}

	private void initAvailableOptions() throws ParseException {
		if (commandsOptions != null) {
			return;
		}
		initMandatoryOptions();

		Options mandatoryOptions = OptionsUtil.getMandatoryOptions();
		Options optionalOptions = OptionsUtil.getOptionalOptions();
		
		Options allOptions = OptionsUtil.getAllOptions();
		
	}

	private void initMandatoryOptions() throws ParseException {
//		Options mandatoryOptions = OptionsUtil.getMandatoryOptions(MANDATORY_COMMANDS_FILE);

//		CommandLine cmd = new BasicParser().parse(mandatoryOptions, commandLineArguments);
//		cmd.h
	}

	private void initCommandLineOptions() {
		// TODO Auto-generated method stub

	}

}
