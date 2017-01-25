package de.aquadiva.joyce;

import javax.persistence.EntityManager;

public class JoyceSymbolConstants {
	/**
	 * The base directory to where the BioPortal ontologies are downloaded.
	 */
	public final static String ONTOLOGY_DOWNLOAD_DIR = "joyce.ontologies.downloaddir";
	/**
	 * The directory to where all ontologies available in OWL (either because
	 * OWL was the original format or the original format could be converted to
	 * OWL) are stored.
	 */
	@Deprecated
	public final static String OWL_DIR = "joyce.ontologies.owl.dir";

	/**
	 * This symbol may be set to a comma separated list of BioPortal ontology
	 * acronyms which will then be downloaded during system setup. If the field
	 * is left blank, all available BioPortal ontologies will be downloaded.
	 */
	public final static String ONTOLOGIES_FOR_DOWNLOAD = "joyce.ontologies.download.acronyms";
	/**
	 * The location of a file errors are written occurring during ontology
	 * download.
	 */
	public static final String ONTOLOGY_DOWNLOAD_ERROR_FILE = "joyce.ontologies.download.errors.file";
	/**
	 * The BioPortal API key. Required for the download of BioPortal ontologies.
	 * Can be obtained on the BioPortal homepage.
	 * 
	 * @see http://bioportal.bioontology.org/
	 */
	public static final String BIOPORTAL_API_KEY = "joyce.bioportal.apikey";

	/**
	 * The name of the JPA persistence context as given in
	 * META-INF/persistence.xml.
	 */
	public static final String PERSISTENCE_CONTEXT = "joyce.jpa.persistence.context";

	/**
	 * Whether or not the tool setup algorithm - populating the database,
	 * adapting the dictionary etc - should freshly download the BioPortal
	 * ontologies. This step is not required if the download happened already
	 * and only database population should be done again.
	 */
	public static final String SETUP_DOWNLOAD_BIOPORTAL = "joyce.setup.download.bioportal";
	/**
	 * Whether or not to convert non-OWL ontologies to the OWL format during
	 * setup, after the ontologies have been downloaded.
	 */
	@Deprecated
	public static final String SETUP_CONVERT_TO_OWL = "joyce.setup.convert";
	/**
	 * The path to the dictionary containing all ontology module terms and maps
	 * them to their original ontology as well as to their ontology modules.
	 * This is the same file that has to be used for the ConceptTaggingService's
	 * gazetteer configuration.
	 */
	public static final String CONCEPT_TERM_DICTIONARY = "joyce.ontologies.concepts.dict";
	/**
	 * We use a dictionary-based tagger for the concepts in the user input. This
	 * component requires a properties configuration file. This symbol must be
	 * set to the location of this file.
	 */
	public final static String GAZETTEER_CONFIG = "joyce.ontologies.concepts.gazetteer.conffile";
	/**
	 * The path to a file that will be created by the SetupService in case
	 * something goes wrong. The file will then contain information about the
	 * occurred errors.
	 */
	public static final String SETUP_ERROR_FILE = "joyce.setup.errors.file";
	/**
	 * The path to the file mapping meta concepts to the actual ontology concept
	 * IRIs aggregated by the respective meta concept.
	 */
	public static final String META_CLASS_TO_IRI_CLASS_MAPPING = "joyce.ontologies.concepts.metamapping";

	/**
	 * The path to the file that maps class IRIs and meta class IDs to the IDs
	 * of the ontology modules they occur in. This file is created during system
	 * setup after ontology modularization and then stored to the location given
	 * by this symbol. Note that the file will be stored in GZIP format.
	 */
	public static final String MIXEDCLASS_ONTOLOGY_MAPPING = "joyce.ontologies.modules.concepts.mixedclassontologymapping";
	/**
	 * The path to the full dictionary that maps all synonyms of all classes of
	 * all ontologies in BioPortal to their IRIs. This dictionary is not meant
	 * to be used directly because it is most like not synchronized to the
	 * actual ontology repository of the tool. This is due to the fact that the
	 * dictionary is created using the BioPortal API which facilitates access to
	 * class synonyms (in the original ontologies, alternative names are stored
	 * under a range of different properties). This full dictionary is filtered
	 * during the setup process. The filtered version is stored at
	 * {@link #DICT_FILTERED_PATH}.
	 */
	public static final String DICT_FULL_PATH = "joyce.ontologies.dictionary.full.path";
	/**
	 * The path to the dictionary that has been filtered for classes actually
	 * occurring in the tool's ontology repository. See {@link #DICT_FULL_PATH}
	 * for details. This path should also be the one configured in the
	 * configuration file for ad-concept-tagging, probably something like
	 * 'bioportal.gazetter.properties'.
	 */
	public static final String DICT_FILTERED_PATH = "joyce.ontologies.dictionary.filtered.path";
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
	 * properties. Specifies whether to create, not create or update the
	 * database schema automatically at the start of the {@link EntityManager}
	 * on basis of the JPA persistence units defined in the persistence context
	 * (given by {@link #PERSISTENCE_CONTEXT}.
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
	 * within the Hibernate code, e.g. used for batch inserts or updates.
	 * Without this setting there won't be any batching and thus the overhead
	 * per SQL command issued would be relatively high.
	 */
	public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";

}
