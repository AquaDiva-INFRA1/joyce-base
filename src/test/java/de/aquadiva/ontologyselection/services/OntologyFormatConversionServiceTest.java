package de.aquadiva.ontologyselection.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.OSSymbolConstants;
import de.aquadiva.ontologyselection.base.services.IOntologyFormatConversionService;
import de.aquadiva.ontologyselection.base.services.OSBaseModule;

public class OntologyFormatConversionServiceTest {
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
	public void testCopyOWLOntologies() throws IOException {
		IOntologyFormatConversionService formatConverter = registry
				.getService(IOntologyFormatConversionService.class);

		SymbolSource symbolSource = registry.getService(SymbolSource.class);
		String owlDirPath = symbolSource
				.valueForSymbol(OSSymbolConstants.OWL_DIR);

		File owlDir = new File(owlDirPath);

		// deletes the target directory so at the end of the test we can be sure
		// the data there was created by the
		// current test run and not by an old test run
		FileUtils.deleteDirectory(owlDir);

		formatConverter.convertFromDownloadDirToOwlDir();

		String[] owlFiles = owlDir.list();
		assertNotNull(owlFiles);
		assertEquals(4, owlFiles.length);
	}
}
