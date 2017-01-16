package de.aquadiva.ontologyselection.base.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.ontologyselection.JoyceSymbolConstants;
import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.data.OntologyModule;

/**
 * Simply writes a few statistics of the ontology database to file.
 * 
 * @author faessler
 *
 */
public class OntologyRepositoryStatsPrinterService implements IOntologyRepositoryStatsPrinterService {

	private Logger log;
	private IOntologyDBService dbService;
	private String ontologyDownloadDir;

	public OntologyRepositoryStatsPrinterService(Logger log, IOntologyDBService dbService,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_DOWNLOAD_DIR) String ontologyDownloadDir) {
		this.log = log;
		this.dbService = dbService;
		this.ontologyDownloadDir = ontologyDownloadDir;
	}

	@Override
	public void printOntologyRepositoryStats(File file) {
		File ontoDir = new File(ontologyDownloadDir + File.separator + OntologyDownloadService.ONTO_DIR);
		File[] oboFiles = ontoDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".obo") || name.toLowerCase().endsWith(".obo.gz");
			}

		});
		File[] owlFiles = ontoDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".owl") || name.toLowerCase().endsWith(".owl.gz");
			}
		});

		List<Ontology> fullOntos = dbService.getAllOntologies();
		List<Ontology> parsingErrorsFullOntos = dbService.getOntologiesWithParsingError();
		List<Ontology> modularizationErrors = dbService.getAllOntologiesWithModularizationError();
		List<OntologyModule> modules = dbService.getAllOntologyModules();
		List<OntologyModule> parsingErrorsModules = dbService.getModulesWithParsingError();
		List<Ontology> usableOntos = new ArrayList<>();
		List<Ontology> usableModules = new ArrayList<>();
		
		for (Ontology o : fullOntos) {
			if (!o.getHasParsingError() && !o.getClassIds().isEmpty())
				usableOntos.add(o);
		}
		for (OntologyModule om : modules) {
			if (!om.getHasParsingError() && !om.getClassIds().isEmpty())
				usableModules.add(om);
		}
		
		double meanNumOntoClasses = getMeanNumberOfClasses(usableOntos);
		double varianceOntoClasses = getClassNumberVariance(usableOntos, meanNumOntoClasses);
		double stdDeviationOntoClasses = Math.sqrt(varianceOntoClasses);
		
		double meanNumModuleClasses = getMeanNumberOfClasses(usableModules);
		double varianteModuleClasses = getClassNumberVariance(usableModules, meanNumModuleClasses);
		double stdDeviationModuleClasses = Math.sqrt(varianteModuleClasses);

		log.debug("Writing ontology repository statistics to {}", file);
		try (OutputStream os = new FileOutputStream(file)) {
			FileUtils.write(file, "Total downloaded ontologies\t" + (oboFiles.length + owlFiles.length) + "\n",
					Charset.forName("UTF-8"), false);
			FileUtils.write(file, "OWL ontologies downloaded\t" + owlFiles.length + "\n", Charset.forName("UTF-8"),
					true);
			FileUtils.write(file, "OBO ontologies downloaded\t" + oboFiles.length + "\n", Charset.forName("UTF-8"),
					true);

			FileUtils.write(file, "Total number of ontologies plus cluster based modules in the database\t"
					+ (modules.size() + fullOntos.size()) + "\n", Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Number of original ontologies in the database\t" + fullOntos.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Number of cluster based modules in the database\t" + modules.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file,
					"Number of original ontologies with parsing errors\t" + parsingErrorsFullOntos.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file,
					"Number of original ontologies with modularization errors\t" + modularizationErrors.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Number of modules with parsing errors\t" + parsingErrorsModules.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Number of full ontologies without parsing errors and with at least one class\t" + usableOntos.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Number of modules without parsing errors and with at least one class\t" + usableModules.size() + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Mean number of classes in full ontologies\t" + meanNumOntoClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Variance of classes in full ontologies\t" + varianceOntoClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Std deviation of classes in full ontologies\t" + stdDeviationOntoClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Mean number of classes in modules\t" + meanNumModuleClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Variance of classes in modules\t" + varianteModuleClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "Std deviation of classes in modules\t" + stdDeviationModuleClasses + "\n",
					Charset.forName("UTF-8"), true);
			FileUtils.write(file, "All available ontologies from BioPortal without parsing errors for which we have at least one class:\t",
					Charset.forName("UTF-8"), true);
			for (Ontology o : fullOntos) {
				if (o.getClassIds().isEmpty())
					System.out.println(o.getId() + " doesn't have any classes.");
				if (!o.getHasParsingError() && !o.getClassIds().isEmpty())
					FileUtils.write(file, o.getId() + "\n", Charset.forName("UTF-8"), true);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double getMeanNumberOfClasses(Collection<Ontology> ontologies) {
		double numClasses = 0d;
		for (Ontology o : ontologies) {
			numClasses += o.getClassIds().size();
		}
		return numClasses / ontologies.size();
	}
	
	private double getClassNumberVariance(Collection<Ontology> ontologies, double meanNumClasses) {
		double squaredDifferenceSum = 0;
		for (Ontology o : ontologies) {
			squaredDifferenceSum += Math.pow(o.getClassIds().size() - meanNumClasses, 2);
		}
		return squaredDifferenceSum / ontologies.size();
	}

}
