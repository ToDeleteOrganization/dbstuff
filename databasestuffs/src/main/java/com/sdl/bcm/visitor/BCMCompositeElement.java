package com.sdl.bcm.visitor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public interface BCMCompositeElement<PARENT, CHILD> {

	/**
	 * Returns the direct parent for this BCMElement.
	 * 
	 * @return The direct parent.
	 */
	@JsonBackReference
	PARENT getParent();

	/**
	 * Returns the direct children of this element.
	 * 
	 * @return A list with the direct children.
	 */
	@JsonManagedReference
	List<CHILD> getChildren();
}
