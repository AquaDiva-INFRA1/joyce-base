package de.aquadiva.joyce.base.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologyModule;

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
			@Symbol(JoyceSymbolConstants.ONTOLOGIES_DOWNLOAD_DIR) String ontologyDownloadDir) {
		this.log = log;
		this.dbService = dbService;
		this.ontologyDownloadDir = ontologyDownloadDir;
	}

	@Override
	public void printOntologyRepositoryStats(File file) {
		File ontoDir = new File(ontologyDownloadDir);
		File[] oboFiles = ontoDir.listFiles((File dir, String name) -> 
				 name.toLowerCase().endsWith(".obo") || name.toLowerCase().endsWith(".obo.gz"));
			

		oboFiles = oboFiles == null ? new File[0] : oboFiles;
		File[] umlsFiles = ontoDir.listFiles((File dir, String name) -> 
				 name.toLowerCase().endsWith(".umls") || name.toLowerCase().endsWith(".umls.gz"));
			

		umlsFiles = umlsFiles == null ? new File[0] : umlsFiles;
		File[] owlFiles = ontoDir.listFiles((File dir, String name) ->
				 name.toLowerCase().endsWith(".owl") || name.toLowerCase().endsWith(".owl.gz"));
		owlFiles = owlFiles == null ? new File[0] : owlFiles;
		
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
			FileUtils.write(file, "Total downloaded ontologies\t" + (oboFiles.length + owlFiles.length + umlsFiles.length) + "\n",
					Charset.forName("UTF-8"), false);
			FileUtils.write(file, "OWL ontologies downloaded\t" + owlFiles.length + "\n", Charset.forName("UTF-8"),
					true);
			FileUtils.write(file, "OBO ontologies downloaded\t" + oboFiles.length + "\n", Charset.forName("UTF-8"),
					true);
			FileUtils.write(file, "UMLS ontologies downloaded\t" + umlsFiles.length + "\n", Charset.forName("UTF-8"),
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
