package com.test.testing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.VisitorException;

public abstract class Test {

	abstract void test() throws VisitorException;

	protected void showJsonFormat(BCMElement bcm, String msg) {
		try {
			if (msg != null) {
				System.out.println(msg);
			}
			System.out.println(new ObjectMapper().writer().writeValueAsString(bcm));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	protected void showJsonFormat(BCMElement bcm) {
		showJsonFormat(bcm, null);
	}
}
