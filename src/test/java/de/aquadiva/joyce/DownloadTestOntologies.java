package de.aquadiva.joyce;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.Ignore;
import org.junit.Test;

import de.aquadiva.joyce.base.services.IOntologyDownloadService;
import de.aquadiva.joyce.base.services.IOntologyFormatConversionService;
import de.aquadiva.joyce.base.services.JoyceBaseModule;

/**
 * A helper class to download the ontologies from BioPortal our tests work with.
 * 
 * Note that you have to deliver your BioPortal API key by setting the Java
 * system property 'joyce.bioportal.apikey' to the corresponding value. You can
 * do this by entering your key in the outcommented System.setProperty() line
 * below and commenting it in, or by adding the key to the marked place in
 * src/test/resources/configuration.properties. JUST BE SURE NOT TO UPLOAD YOUR
 * KEY BACK INTO THE VERSION CONTROL SYSTEM
 * 
 * PLEASE NOTE Running this class will probably break some tests that expect a
 * certain number of classes in the test ontologies. To download them anew might
 * bring more classes. In this case, just change the number of expected classes
 * to fix the tests.
 * 
 * @author faessler
 * 
 */
public class DownloadTestOntologies {

	// We normally ignore this test so not to let it run automatically and
	// potentially break tests. Comment this out for running the class and then
	// run this class as a JUnit test if you want to download the test ontologies
	// anew.
	@Ignore
	@Test
	public void testDownloadOntologies() {
		// System.setProperty(JoyceSymbolConstants.BIOPORTAL_API_KEY, "<YOUR API KEY>");
		Registry registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);
		IOntologyDownloadService downloadService = registry.getService(IOntologyDownloadService.class);
		downloadService.downloadBioPortalOntologiesToConfigDirs("GRO", "BCO", "OBI");

		IOntologyFormatConversionService conversionService = registry
				.getService(IOntologyFormatConversionService.class);
		conversionService.convertFromDownloadDirToOwlDir();
	}

}
