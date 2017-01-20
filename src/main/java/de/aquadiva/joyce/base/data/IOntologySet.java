package de.aquadiva.joyce.base.data;

import java.util.HashSet;

/**
 * An interface for sets of ontologies/modules.
 * 
 * @author friederike
 *
 */
public interface IOntologySet extends IScoredObject, IInformationCachingObject {
	
	/**
	 * 
	 * @return the ontologies belonging to this ontology set
	 */
	HashSet<IOntology> getOntologies();
	
	/**
	 * Adds a ontology/module to this set.
	 * 
	 * @param o
	 */
	void addOntology(IOntology o);
	
	/**
	 * Creates a deep copy of this ontology set. Caution: Referenced ontologies - like modules or a source ontologies - are NOT copied.
	 * @return
	 */
	IOntologySet copy();
	
	public void setOntologies(HashSet<IOntology> ontologies);
	
	/**
	 * Returns the ids of the ontologies contained in this set.
	 * 
	 * @return
	 */
	public HashSet<String> getIds();
}
