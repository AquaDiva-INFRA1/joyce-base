package de.aquadiva.ontologyselection.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.services.IOntologyDBService;
import de.aquadiva.ontologyselection.base.services.JoyceBaseModule;

public class OntologyDBServiceRetrievalTest {

	private static Registry registry;

	@BeforeClass
	public static void setup() {
		registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);

		// Import the test ontologies in the in-memory database so we can then test retrieval
		IOntologyDBService importService = registry.getService(IOntologyDBService.class);
		importService.importBioPortalOntologies(new File("src/test/resources/ontology-for-db-import"), new File(
				"src/test/resources/ontology-download/meta-json"));

		// TODO insert modularization and test its retrieval in the method stub below
	}

	@AfterClass
	public static void shutdown() {
		registry.shutdown();
	}

	@Test
	public void testGetAllOntologies() {
		IOntologyDBService dbService = registry.getService(IOntologyDBService.class);
		List<Ontology> ontologies = dbService.getAllOntologies();
		assertEquals(3, ontologies.size());
	}

	@Test
	public void testGetOntologiesById() {
		IOntologyDBService dbService = registry.getService(IOntologyDBService.class);
		List<Ontology> ontos = dbService.getOntologiesByIds("GRO", "BCO", "ENVO");
		HashSet<String> expectedIds = Sets.newHashSet("GRO", "BCO");
		for (Ontology onto : ontos) {
			assertTrue("ID " + onto.getId() + " is not (anymore) included in the expected set of ontology IDs.",
					expectedIds.remove(onto.getId()));
		}
		assertTrue("Not all expected ontologies have been returned", expectedIds.isEmpty());
	}
	
	@Test
	public void testGetOntologyDataFromDatabase() throws Exception {
		IOntologyDBService dbService = registry.getService(IOntologyDBService.class);
		List<Ontology> ontos = dbService.getOntologiesByIds("OBI");
		assertEquals("OBI ontology not found in database", 1, ontos.size());
		Ontology bco = ontos.get(0);
		byte[] ontologyData = bco.getOntologyData();
		byte[] ontologyDataFromFile = FileUtils.readFileToByteArray(new File("src/test/resources/ontology-for-db-import/OBI.owl.gz"));
		assertArrayEquals(ontologyDataFromFile, ontologyData);
	}

	@Test
	public void testGetAllOntologyModules() {
		// TODO implement
	}

}
