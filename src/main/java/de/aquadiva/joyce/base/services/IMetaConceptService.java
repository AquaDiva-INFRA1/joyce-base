package de.aquadiva.joyce.base.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.shell.util.json.JSONException;

import com.google.common.collect.Multiset;

import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.util.MetaConceptMapCreationException;

public interface IMetaConceptService {


	/**
	 * Returns the IRIs of asserted classes in ontology <tt>o</tt> with respect
	 * to the <tt>metaConceptMapping</tt>. The result are the class IRIs of
	 * <tt>o</tt> for those classes that are not included in the meta concept
	 * mapping - and thus are not mapped to other classes as being equal - and
	 * meta class IDs for those asserted ontology classes that have been mapped
	 * to a meta class.
	 * 
	 * @param o
	 * @param metaConceptMapping
	 * @return
	 */
	Set<String> getMixedClassIdsForOntology(Ontology o);

	/**
	 * Returns a map containing for each ontology, that has at least one class
	 * in <tt>mixedClasses</tt> the list of all IriClasses (no MetaClasses!)
	 * that are either included directly in <tt>mixedClasses</tt> or implicitly
	 * by MetaClasses therein.
	 * 
	 * @param mixedClasses
	 * @return
	 */
	Map<String, List<String>> getOntology2IriClassMapping(Multiset<String> mixedClasses);

	/**
	 * Returns those ontology IDs where at least one IriClass or MetaClass from
	 * <tt>mixedClasses</tt> is included in the respective ontology.
	 * 
	 * @param mixedClasses
	 * @return
	 */
	Multiset<String> getOntologiesForMixedClasses(Multiset<String> mixedClasses);

	Set<String> convertMixedClassesToIriClasses(Multiset<String> mixedClasses);

	/**
	 * Loads the meta class to class IRI mapping file that needs to be generated
	 * by {@link #createMetaConceptMap()} before. This creates in in-memory map
	 * of the the meta-IRI mapping and also a IRI-meta mapping.
	 * 
	 * @throws IOException
	 */
	void loadMetaClassIriMaps() throws IOException;
}
