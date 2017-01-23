package de.aquadiva.joyce.base.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * A class representing a set of ontologies or modules.
 * 
 * @author friederike
 *
 */
public class OntologySet extends InformationCachingObject implements IOntologySet {

	protected HashSet<IOntology> ontologies = new HashSet<>();
	protected Map<ScoreType, Double> scores;
	protected Double overallScore;
	
	@Override
	public Map<ScoreType, Double> getScores() {
		return scores;
	}
	
	public void setScores(Map<ScoreType, Double> scores) {
		this.scores = scores;
	}

	@Override
	public void setScore(ScoreType scoreType, Double score) {
		if (null == scores)
			scores = new HashMap<>();
		scores.put(scoreType, score);
	}
	
	@Override
	public Double getScore(ScoreType key) {
		if (null == scores)
			return 0d;
		Double score = scores.get(key);
		if (null == score)
			return 0d;
		return score;
	}
	
	@Override
	public Double getOverallScore() {
		return overallScore;
	}

	@Override
	public void setOverallScore(Double score) {
		this.overallScore = score;
	}

	@Override
	public HashSet<IOntology> getOntologies() {
		return this.ontologies;
	}
	
	@Override
	public void setOntologies(HashSet<IOntology> ontologies) {
		this.ontologies = ontologies;
	}

	@Override
	public void addOntology(IOntology o) {
		
		if( this.ontologies == null ) {
			this.ontologies = new HashSet<IOntology>();
		}
		
		this.ontologies.add(o);
		
	}
	
	/**
	 * Creates a deep copy of this ontology set. Caution: Referenced ontologies - like modules or a source ontologies - are NOT copied.
	 * Scores and cached information are also NOT copied.
	 */
	@Override
	public IOntologySet copy() {
		IOntologySet s = new OntologySet();
		HashSet<IOntology> ontologiesCopy = new HashSet<IOntology>();
		for(IOntology o : this.ontologies) {
			ontologiesCopy.add(o);
		}
		s.setOntologies(ontologiesCopy);

		return s;
	}

	@Override
	public String toString() {
		String str = "Ontology Set [";
		
		for(IOntology o : this.ontologies) {
			str += "m" + o.getId() + " ";
		}
		
		str += "; scores: ";
		
		for(ScoreType t : this.scores.keySet()) {
			str += "(" + t + ", " + this.scores.get(t) + ") ";
		}
		
//		for(ScoreType t : this.scores.keySet()) {
//			if(t==ScoreType.CLASS_OVERHEAD || t==ScoreType.CLASS_OVERLAP || t==ScoreType.TERM_COVERAGE) {
//				str += "(" + t + ", " + this.scores.get(t) + ") ";
//
//			}
//		}
		
		str+= "]";
		
		return str;
	}

	@Override
	public int hashCode() {
		return this.getIds().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OntologySet other = (OntologySet) obj;
				
		if(this.getIds().equals(other.getIds())) return true;
		
		return false;
		
	}

	@Override
	public HashSet<String> getIds() {
		HashSet<String> ids = new HashSet<String>();
		for(IOntology o : this.ontologies) {
			ids.add(o.getId());
		}
		return ids;
	}
	
	

}
