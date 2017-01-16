package de.aquadiva.ontologyselection.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.base.data.ScoreType;
import de.aquadiva.ontologyselection.base.services.JoyceBaseModule;

public class OntologyJPATest {

	private static EntityManagerFactory entityManagerFactory;

	@BeforeClass
	public static void setUp() throws Exception {
		Registry registry = RegistryBuilder.buildAndStartupRegistry(JoyceBaseModule.class);
		entityManagerFactory = registry.getService(EntityManagerFactory.class);
		
	}

	@AfterClass
	public static void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testStoryOntology() {
		// Create an ontology
		Ontology o = new Ontology();
		o.setId("someId");
		o.setName("TestOnto");
		o.setFile(new File("just/a/test.owl"));
		o.setScore(ScoreType.POPULARITY, .7);

		// create an ontology module to this ontology
		OntologyModule om = new OntologyModule();
		om.setId("someModuleId");
		om.setName("Module1");
		om.setFile(new File("moduleFile.owl"));
		om.setSourceOntology(o);
		
		// Make the ontology persistent, storing it in the database
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(o);
		em.persist(om);
		em.getTransaction().commit();
		em.close();
		
		
		// Now get the ontology back from the database
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		// this is how we can get exactly instances of the Ontology super type without the OntologyModule subtype.
		// From http://stackoverflow.com/questions/24512975/non-polymorphic-queries-with-hibernate-jpa-jpql and slightly adapted
		List<Ontology> persistedOntos = em.createQuery("select o from Ontology o where type(o) = Ontology", Ontology.class).getResultList();
		assertEquals(1, persistedOntos.size());
		Ontology po = persistedOntos.get(0);
		assertEquals("TestOnto", po.getName());
		assertEquals(new File("just/a/test.owl"), po.getFile());
		assertEquals(new Double(.7d), po.getScore(ScoreType.POPULARITY));
		// Does the association to the modules work?
		assertEquals("someModuleId", po.getModules().get(0).getId());
		
		List<OntologyModule> persistedOntoModules = em.createQuery("from OntologyModule", OntologyModule.class).getResultList();
		assertEquals(1, persistedOntoModules.size());
		assertEquals("someModuleId", persistedOntoModules.get(0).getId());
		
		// This should not trigger a new database query since we have already opened all modules above
		OntologyModule ontologyModule = em.find(OntologyModule.class, "someModuleId");
		ontologyModule = em.find(OntologyModule.class, "someModuleId");
		assertNotNull(ontologyModule);
		// Does the association to the source ontology work? 
		assertEquals("someId", ontologyModule.getSourceOntology().getId());
		
		em.getTransaction().commit();
		em.close();
	}
}
