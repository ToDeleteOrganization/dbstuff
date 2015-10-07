package com.sdl.bcm.visitor;

public interface BCMElement {

	/**
	 * Entry point for a visitor in order to visit this element hierarchy.
	 * 
	 * @param visitor
	 *            The visitor.
	 * 
	 * @return <i>True</i> if this element accepts the visitor <br />
	 *         <i>False</i> otherwise.
	 */
	boolean accept(BCMVisitor visitor);

}
