package nmf;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable{
	
	private static final long serialVersionUID = 7557356762656495398L;
	private Date date;
	private String title;
	private String path;
	
	public Document() {
		date = null;
		title = "";
		path = "";
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
