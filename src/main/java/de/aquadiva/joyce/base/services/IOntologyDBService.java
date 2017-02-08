package de.aquadiva.joyce.base.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologyModule;

/**
 * This service is the typical follow-up on the
 * {@link IOntologyFormatConversionService} which should have been run after the
 * {@link OntologyDownloadService}. This service performs a database import
 * referencing the download files and parsing the JSON meta data downloaded with
 * the ontology files and joins them in database tables.
 * 
 * @author faessler
 * 
 */
public interface IOntologyDBService {
	/**
	 * Reads OWL ontology files from <tt>owlDir</tt> and their BioPortal
	 * metadata from <tt>metaDir</tt>. Creates {@link Ontology} instances and
	 * persists them into the database. Then returns the created ontologies.
	 * 
	 * @param owlDir
	 * @param metaDir
	 * @return
	 */
	List<Ontology> importBioPortalOntologies(File owlDir, File metaDir);

	/**
	 * Calls {@link #importBioPortalOntologies(File, File)} with the
	 * <tt>owlDir</tt> and <tt>metaDir</tt> as specified in the configuration
	 * file.
	 * 
	 * @return
	 */
	List<Ontology> importBioPortalOntologiesFromConfigDirs();

	/**
	 * Returns all full ontologies in the database, no modules.
	 * 
	 * @return All ontologies in the database.
	 */
	List<Ontology> getAllOntologies();

	List<OntologyModule> getAllOntologyModules();

	List<Ontology> getOntologiesByIds(String... ids);

	void commit();

	void beginTransaction();

	<T extends Ontology> void storeOntologies(Collection<T> ontologies, boolean commit);

	void shutdown();

	List<Ontology> getAllOntologiesWithModularizationError();

	List<Ontology> getOntologiesWithParsingError();

	List<OntologyModule> getModulesWithParsingError();
}
