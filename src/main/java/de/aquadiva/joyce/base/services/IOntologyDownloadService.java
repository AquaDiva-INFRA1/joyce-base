package de.aquadiva.joyce.base.services;

import java.io.File;

public interface IOntologyDownloadService {
	void downloadBioPortalOntologies(File downloadDir, File jsonMetaDir, String... requestedAcronyms);
	void downloadBioPortalOntologiesToConfigDirs(String...requestedAcronyms);
}
