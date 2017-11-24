package de.aquadiva.joyce;

import javax.persistence.EntityManager;

public class JoyceSymbolConstants {

	/**
	 * This class should not be instantiated, it is there for constants only.
	 */
	private JoyceSymbolConstants() {
	}

	/**
	 * This is a kind of 'meta configuration property' - if set, it defines the
	 * location of a property file that specifies the other properties.
	 */
	public static final String JOYCE_CONFIG_FILE = "joyce.configuration.file";
	/**
	 * The directory to where the BioPortal ontologies are downloaded.
	 */
	public static final String ONTOLOGIES_DOWNLOAD_DIR = "joyce.ontologies.download.dir";
	/**
	 * The directory to where the BioPortal mappings are downloaded.
	 */
	public static final String MAPPINGS_DOWNLOAD_DIR = "joyce.mappings.download.dir";
	/**
	 * This is the directory where BioPortal ontology meta information is downloaded
	 * to.
	 */
	public static final String ONTOLOGY_INFO_DOWNLOAD_DIR = "joyce.ontologies.info.download.dir";
	/**
	 * The directory to where all ontologies available in OWL (either because OWL
	 * was the original format or the original format could be converted to OWL) are
	 * stored.
	 */
	public static final String OWL_DIR = "joyce.ontologies.owl.dir";

	/**
	 * This symbol may be set to a comma separated list of BioPortal ontology
	 * acronyms which will then be downloaded during system setup. If the field is
	 * left blank, all available BioPortal ontologies will be downloaded.
	 */
	public static final String ONTOLOGIES_FOR_DOWNLOAD = "joyce.ontologies.download.acronyms";
	/**
	 * This symbol may be set to a comma separated list of BioPortal ontology
	 * acronyms for which ontology mappings will then be downloaded during system
	 * setup. If the field is left blank, the mappings for the ontologies in the
	 * ontology directory are downloaded.
	 */
	public static final String MAPPINGS_FOR_DOWNLOAD = "joyce.mappings.download.acronyms";
	/**
	 * The location of a file errors are written occurring during ontology download.
	 */
	public static final String ONTOLOGY_DOWNLOAD_ERROR_FILE = "joyce.ontologies.download.errors.file";
	/**
	 * The directory where the class names (preferred names, synonyms and more) of
	 * the downloaded ontologies are stored.
	 */
	public static final String ONTOLOGY_CLASSES_NAMES_DIR = "joyce.ontologies.classes.names.dir";
	/**
	 * The BioPortal API key. Required for the download of BioPortal ontologies. Can
	 * be obtained on the BioPortal homepage.
	 * 
	 * @see http://bioportal.bioontology.org/
	 */
	public static final String BIOPORTAL_API_KEY = "joyce.bioportal.apikey";

	/**
	 * The name of the JPA persistence context as given in META-INF/persistence.xml.
	 */
	public static final String PERSISTENCE_CONTEXT = "joyce.jpa.persistence.context";

	/**
	 * Whether or not the tool setup algorithm - populating the database, adapting
	 * the dictionary etc - should freshly download the BioPortal ontologies. This
	 * step is not required if the download happened already and only database
	 * population should be done again.
	 */
	public static final String SETUP_DOWNLOAD_BIOPORTAL_ONTOLOGIES = "joyce.setup.download.bioportal.ontologies";

