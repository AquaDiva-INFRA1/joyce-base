package de.aquadiva.joyce.base.data;

import java.util.Map;

import com.google.common.collect.Multiset;

/**
 * An interface for objects that cache information of a certain {@link InfoType}.
 * 
 * @author friederike
 *
 */
public interface IInformationCachingObject {
	
	/**
	 * 
	 * @return the cached information attached to this object
	 */
	public Map<InfoType, Multiset<String>> getCachedInformation();
	
	/**
	 * Assigns the given information to this object.
	 * 
	 * @param informationToCache
	 */
	public void setCacheInformation(Map<InfoType, Multiset<String>> informationToCache);
	
	/**
	 * Assign the given information of the given type to this object.
	 * 
	 * @param type
	 * @param info
	 */
	public void addCacheInformation(InfoType type, Multiset<String> info);
	
	/**
	 * 
	 * @param type
	 * @return the cached information of the given type
	 */
	public Multiset<String> getCachedInformation(InfoType type);
	
}
