package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;

public class OntologyFormatConversionService implements IOntologyFormatConversionService {

	private File owlDir;
	private File ontoDir;
	private Logger log;
	private IOWLParsingService parsingService;

	public OntologyFormatConversionService(Logger log, IOWLParsingService parsingService,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_DOWNLOAD_DIR) String downloadDir,
			@Symbol(JoyceSymbolConstants.OWL_DIR) String owlDir) {
		this.log = log;
		this.parsingService = parsingService;
		this.owlDir = new File(owlDir);

		this.ontoDir = new File(downloadDir + File.separator + OntologyDownloadService.ONTO_DIR);
	}

	@Override
	public void convertToOwl(File sourceDir, File targetDir) {
		File[] owlFiles = sourceDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean accept = false;
				String[] dotSplit = name.split("\\.");
				// look for the valid file type ending in the later parts of the file name
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
				// look for the valid file type ending in the later parts of the file name
				for (int i = 1; i < dotSplit.length; i++) {
					String namePart = dotSplit[i];
					if (namePart.equalsIgnoreCase("obo"))
						accept = true;
				}
				return accept;
			}
		});
		String[] allFiles = sourceDir.list();
		
		log.info("Found {} ontologies in OBO format. Attempting to convert to OWL.", null == oboFiles ? 0 : oboFiles.length);
		for (int i = 0; i < oboFiles.length; i++) {
			File obofile = oboFiles[i];
			try {
				String filename = obofile.getName();
				String acronym = filename.substring(0, filename.indexOf('.'));
				File destOwlFile = new File(owlDir.getAbsolutePath() + File.separator + acronym + ".owl.gz");
				log.debug("Converting OBO file {} to OWL file {}.", obofile, destOwlFile);
				parsingService.convertOntology(obofile, destOwlFile);
			} catch (Exception | Error e) {
				log.error("OBO file {} could not be converted to OWL. Error message: {}", obofile, e.getMessage());
				log.debug("Exception was: ", e);
			}
		}

		log.info("Copying {} OWL ontologies from {} to {}.", new Object[] {owlFiles == null ? 0 : owlFiles.length, ontoDir, owlDir});
		int copied = 0;
		for (int i = 0; null != owlFiles && i < owlFiles.length; i++) {
			File sourceFile = owlFiles[i];
			File destFile = new File(owlDir.getAbsolutePath() + File.separator + sourceFile.getName());
			try {
				FileUtils.copyFile(sourceFile, destFile);
				++copied;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("{} OWL ontologies sucessfully copied.", copied);
		log.info("{} ontologies were neither in OWL nor in in OBO format and are currently not supported.", (allFiles.length-(owlFiles.length+oboFiles.length))); 
	}

	@Override
	public void convertFromDownloadDirToOwlDir() {
		convertToOwl(ontoDir, owlDir);
	}

}
