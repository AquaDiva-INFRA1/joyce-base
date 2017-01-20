package de.aquadiva.joyce.base.data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class OntologyModule extends Ontology {
	private Ontology sourceOntology;
	private String pivotConceptId;

	public String getPivotConceptId() {
		return pivotConceptId;
	}

	public void setPivotConceptId(String pivotConceptId) {
		this.pivotConceptId = pivotConceptId;
	}

	@ManyToOne
	public Ontology getSourceOntology() {
		return sourceOntology;
	}

	public void setSourceOntology(Ontology sourceOntology) {
		this.sourceOntology = sourceOntology;
	}

	
	
	/**
	 * Creates a deep copy of this module. Caution: This means that also the source ontology is copied.
	 */
	public OntologyModule copy(Ontology sourceOntology) {
		OntologyModule m = new OntologyModule();
		m.setScores(scores);
		m.setId(id);
		m.setFile(file);
		m.setOntologyData(ontologyData);
		m.setOwlOntology(owlOntology);
		m.setName(name);
		m.setOverallScore(overallScore);
		m.setSourceOntology(sourceOntology);
		m.setPivotConceptId(pivotConceptId);
		m.setClassIds(classes);

		return m;
	}
	
	/**
	 * Creates a deep copy of this module. Caution: This means that also the source ontology is copied.
	 */
	@Override
	public OntologyModule copy() {
//		OntologyModule m = new OntologyModule();
//		m.setScores(scores);
//		m.setId(id);
//		m.setFile(file);
//		m.setOntologyData(ontologyData);
//		m.setOwlOntology(owlOntology);
//		m.setName(name);
//		m.setOverallScore(overallScore);
//		m.setSourceOntology(sourceOntology.copy(this));
//		m.setPivotConceptId(pivotConceptId);
		return copy(sourceOntology.copy(this));

//		return m;

	}
}
