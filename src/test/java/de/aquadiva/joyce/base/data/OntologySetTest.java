package de.aquadiva.joyce.base.data;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologySet;

public class OntologySetTest {
	static OntologySet s1;
	static OntologySet s2;
	static OntologySet s3;
	static OntologySet s4;
	static ArrayList<OntologySet> list1;

	@Before
	public void setUp() throws Exception {
		s1 = new OntologySet();
		Ontology o1 = new Ontology();
		o1.setId("ontology-1");
		s1.addOntology(o1);
		Ontology o2 = new Ontology();
		o2.setId("ontology-2");
		s1.addOntology(o2);
		
		s2 = new OntologySet();
		Ontology o3 = new Ontology();
		o3.setId("ontology-1");
		s2.addOntology(o3);
		Ontology o4 = new Ontology();
		o4.setId("ontology-2");
		s2.addOntology(o4);
		
		s3 = new OntologySet();
		Ontology o5 = new Ontology();
		o5.setId("ontology-1");
		s3.addOntology(o5);
		Ontology o6 = new Ontology();
		o6.setId("ontology-2");
		s3.addOntology(o6);
		
		s4 = new OntologySet();
		Ontology o7 = new Ontology();
		o7.setId("ontology-1");
		s4.addOntology(o7);
		Ontology o8 = new Ontology();
		o8.setId("ontology-2");
		s4.addOntology(o8);
		
		list1 = new ArrayList<OntologySet>();
		list1.add(s1);
		list1.add(s2);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEquals() {
		assertTrue(s1.equals(s2));		
	}
	
	@Test
	public void testHashCode() {
		assertTrue(s1.hashCode()==s2.hashCode());		
	}
	
	@Test
	public void testSetContainmentInArrayListsOfOntologySets() {
		assertTrue(list1.contains(s1));
		assertTrue(list1.contains(s2));
		assertTrue(list1.contains(s3));
		assertTrue(list1.contains(s4));
	}

}
