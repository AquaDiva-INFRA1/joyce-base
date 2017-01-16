package de.aquadiva.ontologyselection.services;

import java.io.File;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.services.IOntologyDBService;
import de.aquadiva.ontologyselection.base.services.OSBaseModule;

public class OntologyDBServiceImportTest {
	private static Registry registry;

	@BeforeClass
	public static void setup() {
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
