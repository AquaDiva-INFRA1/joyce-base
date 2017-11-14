package de.aquadiva.joyce.base.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.shell.util.json.JSONException;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.aquadiva.joyce.base.util.MetaConceptMapCreationException;
import de.aquadiva.neo4j.plugins.AquaDivaExport;
import de.julielab.bioportal.ontologies.data.OntologyClass;
import de.julielab.bioportal.ontologies.data.OntologyClassMapping;
import de.julielab.bioportal.util.BioPortalToolUtils;
import de.julielab.java.utilities.FileUtilities;
import de.julielab.neo4j.plugins.ConceptManager;
import de.julielab.neo4j.plugins.FacetManager.FacetLabel;
import de.julielab.neo4j.plugins.constants.semedico.FacetConstants;
import de.julielab.neo4j.plugins.datarepresentation.ConceptCoordinates;
import de.julielab.neo4j.plugins.datarepresentation.ImportConcept;
import de.julielab.neo4j.plugins.datarepresentation.ImportFacet;
import de.julielab.neo4j.plugins.datarepresentation.ImportFacetGroup;
import de.julielab.neo4j.plugins.datarepresentation.ImportMapping;
import de.julielab.neo4j.plugins.datarepresentation.JsonSerializer;

public class Neo4jService implements INeo4jService {

	private static final String MAPPINGS_INSERTED_PROP = "mappingsInserted";

	private GraphDatabaseService graphDb;
	private Logger log;
	private File ontologyNamesDirectory;
	private File mappingsDir;

	private File metaConceptMappingFile;

	private File dictPath;

	public Neo4jService(Logger log, @Symbol(JoyceSymbolConstants.NEO4J_PATH) File neo4jDbDirectory,
			@Symbol(JoyceSymbolConstants.ONTOLOGY_CLASSES_NAMES_DIR) File ontologyNamesDirectory,
			@Symbol(JoyceSymbolConstants.MAPPINGS_DOWNLOAD_DIR) File mappingsDir,
			@Symbol(JoyceSymbolConstants.META_CLASS_TO_IRI_CLASS_MAPPING) File metaConceptMappingFile, @Symbol(JoyceSymbolConstants.DICT_FILTERED_PATH) File dictPath) {
		this.log = log;
		this.ontologyNamesDirectory = ontologyNamesDirectory;
		this.mappingsDir = mappingsDir;
		this.metaConceptMappingFile = metaConceptMappingFile;
		this.dictPath = dictPath;
		GraphDatabaseFactory factory = new GraphDatabaseFactory();
		graphDb = factory.newEmbeddedDatabase(neo4jDbDirectory);
	}

