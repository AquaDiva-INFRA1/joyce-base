package de.aquadiva.ontologyselection.base.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import de.aquadiva.ontologyselection.OSSymbolConstants;
import de.aquadiva.ontologyselection.base.data.Ontology;

/**
 * This service reads - not creates - the ontology class IRI to meta class ID
 * mapping used to assemble equivalent classes into one single representant.
 * 
 * @author faessler
 *
 */
public class MetaConceptService implements IMetaConceptService {

	private Logger log;
	/**
	 * <p>
	 * IRIClass -> MetaClass
	 * </p>
	 */
	private Map<String, String> iriClass2MetaClassMapping;
	/**
	 * <p>
	 * MixedClass -> [Module1, Ontology1, Ontology2, Module2,...]
	 * </p>
	 */
	private Multimap<String, String> mixedClassToModuleMapping;
	private Multimap<String, String> metaClass2IriClassMapping;
	private File mixedClassOntologyMappingFile;

	public MetaConceptService(Logger log,
			@Symbol(OSSymbolConstants.META_CLASS_TO_IRI_CLASS_MAPPING) File metaConceptMappingFile,
			@Symbol(OSSymbolConstants.MIXEDCLASS_ONTOLOGY_MAPPING) File mixedClassOntologyMappingFile)
					throws IOException {
		this.log = log;
		this.mixedClassOntologyMappingFile = mixedClassOntologyMappingFile;
		metaClass2IriClassMapping = readMetaClass2IriClassMapping(metaConceptMappingFile);
		iriClass2MetaClassMapping = readInversedMetaClass2IriClassMapping(metaConceptMappingFile);
		if (mixedClassOntologyMappingFile.exists())
			mixedClassToModuleMapping = readMixedClassToModuleMapping(mixedClassOntologyMappingFile);

	}

	@Override
	public Set<String> getMixedClassIdsForOntology(Ontology o) {
		OWLOntology owl = o.getOwlOntology();
		Set<String> mixedClassesInModule = new HashSet<>();
		for (OWLClass c : owl.getClassesInSignature()) {
			// we are only interested in asserted classes for the time being
			if (c.isAnonymous())
				continue;
			String iri = c.getIRI().toString();
			String metaConceptId = iriClass2MetaClassMapping.get(iri);
			if (null != metaConceptId)
				mixedClassesInModule.add(metaConceptId);
			else
				mixedClassesInModule.add(iri);
		}
		return mixedClassesInModule;
	}

