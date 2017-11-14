package de.aquadiva.joyce.base.services;

import java.io.IOException;

import org.neo4j.shell.util.json.JSONException;

import de.aquadiva.joyce.base.util.MetaConceptMapCreationException;

public interface INeo4jService {
	void insertClasses() throws MetaConceptMapCreationException;
	void insertMappings() throws MetaConceptMapCreationException;
	void createMetaClassesInDatabase() throws MetaConceptMapCreationException;
	/**
	 * <p>
	 * The outcoming mapping file looks like this:
	 * 
	 * <pre>
	 * atid0   http://purl.obolibrary.org/obo/GO_0042493||http://www.bootstrep.eu/ontology/GRO#ResponseToDrug
	 * atid1   http://www.bootstrep.eu/ontology/GRO#ResponseToChemicalStimulus||http://purl.obolibrary.org/obo/GO_0042221
	 * atid2   http://www.bootstrep.eu/ontology/GRO#TranscriptionCofactorActivity||http://purl.obolibrary.org/obo/GO_0003712
	 * </pre>
	 * 
	 * and aggregates ontology classes that have been mapped to each other into
	 * a single meta class ID. Thus, this is a "meta class to class IRIs"
	 * mapping file.
	 * </p>
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	void exportMetaClassToIriMappingFile() throws MetaConceptMapCreationException;
	void exportLingpipeDictionary() throws MetaConceptMapCreationException;
}
