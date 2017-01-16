package de.aquadiva.ontologyselection.base.data;

import java.util.TreeSet;

/**
 * A set of ontologies that have been scored. Additionally, this class exposes a single score for the whole set of ontologies
 * @author faessler
 * @deprecated  use {@link OntologySet} instead
 *
 */
@Deprecated
public class ScoredOntologySet extends TreeSet<IOntology> {
	private static final long serialVersionUID = -8363604539358184032L;
	private Double score;

	public ScoredOntologySet() {
		super();
		// TODO continue. The idea is that the ScoredOntologySet should be a TreeSet with a comparator that keeps the set sorted regarding the ontologie's overall score
//new Comparator<IOntology>() {
//
//	@Override
//	public int compare(IOntology o1, IOntology o2) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//			
//		}
	}
	
	/**
	 * The overall score of this ontology set.
	 * @return
	 */
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}
