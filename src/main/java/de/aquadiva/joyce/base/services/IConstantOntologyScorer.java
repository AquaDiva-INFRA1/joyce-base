package de.aquadiva.joyce.base.services;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;

/**
 * Interface for ontology scorers that compute a score for each ontology solely based on inherent properties of the
 * ontology module itself or by some other, possibly external, measurement that is constant in regards of the ontology.
 * 
 * @author faessler, friederike
 * 
 */
public interface IConstantOntologyScorer {
	/**
	 * Computes the score of <tt>o</tt> with respect to this scorer and sets the score to the ontology.
	 * 
	 * @param o
	 * @return
	 */
	void score(IOntology o);

	
	/**
	 * Computes the score of <tt>s</tt> with respect to this scorer and sets this score to the set of ontologies.
	 * The score is computed from scratch.
	 * 
	 * @param s
	 * @return
	 */
	void score(IOntologySet s);
	
	/**
	 * Returns the score of the set <tt>s</tt> unified with <tt>o</tt> with respect to this scorer.
	 * The method assumes that s has been already scored without taking the given ontology into account and calculates the score of the entire set based on this score.
	 * 
	 * @param s
	 * @param o
	 * @return
	 */
	Double getScoreAdded(IOntologySet s, IOntology o);
	
	/**
	 * Returns the score of the set <tt>s</tt> without <tt>o</tt> with respect to this scorer.
	 * The method assumes that s has been already scored and calculates the score of s\o based on this score without re-computing the score from scratch.
	 * 
	 * @param s
	 * @param o
	 * @return
	 */
	Double getScoreRemoved(IOntologySet s, IOntology o);
}