	@Override
	public void insertClasses() throws MetaConceptMapCreationException {
		Gson gson = new Gson();
		ConceptManager cm = new ConceptManager();
		File[] ontologyNameFiles = ontologyNamesDirectory
				.listFiles((f, n) -> n.endsWith(".jsonlst") || n.endsWith(".jsonlst.gz"));
		// INSERT ONTOLOGY CLASSES INTO GRAPH DB
		log.info("Inserting {} ontology class files into the embedded Neo4j database.", ontologyNameFiles.length);
		for (int i = 0; i < ontologyNameFiles.length; i++) {
			File f = ontologyNameFiles[i];
			String acronym = BioPortalToolUtils.getAcronymFromFileName(f);
			boolean alreadyExists = false;
			try (Transaction tx = graphDb.beginTx()) {
				Node facetNode = graphDb.findNode(FacetLabel.FACET, FacetConstants.PROP_NAME, acronym);
				alreadyExists = facetNode != null;
				tx.success();
			}
			if (!alreadyExists) {
				try {
					log.trace("Inserting the classes of file {} into the Neo4j database", f);
					// The format of the name files is one class per line as
					// a JSON object on its own. We will now build a JSON
					// array out of all the classes
					// of the file
					BufferedReader br = FileUtilities.getReaderFromFile(f);
					// Convert the JSON lines to OntologyClass objects
					Stream<OntologyClass> classStream = br.lines().map(l -> gson.fromJson(l, OntologyClass.class));
					// Convert the OntologyClass objects to ImportConcepts
					Stream<ImportConcept> conceptStream = classStream.map(c -> {
						List<ConceptCoordinates> parentCoordinates = Collections.emptyList();
						if (c.parents != null && c.parents.parents != null)
							parentCoordinates = c.parents.parents.stream()
									.map(p -> new ConceptCoordinates(p, acronym, true)).collect(Collectors.toList());
						return new ImportConcept(c.prefLabel, c.synonym.synonyms, c.definition,
								new ConceptCoordinates(c.id, acronym, true), parentCoordinates);
					});
					List<ImportConcept> concepts = conceptStream.collect(Collectors.toList());
					String termsJson = JsonSerializer.toJson(concepts);
					// Facet groups are unique by name in the database (the
					// ConceptManager makes sure of it). Thus, we will have
					// a single facet group with the following name after
					// the import
					// of all ontology classes.
					ImportFacetGroup fg = new ImportFacetGroup("BioPortal Ontologies");
					ImportFacet facet = new ImportFacet(BioPortalToolUtils.getAcronymFromFileName(f), "go",
							FacetConstants.SRC_TYPE_HIERARCHICAL, Arrays.asList("none"), Arrays.asList("none"), 0,
							Arrays.asList("none"), fg);

					String facetJson = JsonSerializer.toJson(facet);
					cm.insertFacetTerms(graphDb, facetJson, termsJson, null);
				} catch (IOException e) {
					throw new MetaConceptMapCreationException(
							"The ontology name file " + f.getAbsolutePath() + " could not be read", e);
				} catch (JSONException e) {
					throw new MetaConceptMapCreationException(
							"The JSON format specifying the ontology class names or - but less probable - the facet JSON format does not fit the requirements of the employed version of the julielab-neo4j-plugin-concepts dependency. There might be a compatibility issue between the julielab-bioportal-tools and the plugin-concepts libraries.",
							e);
				}
			} else {
				// ontology facet node was found
				log.debug("Ontology with ID {} already exists in the database and is not inserted again.", acronym);
			}
		}

	}

	@Override
	public void insertMappings() throws MetaConceptMapCreationException {
		// INSERT CLASS MAPPINGS INTO GRAPH DB
		Gson gson = new Gson();
		ConceptManager cm = new ConceptManager();
		File[] mappingFiles = mappingsDir.listFiles((f, n) -> n.endsWith(".json") || n.endsWith(".json.gz"));
		Type mappingListType = new TypeToken<List<OntologyClassMapping>>() {//
		}.getType();
		log.info("Inserting {} ontology class mapping files into an embedded Neo4j database.", mappingFiles.length);
		for (int i = 0; i < mappingFiles.length; i++) {
			File f = mappingFiles[i];
			String acronym = BioPortalToolUtils.getAcronymFromFileName(f);
			Node facetNode = null;
			try (Transaction tx = graphDb.beginTx()) {
				facetNode = graphDb.findNode(FacetLabel.FACET, FacetConstants.PROP_NAME, acronym);

				if (facetNode != null && (!facetNode.hasProperty(MAPPINGS_INSERTED_PROP)
						|| !((boolean) facetNode.getProperty(MAPPINGS_INSERTED_PROP)))) {
					log.trace("Inserting the mappings of file {} into the Neo4j database", f);
					try (Reader r = FileUtilities.getReaderFromFile(f)) {
						List<OntologyClassMapping> mappings = gson.fromJson(r, mappingListType);
						List<ImportMapping> toInsert = new ArrayList<>(mappings.size());
						for (OntologyClassMapping mapping : mappings) {
							// for the moment, we only work with LOOM (that
							// does
							// not
							// mean that this is the best strategy, we
							// simply
							// haven't investigated other possibilities)
							if (!mapping.source.equalsIgnoreCase("LOOM"))
								continue;
							String from = mapping.classes.get(0).id;
							String to = mapping.classes.get(1).id;
							ImportMapping importMapping = new ImportMapping(from, to, mapping.source);
							toInsert.add(importMapping);
						}
						cm.insertMappings(graphDb, JsonSerializer.toJson(toInsert));
						facetNode.setProperty(MAPPINGS_INSERTED_PROP, true);
					} catch (IOException e) {
						throw new MetaConceptMapCreationException(
								"The ontology class mapping JSON file " + f.getAbsolutePath() + "could not be read.",
								e);
					} catch (JSONException e) {
						throw new MetaConceptMapCreationException(
								"The JSON format that was sent to the insertMappings method of the ConceptManager did not match the expected format. There might be a compatibility issue between the julielab-bioportal-tools and the plugin-concepts libraries.");
					}
				} else {
					if (facetNode == null)
						log.trace(
								"Mappings for ontology {} are not inserted because the ontology is not present in the graph database",
								acronym);
					else
						log.debug(
								"Mappings for ontology {} are not inserted because they already have been inserted before.",
								acronym);
				}
				tx.success();
			}

		}

	}

