package de.aquadiva.ontologyselection.services;

import java.io.File;
import java.util.Properties;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.OSSymbolConstants;
import de.aquadiva.ontologyselection.base.services.IOntologyDBService;
import de.aquadiva.ontologyselection.base.services.OSBaseModule;

/**
 * Here we just assure that the JPA property settings given via the central Tapestry configuration facility used correctly.
 * @author faessler
 *
 */
public class OntologyDBServiceImportConfigSymbolsTest {
	private static Registry registry;

	@BeforeClass
	public static void setup() {
		// configure the default production JPA context which does not define any settings beside the persistent classes
		System.setProperty(OSSymbolConstants.PERSISTENCE_CONTEXT, "de.aquadiva.ontologyselection.jpa");
		// set the missing JPA properties via system properties
		System.setProperty(OSSymbolConstants.JPA_JDBC_DRIVER, "org.h2.Driver");
		System.setProperty(OSSymbolConstants.JPA_JDBC_URL, "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
		System.setProperty(OSSymbolConstants.JPA_JDBC_USER, "sa");
		System.setProperty(OSSymbolConstants.JPA_JDBC_PW, "");
		System.setProperty(OSSymbolConstants.HIBERNATE_SHOW_SQL, "true");
		System.setProperty(OSSymbolConstants.HIBERNATE_HBM2DDL_AUTO, "create");
		System.setProperty(OSSymbolConstants.HIBERNATE_JDBC_BATCH_SIZE, "50");
		registry = RegistryBuilder.buildAndStartupRegistry(OSBaseModule.class);
	}

	@AfterClass
	public static void shutdown() {
		registry.shutdown();
	}
	
	@Test
	public void testDBImport() {
		IOntologyDBService importService = registry.getService(IOntologyDBService.class);
		importService.importBioPortalOntologies(new File("src/test/resources/ontology-for-db-import"), new File("src/test/resources/ontology-download/meta-json"));
	}
}
