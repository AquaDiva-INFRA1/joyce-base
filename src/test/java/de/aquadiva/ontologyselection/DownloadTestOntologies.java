package de.aquadiva.ontologyselection;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import de.aquadiva.ontologyselection.base.services.IOntologyDownloadService;
import de.aquadiva.ontologyselection.base.services.OSBaseModule;

/**
 * A helper class to download the ontologies from BioPortal our tests work with.
 * 
 * @author faessler
 * 
 */
public class DownloadTestOntologies {

	public static void main(String[] args) {
		Registry registry = RegistryBuilder.buildAndStartupRegistry(OSBaseModule.class);
		IOntologyDownloadService downloadService = registry.getService(IOntologyDownloadService.class);
		downloadService.downloadBioPortalOntologiesToConfigDirs("GRO", "BCO", "ENVO", "OBI");
//		downloadService.downloadBioPortalOntologiesToConfigDirs("GRO");
	}

}
