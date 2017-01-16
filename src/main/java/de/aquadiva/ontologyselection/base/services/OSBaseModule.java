package de.aquadiva.ontologyselection.base.services;

import static de.aquadiva.ontologyselection.OSSymbolConstants.HIBERNATE_HBM2DDL_AUTO;
import static de.aquadiva.ontologyselection.OSSymbolConstants.HIBERNATE_JDBC_BATCH_SIZE;
import static de.aquadiva.ontologyselection.OSSymbolConstants.HIBERNATE_SHOW_SQL;
import static de.aquadiva.ontologyselection.OSSymbolConstants.JPA_JDBC_DRIVER;
import static de.aquadiva.ontologyselection.OSSymbolConstants.JPA_JDBC_PW;
import static de.aquadiva.ontologyselection.OSSymbolConstants.JPA_JDBC_URL;
import static de.aquadiva.ontologyselection.OSSymbolConstants.JPA_JDBC_USER;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;

import de.aquadiva.ontologyselection.OSSymbolConstants;

public class OSBaseModule {

	public static void contributeSymbolSource(Logger logger, final OrderedConfiguration<SymbolProvider> configuration) {
		try {			
			String configFileName = "configuration.properties";
			ClasspathResourceSymbolProvider resSmbProv = new ClasspathResourceSymbolProvider(configFileName);
			configuration.add("DevSymbols", resSmbProv,
					"before:ApplicationDefaults");
		} catch (NullPointerException e) {
			logger.info("No configuration file found in the classpath");
		}
	}

	public static void bind(ServiceBinder binder) {
		binder.bind(IOWLParsingService.class, OWLParsingService.class);
		binder.bind(IOntologyDownloadService.class, OntologyDownloadService.class);
		binder.bind(IOntologyFormatConversionService.class, OntologyFormatConversionService.class);
		binder.bind(IOntologyDBService.class, OntologyDBService.class);
		binder.bind(IMetaConceptService.class, MetaConceptService.class);
		binder.bind(IOntologyRepositoryStatsPrinterService.class, OntologyRepositoryStatsPrinterService.class);
	}

	public static EntityManagerFactory buildEntityManagerFactory(
			@Symbol(OSSymbolConstants.PERSISTENCE_CONTEXT) String persistenceContext,
			@Symbol(JPA_JDBC_DRIVER) String driver, @Symbol(JPA_JDBC_URL) String url,
			@Symbol(JPA_JDBC_USER) String user, @Symbol(JPA_JDBC_PW) String password,
			@Symbol(HIBERNATE_SHOW_SQL) String showSql, @Symbol(HIBERNATE_HBM2DDL_AUTO) String hbm2ddlAuto,
			@Symbol(HIBERNATE_JDBC_BATCH_SIZE) String batchsize) {
		Map<String, String> settings = new HashMap<>();
		settings.put(JPA_JDBC_DRIVER, driver);
		settings.put(JPA_JDBC_URL, url);
		settings.put(JPA_JDBC_USER, user);
		settings.put(JPA_JDBC_PW, password);
		settings.put(HIBERNATE_SHOW_SQL, showSql);
		settings.put(HIBERNATE_HBM2DDL_AUTO, hbm2ddlAuto);
		settings.put(HIBERNATE_JDBC_BATCH_SIZE, batchsize);
		return Persistence.createEntityManagerFactory(persistenceContext, settings);
	}

	/**
	 * JPA EntityManager instances are lightweight wrappers around JDBC
	 * connections and NOT threadsafe. Thus, they can be stored in service
	 * singletons on a per-thread basis.
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	@Scope(value = "perthread")
	public static EntityManager buildEntityManager(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}

	public static ExecutorService buildExecutorService(Logger log, RegistryShutdownHub shutdownHub) {
		OntologySelectionExecutorService executorService = new OntologySelectionExecutorService(log, Executors.newFixedThreadPool(4));
		executorService.startupService(shutdownHub);
		return executorService;
	}
}
