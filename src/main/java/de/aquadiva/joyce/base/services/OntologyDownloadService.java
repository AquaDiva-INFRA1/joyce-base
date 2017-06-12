package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.http.ParseException;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.julielab.bioportal.ontologies.DownloadStats;
import de.julielab.bioportal.ontologies.MappingDownloader;
import de.julielab.bioportal.ontologies.OntologyDownloader;
import de.julielab.bioportal.util.BioPortalOntologyToolsException;

public class OntologyDownloadService implements IOntologyDownloadService {
	public static final String REST_URL_BIOPORTAL = "http://data.bioontology.org";
	public static final String JSON_DIR = "meta-json";
	public static final String ONTO_DIR = "ontologies";
	/**
	 * File name part identifying a JSON meta information file as being the
	 * submission meta data.
	 */
	public static final String SUBMISSION = "submission";
	public static final String SUBMISSIONS = "submissions";
	public static final String PROJECTS = "projects";
	public static final String ANALYTICS = "analytics";
	private File jsonDir;
	private File ontoDir;
	private Logger log;
	private OntologyDownloader ontologyDownloader;
	private MappingDownloader mappingDownloader;
	private File mappingsDownloadDir;

	public OntologyDownloadService(Logger log,
			@Symbol(JoyceSymbolConstants.ONTOLOGIES_DOWNLOAD_DIR) String ontologyDownloadDir,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_INFO_DOWNLOAD_DIR) String ontologyInfoDir,
			@Symbol(JoyceSymbolConstants.MAPPINGS_DOWNLOAD_DIR) File mappingsDownloadDir,
			@Symbol(JoyceSymbolConstants.BIOPORTAL_API_KEY) String apiKey,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_DOWNLOAD_ERROR_FILE) File errorFile) {

		this.mappingsDownloadDir = mappingsDownloadDir;
		ontologyDownloader = new OntologyDownloader(apiKey);
		mappingDownloader = new MappingDownloader(apiKey);
		this.log = log;

		this.ontoDir = new File(ontologyDownloadDir);
		this.jsonDir = new File(ontologyInfoDir);

		if (!jsonDir.exists())
			jsonDir.mkdirs();
		if (!ontoDir.exists())
			ontoDir.mkdirs();
	}

	@Override
	public void downloadBioPortalOntologies(File downloadDir, File jsonMetaDir, String... requestedAcronyms) {
		try {
			DownloadStats downloadStats = ontologyDownloader.downloadOntologies(ontoDir, jsonDir,
					new HashSet<>(Arrays.asList(requestedAcronyms)));
			FileUtils.write(new File("ontologyDownloadReport.txt"), downloadStats.report() + "\n", "UTF-8", false);
		} catch (ParseException | IOException | BioPortalOntologyToolsException | InterruptedException
				| ExecutionException e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void downloadBioPortalOntologiesToConfigDirs(String... validAcronyms) {
		downloadBioPortalOntologies(ontoDir, jsonDir, validAcronyms);
	}

	@Override
	public void downloadBioPortalMappingsToConfigDirs(String... requestedAcronyms) {
		try {
			mappingDownloader.downloadOntologyMappings(mappingsDownloadDir, ontoDir,
					Stream.of(requestedAcronyms).collect(Collectors.toSet()));
		} catch (ParseException | IOException | BioPortalOntologyToolsException e) {
			e.printStackTrace();
		}
	}

}
