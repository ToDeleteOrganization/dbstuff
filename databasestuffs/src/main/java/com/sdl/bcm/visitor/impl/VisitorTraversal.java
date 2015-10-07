package com.sdl.bcm.visitor.impl;

public enum VisitorTraversal {

	/**
	 * Specified with a visitor, it traverses only the source paragraph.
	 */
	SOURCE,

	/**
	 * Specified with a visitor, it traverses only the target paragraph.
	 */
	TARGET,

	/**
	 * Specified with a visitor, it traverses all paragraph.
	 */
	ALL;
}
