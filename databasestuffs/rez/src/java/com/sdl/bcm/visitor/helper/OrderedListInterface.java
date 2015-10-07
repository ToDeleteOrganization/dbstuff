package com.sdl.bcm.visitor.helper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * @author vnastase
 * @version 1.0
 * An interface for a custom List type data class that is cloneable and functions like both a List and a Deque
 * Used in terminology application, for communication between methods
 */
public interface OrderedListInterface<T> extends List<T>, Deque<T>, Collection<T>, Iterable<T>, Cloneable, Serializable {

}
