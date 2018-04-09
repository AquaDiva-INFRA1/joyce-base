package de.aquadiva.joyce.base.services;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.joyce.base.services.IOntologyDBService;
import de.aquadiva.joyce.base.services.JoyceBaseModule;

public class OntologyDBServiceImportTest {
	private static Registry registry;

	@BeforeClass
	public static void setup() {
		registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);
	}

	@AfterClass
	public static void shutdown() {
		registry.shutdown();
	}
	
	@Test
	public void testDBImport() {
		IOntologyDBService importService = registry.getService(IOntologyDBService.class);
		importService.importBioPortalOntologiesFromConfigDirs();
	}
}
