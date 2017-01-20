package de.aquadiva.joyce.base.data.bioportal;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class OntologySubmission {
	public String hompepage;
	public String hasOntologyLanguage;
	public Date released;
	public Date creationDate;
	public String documentation;
	public String publication;
	public String version;
	public String description;
	public String status;
	@SerializedName("@id")
	public String submissionId;
}
