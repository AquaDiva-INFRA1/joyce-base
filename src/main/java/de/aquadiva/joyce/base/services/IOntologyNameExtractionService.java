package de.aquadiva.joyce.base.services;

public interface IOntologyNameExtractionService {
	/**
	 * Extracts the class names (preferred name, synonyms but also
	 * descriptions/definitions) from ontologies in the ontology download
	 * directory. This directory is specified in the configuration.
	 */
	void extractNames();
}
