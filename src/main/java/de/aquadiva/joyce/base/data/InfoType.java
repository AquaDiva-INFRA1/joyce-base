package de.aquadiva.joyce.base.data;

/**
 * The different types of cached information an ontology or a set of ontologies
 * can have.
 * 
 * @author friederike
 * 
 */
public enum InfoType {
	/**
	 * A multiset of classes defined within a given (set of) ontologie(s) that
	 * cover the input terms. An element is represented in the multiset as often
	 * as it occurs within the given ontologies.
	 */
	COVERING_CLASSES,
	/**
	 * A multiset of classes defined within a given (set of) ontologie(s) that
	 * do not cover any input term. An element is represented in the multiset as
	 * often as it occurs within the given ontologies.
	 */
	NON_COVERING_CLASSES,
	/**
	 * A multiset of all the classes defined within a given (set of)
	 * ontologie(s). An element is represented in the multiset as often as it
	 * occurs within the given ontologies.
	 */
	ALL_CLASSES,
	/**
	 * A multiset of classes that were found in query input text and were not
	 * found in a given (set of) ontologie(s). Each element is only present once
	 * since a class cannot be absent more than once.
	 */
	MISSING_CLASSES,
}