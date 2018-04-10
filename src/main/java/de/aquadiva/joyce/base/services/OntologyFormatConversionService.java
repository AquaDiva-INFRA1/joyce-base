package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;

public class OntologyFormatConversionService implements IOntologyFormatConversionService {

	private File owlDir;
	private File ontoDir;
	private Logger log;
	private IOWLParsingService parsingService;

	public OntologyFormatConversionService(Logger log, IOWLParsingService parsingService,
			@Symbol(JoyceSymbolConstants.ONTOLOGIES_DOWNLOAD_DIR) String downloadDir,
			@Symbol(JoyceSymbolConstants.OWL_DIR) String owlDir) {
		this.log = log;
		this.parsingService = parsingService;
		this.owlDir = new File(owlDir);

		this.ontoDir = new File(downloadDir);
	}

	@Override
	public void convertToOwl(File sourceDir, File targetDir) {
		File[] owlFiles = sourceDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean accept = false;
				String[] dotSplit = name.split("\\.");
				// look for the valid file type ending in the later parts of the
				// file name
				for (int i = 1; i < dotSplit.length; i++) {
					String namePart = dotSplit[i];
					if (namePart.equalsIgnoreCase("owl"))
						accept = true;
				}
				return accept;
			}
		});

		File[] oboFiles = sourceDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean accept = false;
				String[] dotSplit = name.split("\\.");
				// look for the valid file type ending in the later parts of the
				// file name
				for (int i = 1; i < dotSplit.length; i++) {
					String namePart = dotSplit[i];
					if (namePart.equalsIgnoreCase("obo"))
						accept = true;
				}
				return accept;
			}
		});

		File[] umlsFiles = sourceDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean accept = false;
				String[] dotSplit = name.split("\\.");
				// look for the valid file type ending in the later parts of the
				// file name
				for (int i = 1; i < dotSplit.length; i++) {
					String namePart = dotSplit[i];
					if (namePart.equalsIgnoreCase("umls"))
						accept = true;
				}
				return accept;
			}
		});
		String[] allFiles = sourceDir.list();

		if (null != oboFiles) {
			log.info("Found {} ontologies in OBO format. Attempting to convert to OWL.",
					 oboFiles.length);
			for (int i = 0; i < oboFiles.length; i++) {
				File obofile = oboFiles[i];
				try {
					String filename = obofile.getName();
					String acronym = filename.substring(0, filename.indexOf('.'));
					File destOwlFile = new File(owlDir.getAbsolutePath() + File.separator + acronym + ".owl.gz");
					if (destOwlFile.exists()) {
						log.debug("Ontology with ID {} already exists in the destination folder and will not be converted again.", acronym);
						continue;
					}
					log.debug("Converting OBO file {} to OWL file {}.", obofile, destOwlFile);
					parsingService.convertOntology(obofile, destOwlFile);
				} catch (Exception | Error e) { //Intentionally, can be thrown
					log.error("OBO file {} could not be converted to OWL. Error message: {}", obofile, e.getMessage());
					log.debug("Exception was: ", e);
				}
			}
		} else {
			oboFiles = new File[]{};
		}

		log.info("Found {} ontologies in UMLS format. Attempting to convert to OWL.",
				null == umlsFiles ? 0 : umlsFiles.length);
		for (int i = 0; umlsFiles != null && i < umlsFiles.length; i++) {
			File umlsfile = umlsFiles[i];
			try {
				String filename = umlsfile.getName();
				String acronym = filename.substring(0, filename.indexOf('.'));
				File destOwlFile = new File(owlDir.getAbsolutePath() + File.separator + acronym + ".owl.gz");
				if (destOwlFile.exists()) {
					log.debug("Ontology with ID {} already exists in the destination folder and will not be converted again.", acronym);
					continue;
				}
				log.debug("Converting UMLS file {} to OWL file {}.", umlsfile, destOwlFile);
				parsingService.convertOntology(umlsfile, destOwlFile);
			} catch (Exception | Error e) { //Intentionally, can be thrown
				log.error("UMLS file {} could not be converted to OWL. Error message: {}", umlsfile, e.getMessage());
				log.debug("Exception was: ", e);
			}
		}

		if (null == owlFiles) {
			return;
		}
		log.info(
				"Found {} ontologies in OWL format. They are converted to the same RDF/XML OWL format as ontologies of other formats.",
				 owlFiles.length);
		for (int i = 0; i < owlFiles.length; i++) {
			File owlfile = owlFiles[i];
			try {
				String filename = owlfile.getName();
				String acronym = filename.substring(0, filename.indexOf('.'));
				File destOwlFile = new File(owlDir.getAbsolutePath() + File.separator + acronym + ".owl.gz");
				if (destOwlFile.exists()) {
					log.debug("Ontology with ID {} already exists in the destination folder and will not be converted again.", acronym);
					continue;
				}
				log.debug("Converting OWL file {} to OWL RDF/XML file {}.", owlfile, destOwlFile);
				parsingService.convertOntology(owlfile, destOwlFile);
			} catch (Exception | Error e) { //Intentionally, can be thrown
				log.error("OWL file {} could not be converted to OWL RDF/XML. Error message: {}", owlfile, e.getMessage());
				log.debug("Exception was: ", e);
			}
		}

		log.info("{} ontologies were neither in OWL nor in in OBO format and are currently not supported.",
				(allFiles.length - (owlFiles.length + oboFiles.length)));
	}

	@Override
	public void convertFromDownloadDirToOwlDir() {
		convertToOwl(ontoDir, owlDir);
	}

}
