package de.aquadiva.ontologyselection.base.data;

/**
 * The different type of scores an ontology can have. Each scorer has at least
 * one corresponding score type in this enumeration.
 * 
 * @author faessler
 * 
 */
public enum ScoreType {
	/**
	 * A score indicating how recent the ontology of a module is.
	 */
	UP_TO_DATE,
	/**
	 * A score indicating how frequently used the module's ontology is.
	 */
	POPULARITY,
	/**
	 * A score indicating how active the development community around a given ontology is.
	 */
	ACTIVE_COMMUNITY,
	/**
	 * A score indicating how well the module's classes are described in a
	 * human-understandable manner.
	 */
	CLASS_DESCRIPTIVITY,
	/**
	 * A score indicating how well the set of input terms is reflected in the
	 * ontology module.
	 */
	TERM_COVERAGE,
	/**
	 * A score indicating how much overhead in terms of unnecessary classes has
	 * to be accepted when choosing a set of ontology modules.
	 */
	CLASS_OVERHEAD,
	/**
	 * A score indicating the amount of redundant classes in set of ontology modules.
	 */
	CLASS_OVERLAP,
	/**
	 * A score indicating how well the classes are interconnected by object
	 * properties
	 */
	OBJECT_PROPERTY_RICHNESS,
	/**
	 * A score indicating how well the logical constructs in the module
	 * correspond to the OWL profile the resulting ontology should fit to.
	 */
	OWL_PROFILE_ADEQUACY
}