package de.aquadiva.joyce.base.data;

import java.util.Map;

/**
 * An interface for objects that can be scored. These objects can e.g. be ontologies, modules or sets of ontologies.
 * 
 * @author friederike
 *
 */
public interface IScoredObject {
	/**
	 * The score values for this ontology, as determined at the current time. TODO talk about this and agree upon a
	 * model, also see {@link ScoreType}.
	 * 
	 * @return
	 */
	Map<ScoreType, Double> getScores();
	
	/**
	 * Sets the ontology's score of the given score type.
	 */
	void setScore(ScoreType scoreType, Double score);
	
	/**
	 * Returns the ontology's score of the given score type.
	 */
	Double getScore(ScoreType scoreType);
	
	Double getOverallScore();
	
	void setOverallScore(Double score);
}
