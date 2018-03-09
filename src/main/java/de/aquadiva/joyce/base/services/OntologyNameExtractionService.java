package de.aquadiva.joyce.base.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;

public class OntologyNameExtractionService implements IOntologyNameExtractionService {

	private File ontosInfoDir;
	private File ontosDir;
	private File classNamesDir;
	private Logger log;

	public OntologyNameExtractionService(Logger log, ExecutorService executorService,
			@Symbol(JoyceSymbolConstants.ONTOLOGIES_DOWNLOAD_DIR) File ontosDir,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_INFO_DOWNLOAD_DIR) File ontosInfoDir,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_CLASSES_NAMES_DIR) File classNamesDir) {
		this.log = log;
		this.ontosDir = ontosDir;
		this.ontosInfoDir = ontosInfoDir;
		this.classNamesDir = classNamesDir;
	}

	@Override
	public void extractNames() {
		if (!classNamesDir.exists())
			classNamesDir.mkdirs();

		File toolsDir = new File("tools");
		String[] files = toolsDir.list((dir, name) -> name.contains("julielab-bioportal-ontology-tools"));
		if (files == null)
			throw new IllegalArgumentException("The JAR file of julielab-bioportal-ontology-tools could not be found. You need to start the program from within the joyce-processes/ git repository folder.");
		if (files.length == 0)
			throw new IllegalStateException(
					"julielab-bioportal-ontology-tools could not be found at " + toolsDir.getAbsolutePath());
		if (files.length > 1)
			throw new IllegalStateException(
					"julielab-bioportal-ontology-tools were found multiple times at " + toolsDir.getAbsolutePath()
							+ " which points to a duplication issue. Delete one of the libraries.");
		String jarfile = files[0];
		String[] cmdarray = new String[] {"java", "-jar", "tools/" + jarfile, "-eci", ontosDir.getAbsolutePath(),
				ontosInfoDir.getAbsolutePath(), classNamesDir.getAbsolutePath(), "false"};
		log.info("Starting external process for ontology class name extraction with the command {}", Stream.of(cmdarray).collect(Collectors.joining(" ")));
		try {
			Process process = Runtime.getRuntime().exec(cmdarray);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = br.readLine()) != null)
				log.debug(line);
			process.waitFor();
			log.info("External process finished.");
		} catch (IOException | InterruptedException e) {
			log.error("Name extraction could not be completed. Error message: {}", e.getMessage());
		}
	}

}
