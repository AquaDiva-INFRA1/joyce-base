package de.aquadiva.joyce.base.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.aquadiva.joyce.base.util.JoyceException;

/**
 * Internal class for representing ontologies, original as well as derived ones.
 * This class is a JPA entity and can be used made persistent in a database.
 * TODO: make sure this actually works as intended with the current appearance
 * of the class. TODO: what information / fields do we want to know / store for
 * an ontology? Examples: Last update (download timestamp); constant attributes
 * not inherently represented in the ontology itself like popularity, reasoning
 * time on the different reasoners, OWL profile, ... what else?
 * 
 * @author faessler, friederike
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Ontology extends InformationCachingObject implements IOntology {

	@Override
	public String toString() {

		return "Ontology " + this.id;
	}

	private static final Logger log = LoggerFactory.getLogger(Ontology.class);

	/**
	 * User-displayble name of this ontology. For original, unchanged
	 * ontologies, this should be the original ontology name. For an ontology
	 * derived from a particular ontology, e.g. by pruning, modularizing etc.,
	 * the name should expose the original ontology as well as the type of
	 * transformation.
	 */
	protected String name;
	protected File file;
	protected OWLOntology owlOntology;
	protected String id;
	protected List<OntologyModule> modules;
	protected byte[] ontologyData;
	protected Map<ScoreType, Double> scores;
	protected Double overallScore;
	protected Set<String> classes; // the classes defined within this ontology
									// given by their Ids
	protected Date latestReleaseDate;
	protected String homepage;
	protected String documentationPage;
	protected String status;
	protected Map<Integer, Integer> submissions; // the number of submissions
													// per year
	protected Integer numberOfReferencingProjects;

	private boolean hasParsingError;

	private boolean hasModularizationError;

	@Id
	@Override
	public String getId() {
		if (StringUtils.isBlank(id))
			throw new IllegalStateException(
					"The ID of an ontology was requested, but the ID has never been set. This is an illegal state after instantiation.");
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// TODO: storage just makes sense for constant scores
	@ElementCollection(fetch=FetchType.EAGER)
	public Map<ScoreType, Double> getScores() {
		return scores;
	}

	public void setScores(Map<ScoreType, Double> scores) {
		this.scores = scores;
	}

	public void setScore(ScoreType key, Double value) {
		if (null == scores)
			scores = new HashMap<>();
		scores.put(key, value);
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

	public void setOwlOntology(OWLOntology owlOntology) {
		this.owlOntology = owlOntology;
	}

	@Transient
	public OWLOntology getOwlOntology() {
		return owlOntology;
	}

	public String getName() {
		return name;
	}

	public void setFile(File file) {
		this.file = file;
		if (null == ontologyData && null != file) {
			try {
//				log.debug("Reading file data from file {} for ontology {}", file, id);
//				long time = System.currentTimeMillis();
				ontologyData = FileUtils.readFileToByteArray(file);
//				time = System.currentTimeMillis() - time;
//				log.deb	ug("Took {}ms", time);
			} catch (IOException e) {
				// log.warn(
				// "Ontology data could not be set due to IOException: {}",
				// e.getMessage());
				// e.printStackTrace();
			}
		}
	}

	public File getFile() {
		return file;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "sourceOntology", fetch=FetchType.EAGER)
	public List<OntologyModule> getModules() {
		return modules;
	}

	public void setModules(List<OntologyModule> modules) {
		this.modules = modules;
	}

	public void setOntologyData(byte[] ontologyData) {
		this.ontologyData = ontologyData;
	}

	@Lob
	@Override
	public byte[] getOntologyData() {
		return ontologyData;
	}

	/**
	 * Creates a deep copy of this ontology. Caution: Also the modules are
	 * copied.
	 */
	@Override
	public Ontology copy() {
		Ontology o = basicCopy();
		List<OntologyModule> moduleCopies = new ArrayList<>();
		for (OntologyModule m : this.modules) {
			moduleCopies.add(m.copy(this));
		}
		o.setModules(moduleCopies);

		return o;
	}

	public Ontology copy(OntologyModule ontologyModule) {
		Ontology o = basicCopy();
		List<OntologyModule> moduleCopies = new ArrayList<>();
		for (OntologyModule m : this.modules) {
			if (!m.equals(ontologyModule)) {
				moduleCopies.add(m.copy(this));
			}
		}
		o.setModules(moduleCopies);

		return o;
	}

	/**
	 * Creates and returns a static module with the given ID and data. Returns
	 * null if the module was already present. Static modules only depend on the
	 * ontology itself. In contrast, non static modules may depend on further
	 * information, a set of input terms the ontology should be tailored to, for
	 * example.
	 * 
	 * @param moduleId
	 * @param moduleData
	 * @return
	 */
	// TODO why not just return the already existing module when the moduleId already exists?
	public OntologyModule createStaticModule(String moduleId, byte[] moduleData) {
		OntologyModule m = new OntologyModule();
		m.setId(moduleId);
		m.setOntologyData(moduleData);
		m.setSourceOntology(this);
		m.setHomepage(homepage);
		m.setDocumentationPage(documentationPage);
		m.setScores(new HashMap<ScoreType, Double>(scores));
		m.setLatestReleaseDate(latestReleaseDate);
		m.setName(name);
		m.setNumberOfReferencingProjects(numberOfReferencingProjects);
		m.setStatus(status);

		boolean moduleAlreadyPresent = addModule(m);
		if (!moduleAlreadyPresent)
			return m;
		return null;
	}

	/**
	 * Creates and returns a non static module with the given ID and data. Thus, this method does not check if the module already exists in any way, a new module is created and returned in any case.
	 * 
	 * @param moduleId
	 * @param moduleData
	 * @return
	 */
	public OntologyModule createNonStaticModule(String moduleId, byte[] moduleData) {
		OntologyModule m = new OntologyModule();
		m.setId(moduleId);
		m.setOntologyData(moduleData);
		m.setSourceOntology(this);
		m.setHomepage(homepage);
		m.setDocumentationPage(documentationPage);
		m.setScores(new HashMap<ScoreType, Double>(scores));
		m.setLatestReleaseDate(latestReleaseDate);
		m.setName(name);
		m.setNumberOfReferencingProjects(numberOfReferencingProjects);
		m.setStatus(status);

		return m;
	}

	/**
	 * Copy without modules
	 * 
	 * @return
	 */
	private Ontology basicCopy() {
		Ontology o = new Ontology();
		o.setScores(scores);
		o.setId(id);
		o.setFile(file);
		o.setOntologyData(ontologyData);
		o.setOwlOntology(owlOntology);
		o.setName(name);
		o.setOverallScore(overallScore);
		o.setClassIds(classes);
		o.setHomepage(homepage);
		o.setDocumentationPage(documentationPage);
		o.setLatestReleaseDate(latestReleaseDate);
		o.setNumberOfReferencingProjects(numberOfReferencingProjects);
		o.setStatus(status);

		return o;
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
	@ElementCollection
	public Set<String> getClassIds() {
		return classes;
	}

	public void setClassIds(Set<String> classIds) {
		this.classes = classIds;
	}

	@Override
	public Date getLatestReleaseDate() {
		return this.latestReleaseDate;
	}

	public void setLatestReleaseDate(Date latestReleaseDate) {
		this.latestReleaseDate = latestReleaseDate;
	}

	@Override
	public boolean isSetScore(ScoreType scoreType) {
		if (null == scores)
			return false;
		Double score = scores.get(scoreType);
		if (null == score) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getHomepage() {
		return this.homepage;
	}

	@Override
	public String getDocumentationPage() {
		return this.documentationPage;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	@ElementCollection
	public Map<Integer, Integer> getSubmissions() {
		return this.submissions;
	}

	@Override
	public Integer getNumberOfReferencingProjects() {
		return this.numberOfReferencingProjects;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setDocumentationPage(String documentation) {
		this.documentationPage = documentation;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubmissions(Map<Integer, Integer> submissions) {
		this.submissions = submissions;
	}

	public void setNumberOfReferencingProjects(Integer numberOfReferencingProjects) {
		this.numberOfReferencingProjects = numberOfReferencingProjects;
	}

	public boolean addModule(OntologyModule m) {
		if (null == modules)
			modules = new ArrayList<>();
		boolean moduleAlreadyPresent = false;
		for (OntologyModule presentModuls : modules)
			if (presentModuls.getId().equals(m.getId()))
				moduleAlreadyPresent = true;
		if (!moduleAlreadyPresent)
			modules.add(m);
		return moduleAlreadyPresent;
	}

	public boolean getHasParsingError() {
		return hasParsingError;
	}

	public void setHasParsingError(boolean hasParsingError) {
		this.hasParsingError = hasParsingError;
	}

	public void setHasModularizationError(boolean hasModularizationError) {
		this.hasModularizationError = hasModularizationError;
	}

	public boolean getHasModularizationError() {
		return hasModularizationError;
	}

	@Transient
	public boolean isOwlOntologySet() {
		return owlOntology != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ontology other = (Ontology) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
