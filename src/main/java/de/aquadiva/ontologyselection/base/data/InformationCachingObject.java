package de.aquadiva.ontologyselection.base.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

import com.google.common.collect.Multiset;

/**
 * A class representing objects that cache information of a certain {@link InfoType}.
 * 
 * @author friederike
 *
 */
public class InformationCachingObject {
	protected Map<InfoType, Multiset<String>> cachedInformation; //stores information that is needed to calculate scores incrementally
	
	public Map<InfoType, Multiset<String>> getCachedInformation() {
		return this.cachedInformation;
	}
	
	public void setCacheInformation(Map<InfoType, Multiset<String>> informationToCache) {
		this.cachedInformation = informationToCache;
	}
	
	public void addCacheInformation(InfoType type, Multiset<String> info) {
		
		if (null == cachedInformation) {
			cachedInformation = new HashMap<InfoType, Multiset<String>>();
		}
		
		cachedInformation.put(type, info);

	}
	
	@Transient
	public Multiset<String> getCachedInformation(InfoType type) {
		if (null == cachedInformation) { return null; }
		else { return cachedInformation.get(type); }
	}
}
