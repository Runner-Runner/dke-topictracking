package data;

import java.nio.file.Path;

/**
 * Document reader interface
 */
public interface DocumentHandlerInterface {

	/**
	 * read document text
	 * 
	 * @param filePath
	 * @param asHTML	get enclosing html tags
	 * @return
	 */
	public String readDocumentText(final Path filePath,
			final boolean asHTML);
	
	/**
	 * Read metadata from document
	 * 
	 * @param filePath
	 * @return
	 */
	public String readDocumentMetaData(final Path filePath);
}
