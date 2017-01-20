package de.aquadiva.joyce;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import de.aquadiva.joyce.base.services.IOntologyDownloadService;
import de.aquadiva.joyce.base.services.JoyceBaseModule;

/**
 * A helper class to download the ontologies from BioPortal our tests work with.
 * 
 * Note that you have to deliver your BioPortal API key by setting the Java
 * system property 'joyce.bioportal.apikey' to the corresponding value.
 * 
 * @author faessler
 * 
 */
public class DownloadTestOntologies {

	public static void main(String[] args) {
		Registry registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);
		IOntologyDownloadService downloadService = registry.getService(IOntologyDownloadService.class);
		downloadService.downloadBioPortalOntologiesToConfigDirs("GRO", "BCO", "ENVO", "OBI");
	}

}
