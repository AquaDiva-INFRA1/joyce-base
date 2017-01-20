package de.aquadiva.joyce.base.services;

import com.google.common.collect.Multiset;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;

/**
 * Interface for ontology scorers that do not only depend on the ontology itself but also on a predefined set of class
 * IDs narrow down the scope of the current ontology selection process. Different class sets might lead to significantly
 * different results for each ontology. For example, a coverage scorer will score very differently depending on how many
 * input classes can be found in the respective ontology.
 * 
 * @author faessler, friederike
 * 
 */
public interface IVariableOntologyScorer {
	/**
	 * Computes the score of <tt>o</tt> with respect to this scorer and the given set of <tt>classIds</tt> and sets this score to the ontology.
	 * 
	 * @param o
	 * @param classIds
	 * @return
	 */
	void score(IOntology o, Multiset<String> classIds);
	
	/**
	 * Computes the score of <tt>s</tt> with respect to this scorer and the given set of <tt>classIds</tt> and sets this score to the set of ontologies.
	 * The score is computed from scratch.
	 * 
	 * @param s
	 * @param classIds
	 * @return
	 */
	void score(IOntologySet s, Multiset<String> classIds);
	
	/**
	 * Returns the score of the set <tt>s</tt> unified with <tt>o</tt> with respect to this scorer and the given set of <tt>classIds</tt>.
	 * The method assumes that s has been already scored without taking the given ontology into account and calculates the score of the entire set based on this score.
	 * 
	 * @param s
	 * @param o
	 * @param classIds
	 * @return
	 */
	Double getScoreAdded(IOntologySet s, IOntology o, Multiset<String> classIds);
	
	/**
	 * Returns the score of the set <tt>s</tt> without <tt>o</tt> with respect to this scorer and the given set of <tt>classIds</tt>.
	 * The method assumes that s has been already scored and calculates the score of s\o based on this score without re-computing the score from scratch.
	 * 
	 * @param s
	 * @param o
	 * @param classIds
	 * @return
	 */
	Double getScoreRemoved(IOntologySet s, IOntology o, Multiset<String> classIds);
}
