package com.test.exception;


@SuppressWarnings("serial")
public class MandatoryOptionsException extends RuntimeException {

	private final StringBuilder missingOptions = new StringBuilder();

	public void addMissingOption(String option, String optionDescr) {
		missingOptions.append(option + " - " + optionDescr);
		missingOptions.append(" - ");
		missingOptions.append(optionDescr);
	}
	
	public String getMessage() {
		if (!missingOptions.toString().isEmpty()) {
			missingOptions.insert(0, "The following mandatory options could not be found:");
		}
		return missingOptions.toString();
	}
	
}
