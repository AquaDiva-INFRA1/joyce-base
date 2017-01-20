package de.aquadiva.joyce.application;

import java.io.File;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import de.aquadiva.joyce.base.services.IOntologyRepositoryStatsPrinterService;
import de.aquadiva.joyce.base.services.JoyceBaseModule;

public class OntologyRepositoryStatsPrinterApplication {

	public static void main(String[] args) {
		Registry registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);
		IOntologyRepositoryStatsPrinterService service = registry
				.getService(IOntologyRepositoryStatsPrinterService.class);
		service.printOntologyRepositoryStats(new File("ontologystats.tsv"));

		registry.shutdown();
	}

}
