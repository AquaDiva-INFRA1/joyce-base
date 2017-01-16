package de.aquadiva.ontologyselection.services;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.services.IOWLParsingService;
import de.aquadiva.ontologyselection.base.services.IOntologyDBService;
import de.aquadiva.ontologyselection.base.services.OSBaseModule;

public class OWLParsingServiceTest {

	private static Registry registry;

	// In this test we do NOT use BeforeClass and AfterClass because the OWLOntologyManager instance used for the
	// parsingService will memorize already loaded ontologies. So we just shut down the registry after each tests,
	// getting a new service with a new OntologyManager each time.
	@Before
	public void setup() {
		registry = RegistryBuilder.buildAndStartupRegistry(OSBaseModule.class);
	}

	@After
	public void shutdown() {
		registry.shutdown();
	}

	@Test
	public void testParseFromFile() throws IOException {
		IOWLParsingService parsingService = registry.getService(IOWLParsingService.class);
		OWLOntology o = parsingService.parse(new File("src/test/resources/obi.owl"));
		assertEquals(2864, o.getClassesInSignature().size());
	}

	@Test
	public void testParseFromByteArray() throws Exception {
		IOWLParsingService parsingService = registry.getService(IOWLParsingService.class);
		OWLOntology o = parsingService.parse(FileUtils.readFileToByteArray(new File("src/test/resources/obi.owl")));
		assertEquals(2864, o.getClassesInSignature().size());
	}

	@Test
	public void testFromOntology() throws Exception {
		IOntologyDBService dbService = registry.getService(IOntologyDBService.class);
		dbService.importBioPortalOntologies(new File("src/test/resources/ontology-for-db-import"), new File(
				"src/test/resources/ontology-download/meta-json"));
		 List<Ontology> ontologiesByIds = dbService.getOntologiesByIds("OBI");
		 assertEquals(1, ontologiesByIds.size());
		 Ontology obi = ontologiesByIds.get(0);
		 // enforce reading from the byte array
		 obi.setFile(null);
		 
 		 IOWLParsingService parsingService = registry.getService(IOWLParsingService.class);
		 OWLOntology o = parsingService.parse(obi);
		 assertEquals(2864, o.getClassesInSignature().size());
	}
}