	@Override
	public Map<String, List<String>> getOntology2IriClassMapping(Multiset<String> mixedClasses) {
		if (null == mixedClassToModuleMapping && mixedClassOntologyMappingFile.exists()) {
			try {
				mixedClassToModuleMapping = readMixedClassToModuleMapping(mixedClassOntologyMappingFile);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else if (null == mixedClassToModuleMapping){
			log.error("Mapping file {} could not be found.", mixedClassOntologyMappingFile);
		}
		Map<String, List<String>> ret = new HashMap<>();
		for (String mixedClassId : mixedClasses) {
			for (String ontologyId : mixedClassToModuleMapping.get(mixedClassId)) {
				List<String> ontologyConceptList = ret.get(ontologyId);
				if (null == ontologyConceptList) {
					ontologyConceptList = new ArrayList<>();
					ret.put(ontologyId, ontologyConceptList);
				}
				Collection<String> iriClasses = metaClass2IriClassMapping.get(mixedClassId);
				// if the mapped value is null, mixedClassId was already an
				// IriClass
				if (null == iriClasses) {
					ontologyConceptList.add(mixedClassId);
				} else {
					for (String iriClass : iriClasses)
						ontologyConceptList.add(iriClass);
				}
			}
		}
		return ret;
	}

	@Override
	public Multiset<String> getOntologiesForMixedClasses(Multiset<String> concepts) {
		Multiset<String> returnIds = HashMultiset.create();
		for (String conceptId : concepts) {
			for (String moduleId : mixedClassToModuleMapping.get(conceptId)) {
				returnIds.add(moduleId);
			}
		}
		return returnIds;
	}

	/**
	 * Returns a mapping FROM class IRIs TO meta class IDs (IRIClass ->
	 * MetaClass). Classes not contained in the mapping just do not have other,
	 * equivalent classes.
	 * 
	 * @param metaConceptMappingFile
	 * @return
	 */
	private Map<String, String> readInversedMetaClass2IriClassMapping(File metaConceptMappingFile) {
		Map<String, String> mapping = new HashMap<>();
		if (!metaConceptMappingFile.exists()) {
			log.warn(
					"Meta concept mapping file could not be found at configured path {}. Meta concepts in the dictionary will be removed.",
					metaConceptMappingFile);
			return mapping;
		}
		try (InputStream is = new GZIPInputStream(new FileInputStream(metaConceptMappingFile))) {
			LineIterator lineIterator = IOUtils.lineIterator(is, "UTF-8");
			while (lineIterator.hasNext()) {
				// format is:
				// metaConceptId<tab>classIri1||classIri2||classIri3||...
				String line = lineIterator.nextLine();
				// comment?
				if (line.startsWith("#"))
					continue;
				String[] tabSplit = line.split("\\t");
				String metaId = tabSplit[0];
				String[] classIris = tabSplit[1].split("\\|\\|");
				for (int i = 0; i < classIris.length; i++) {
					String classIri = classIris[i];
					mapping.put(classIri, metaId);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapping;
	}

	/**
	 * Returns a mapping FROM MetaClass TO IriClass (MetaClass -> IriClass).
	 * 
	 * @param metaConceptMappingFile
	 * @return
	 */
	private Multimap<String, String> readMetaClass2IriClassMapping(File metaConceptMappingFile) {
		Multimap<String, String> mapping = HashMultimap.create();
		if (!metaConceptMappingFile.exists()) {
			log.warn(
					"Meta concept mapping file could not be found at configured path {}. Meta concepts in the dictionary will be removed.",
					metaConceptMappingFile);
			return mapping;
		}
		try (InputStream is = new GZIPInputStream(new FileInputStream(metaConceptMappingFile))) {
			LineIterator lineIterator = IOUtils.lineIterator(is, "UTF-8");
			while (lineIterator.hasNext()) {
				// format is:
				// metaConceptId<tab>classIri1||classIri2||classIri3||...
				String line = lineIterator.nextLine();
				// comment?
				if (line.startsWith("#"))
					continue;
				String[] tabSplit = line.split("\\t");
				String metaId = tabSplit[0];
				String[] classIris = tabSplit[1].split("\\|\\|");
				for (int i = 0; i < classIris.length; i++) {
					String classIri = classIris[i];
					mapping.put(metaId, classIri);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapping;
	}

	private Multimap<String, String> readMixedClassToModuleMapping(File classOntologyMappingFile) throws IOException {
		Multimap<String, String> map = HashMultimap.create();
		try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(classOntologyMappingFile))) {
			LineIterator lineIterator = IOUtils.lineIterator(is, "UTF-8");
			while (lineIterator.hasNext()) {
				String line = lineIterator.nextLine();
				String[] split = line.split("\\t");
				String classId = split[0];
				String[] moduleIds = split[1].split("\\|\\|");
				for (int i = 0; i < moduleIds.length; i++) {
					String moduleId = moduleIds[i];
					map.put(classId, moduleId);
				}
			}
		}
		return map;
	}

	@Override
	public Set<String> convertMixedClassesToIriClasses(Multiset<String> mixedClasses) {
		Set<String> iriClasses = new HashSet<>(mixedClasses.size());
		for (String mixedClass : mixedClasses) {
			Collection<String> mappedIriClasses = metaClass2IriClassMapping.get(mixedClass);
			if (null == mappedIriClasses) {
				iriClasses.add(mixedClass);
			} else {
				for (String iriClass : mappedIriClasses) {
					iriClasses.add(iriClass);
				}
			}
		}
		return iriClasses;
	}
}
