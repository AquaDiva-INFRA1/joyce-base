package de.aquadiva.joyce.base.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.aquadiva.joyce.base.services.IOntologyFormatConversionService;
import de.aquadiva.joyce.base.services.JoyceBaseModule;
import de.julielab.bioportal.ontologies.apps.NameExtractorApplication;

public class OntologyFormatConversionServiceTest {
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
	public void testCopyOWLOntologies() throws IOException {
		IOntologyFormatConversionService formatConverter = registry
				.getService(IOntologyFormatConversionService.class);

		SymbolSource symbolSource = registry.getService(SymbolSource.class);
		String owlDirPath = symbolSource
				.valueForSymbol(JoyceSymbolConstants.OWL_DIR);

		File owlDir = new File(owlDirPath);

		// deletes the target directory so at the end of the test we can be sure
		// the data there was created by the
		// current test run and not by an old test run
		FileUtils.deleteDirectory(owlDir);

		formatConverter.convertFromDownloadDirToOwlDir();

		String[] owlFiles = owlDir.list();
		assertNotNull(owlFiles);
		assertEquals(3, owlFiles.length);
	}
	
	@Test
	public void dontConvertOwlfilesTwice() throws IOException {
		Path path = Files.createTempDirectory("converted");
		Path src = Paths.get("src/test/resources/ontology-for-db-import");
		Path ontology = Paths.get("src/test/resources/ontology-for-db-import/BCO.owl.gz");
		Path dest = Paths.get(path.toString(), ontology.getFileName().toString());
		dest.toFile().deleteOnExit();
		Files.copy(ontology, dest);

		Logger log = LoggerFactory.getLogger(OntologyFormatConversionServiceTest.class);
		IOWLParsingService parsingService = registry
				.getService(IOWLParsingService.class);
		OntologyFormatConversionService formatConverter = new OntologyFormatConversionService(log, parsingService,
				src.toString(), path.toString());
		
		long was = dest.toFile().lastModified();
		formatConverter.convertToOwl(src.toFile(), path.toFile());
		long is = dest.toFile().lastModified();
		assertEquals("Modification time was not still the same", was, is);
	}
}
