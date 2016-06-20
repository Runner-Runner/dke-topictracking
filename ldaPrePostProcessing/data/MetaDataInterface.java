package data;

import java.util.ArrayList;

public interface MetaDataInterface {

	/**
	 * 
	 * @param index
	 * @return document name
	 */
	public String getDocName(int index);
	
	/**
	 * 
	 * @param index
	 * @return document filename
	 */
	public String getDocFilename(int index);
	
	/**
	 * 
	 * @param name
	 * @return index of document with name name
	 */
	public int getDocIdForName(String name);

	/**
	 * 
	 * @return list of distinct time stamps for all documents
	 */
	public ArrayList<Integer> getDocDates();
	
	/**
	 * 
	 * @param index
	 * @return time stamp for document with index index
	 */
	public Integer getDocDate(int index);
	
}