	/**
	 * Whether or not the tool setup algorithm - populating the database, adapting
	 * the dictionary etc - should freshly download the BioPortal mappings. This
	 * step is not required if the download happened already.
	 */
	public static final String SETUP_DOWNLOAD_BIOPORTAL_MAPPINGS = "joyce.setup.download.bioportal.mappings";
	/**
	 * Whether or not to convert non-OWL ontologies to the OWL format during setup,
	 * after the ontologies have been downloaded.
	 */
	public static final String SETUP_CONVERT_TO_OWL = "joyce.setup.convert";
	/**
	 * Whether or not to import OWL ontologies into the database. Can be switched
	 * off if the database already exists as desired.
	 */
	public static final String SETUP_IMPORT_ONTOLOGIES = "joyce.setup.import";
	/**
	 * The path to the dictionary containing all ontology module terms and maps them
	 * to their original ontology as well as to their ontology modules. This is the
	 * same file that has to be used for the ConceptTaggingService's gazetteer
	 * configuration.
	 */
	public static final String CONCEPT_TERM_DICTIONARY = "joyce.ontologies.concepts.dict";
	/**
	 * We use a dictionary-based tagger for the concepts in the user input. This
	 * component requires a properties configuration file. This symbol must be set
	 * to the location of this file as a URL, prefixed with "file:". This is
	 * required because the GazetteerAnnotator uses the ChunkerProviderAlt class to
	 * load the configuration file. The ChunkerProviderAlt receives a UIMA resource
	 * loading object that internally uses a file URL. Thus, it must be an URL.
	 */
	public static final String GAZETTEER_CONFIG = "joyce.ontologies.concepts.gazetteer.conffile";
	/**
	 * The path to a file that will be created by the SetupService in case something
	 * goes wrong. The file will then contain information about the occurred errors.
	 */
	public static final String SETUP_ERROR_FILE = "joyce.setup.errors.file";
	/**
	 * This is an input file for the SetupService. The path to the file mapping meta
	 * concepts to the actual ontology concept IRIs aggregated by the respective
	 * meta concept.
	 */
	public static final String META_CLASS_TO_IRI_CLASS_MAPPING = "joyce.ontologies.concepts.metamapping";

	/**
	 * The path to the file that maps class IRIs and meta class IDs to the IDs of
	 * the ontology modules they occur in. This file is created during system setup
	 * after ontology modularization and then stored to the location given by this
	 * symbol. Note that the file will be stored in GZIP format.
	 */
	public static final String MIXEDCLASS_ONTOLOGY_MAPPING = "joyce.ontologies.modules.concepts.mixedclassontologymapping";

	public static final String NEO4J_PATH = "joyce.ontologies.neo4j.path";
	/**
	 * JPA database connection property. Specifies the database URL.
	 */
	public static final String JPA_JDBC_URL = "javax.persistence.jdbc.url";
	/**
	 * JPA database connection property. Specifies the JDBC driver to use.
	 */
	public static final String JPA_JDBC_DRIVER = "javax.persistence.jdbc.driver";
	/**
	 * JPA database connection property. Specifies the database user name.
	 */
	public static final String JPA_JDBC_USER = "javax.persistence.jdbc.user";
	/**
	 * JPA database connection property. Specifies the database password for the
	 * user specified by {@link #JPA_JDBC_USER}.
	 */
	public static final String JPA_JDBC_PW = "javax.persistence.jdbc.password";
	/**
	 * Hibernate setting passed to the JPA persistence context just like the JPA
	 * properties. Specifies whether to show the issued SQL commands or not.
	 */
	public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	/**
	 * Hibernate setting passed to the JPA persistence context just like the JPA
	 * properties. Specifies whether to create, not create or update the database
	 * schema automatically at the start of the {@link EntityManager} on basis of
	 * the JPA persistence units defined in the persistence context (given by
	 * {@link #PERSISTENCE_CONTEXT}.
	 * 
	 * @see
	 * 
	 *      <pre>
	 * http://docs.jboss.org/hibernate/core/3.3/reference/en/html/session-configuration.html
	 * "Table 3.7. Miscellaneous Properties"
	 *      </pre>
	 */
	public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	/**
	 * Hibernate setting passed to the JPA persistence context just like the JPA
	 * properties. Specifies the size of batches created by prepared statements
	 * within the Hibernate code, e.g. used for batch inserts or updates. Without
	 * this setting there won't be any batching and thus the overhead per SQL
	 * command issued would be relatively high.
	 */
	public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";

}
