package com.test.main;

import org.apache.commons.cli.ParseException;

import com.test.context.DBStuffContext;

public class DBStuff {

	public static void main(String[] args) throws ParseException {
		DBStuffContext stuff = DBStuffContext.initialize(args);
		System.out.println(stuff);
	}

}
