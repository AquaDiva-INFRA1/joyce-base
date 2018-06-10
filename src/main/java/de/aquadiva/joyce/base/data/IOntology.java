package de.aquadiva.joyce.base.data;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * An interface for an ontology as it is required by the ontology selection tools.
 * 
 * @author faessler, friederike
 * 
 */
public interface IOntology extends IScoredObject, IInformationCachingObject {
	
	/**
	 * 
	 * @return The Id of the ontology.
	 */
	public String getId();
	
	/**
	 * 
	 * @param scoreType
	 * @return the score of the given type set for this ontology
	 */
	public Double getScore(ScoreType scoreType);
	
	/**
	 * 
	 * @param scoreType
	 * @return true, if the score of the given type has been already calculated, else false
	 */
	boolean isSetScore(ScoreType scoreType);
	
	/**
	 * 
	 * @return The base file holding the actual ontology data in the file system, if accessible.
	 */
	File getFile();

	/**
	 * 
	 * @return The file contents of the actual ontology download data. This data was originally stored at the location
	 *         pointed to by {@link #getFile()}. However, the data is also stored in the database in case the file is
	 *         not accessible.
	 */
	byte[] getOntologyData();

	/**
	 * @return The {@link OWLOntology} representation of this ontology.
	 */
	OWLOntology getOwlOntology();

	/**
	 * Creates a deep copy of this ontology. Caution: References ontologies - like modules or a source ontology - are also copied.
	 * @return
	 */
	IOntology copy();
	
	/**
	 * @return The Ids of the classes defined within this ontology.
	 */
	Set<String> getClassIds();
	
	/**
	 * 
	 * @return the latest release date of this ontology
	 */
	Date getLatestReleaseDate();
	
	/**
	 * 
	 * @return the homepage of the latest release
	 */
	String getHomepage();
	
	/**
	 * 
	 * @return the documentation page of the latest release
	 */
	String getDocumentationPage();

	/**
	 * 
	 * @return the status of the release, e.g. "production"
	 */
	String getStatus();
	
	/**
	 * 
	 * @return the number of submissions of this ontology to BioPortal per year
	 */
	Map<Integer, Integer> getSubmissions();
	
	/**
	 * 
	 * @return the number of projects that use this ontology
	 */
	Integer getNumberOfReferencingProjects();
	
	void setOwlOntology(OWLOntology owlOntology);
	
	boolean isOwlOntologySet();
}
