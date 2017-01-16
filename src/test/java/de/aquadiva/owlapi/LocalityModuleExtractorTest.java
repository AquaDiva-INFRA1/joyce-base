package de.aquadiva.owlapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class LocalityModuleExtractorTest {
@Test
public void testModuleExtraction() throws Exception{
	System.out.println("Creating manager");
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	System.out.println("Loading ontology");
	OWLOntology ont = man.loadOntologyFromOntologyDocument(new File("envo.owl"));
	IRI cls = IRI.create("http://purl.obolibrary.org/obo/ENVO_01000648");
	extractModule(man, ont, ModuleType.BOT, cls);
	extractModule(man, ont, ModuleType.TOP, cls);
	extractModule(man, ont, ModuleType.STAR, cls);
}

private void extractModule(OWLOntologyManager man, OWLOntology ont, ModuleType moduleType, IRI cls) throws Exception {
	System.out.println("---------- Extracting module of type " + moduleType);
	System.out.println("Instantiating extractor");
	SyntacticLocalityModuleExtractor extractor = new SyntacticLocalityModuleExtractor(man, ont, moduleType);
	System.out.println("Retrieving test class");
	OWLClass owlClass = man.getOWLDataFactory().getOWLClass(cls);
	System.out.println("Extracting axiom set");
	Set<OWLAxiom> allAxioms = ont.getAxioms();
	OWLOntology extracted = extractor.extractAsOntology(Collections.<OWLEntity>singleton(owlClass), IRI.create(ont.getOntologyID().getOntologyIRI().toString() + "_" + moduleType.name()));
//	SetView<OWLAxiom> notExtractedAxioms = Sets.difference(allAxioms, extracted);
//	System.out.println("Number of all axioms: " + allAxioms.size());
//	System.out.println("Number of extracted axioms: " + extracted.size());
//	System.out.println("Number of not extracted axioms: " + notExtractedAxioms.size());
//	int numNotExtractedClasses = 0;
//	for (OWLAxiom a : extracted) {
//		System.out.println(a);
//	}
	
	
	
	try (OutputStream os = new FileOutputStream("test_" + moduleType.name() + ".owl")) {
		man.saveOntology(extracted, os);
	}
}
}