	@Override
	public void createMetaClassesInDatabase() throws MetaConceptMapCreationException {
		ConceptManager cm = new ConceptManager();
		try {
			cm.buildAggregatesByMappigs(graphDb, "[LOOM]", "MAPPING_AGGREGATE", null);
		} catch (JSONException e) {
			throw new MetaConceptMapCreationException(e);
		}
	}

	@Override
	public void exportMetaClassToIriMappingFile() throws MetaConceptMapCreationException {
		AquaDivaExport ade = new AquaDivaExport();
		try {
			log.info("Retrieving mapping file data from the prepared Neo4j database.");
			// This string is the base 64 encoding of the GZIPed mapping
			// file
			String metaClassMapping = ade.exportAggregateElementMapping(graphDb);
			log.info("Writing the meta class mapping to {}", metaConceptMappingFile);
			byte[] mappingBytes = DatatypeConverter.parseBase64Binary(metaClassMapping);
			byte[] buffer = new byte[1024];
			try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(mappingBytes));
					OutputStream os = FileUtilities.getOutputStreamToFile(metaConceptMappingFile)) {
				int numRead = -1;
				while ((numRead = gzis.read(buffer)) != -1) {
					System.out.println(new String(buffer));
					os.write(buffer, 0, numRead);
				}
			}
		} catch (Exception e) {
			throw new MetaConceptMapCreationException("Creating the mapping file or writing it went wrong.", e);
		}
		log.info("Done creating the meta class mapping file.");
	}

	@Override
	public void exportLingpipeDictionary() throws MetaConceptMapCreationException {
		AquaDivaExport ade = new AquaDivaExport();
		try {
			log.info("Retrieving lingpipe dictionary data for concept tagging from Neo4j database.");
			// This string is the base 64 encoding of the GZIPed mapping
			// file
			String dictionaryBase64 = ade.exportLingpipeDictionary(graphDb);
			log.info("Writing class name dictionary to {}", dictPath);
			byte[] dictBytes = DatatypeConverter.parseBase64Binary(dictionaryBase64);
			byte[] buffer = new byte[1024];
			try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(dictBytes));
					OutputStream os = FileUtilities.getOutputStreamToFile(dictPath)) {
				int numRead = -1;
				while ((numRead = gzis.read(buffer)) != -1) {
					os.write(buffer, 0, numRead);
				}
			}
		} catch (Exception e) {
			throw new MetaConceptMapCreationException("Creating the dictionary file or writing it went wrong.", e);
		}
		log.info("Done creating the lingpipe dictionary file.");

	}

}
