package com.univ.helsinki.app.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Feed {
	private long id;
	private String title;
	private String content;
	private long updated;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getUpdated() {
		return updated;
	}
	
	public String getUpdatedTimestamp() {
		return getDate(updated, "dd MMM, yyyy HH:mm:ss");
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return title + "/" + content + "/" + getDate(updated, "dd MMM, yyyy HH:mm:ss");
	}

	/**
	 * Return date in specified format.
	 * @param milliSeconds Date in milliseconds
	 * @param dateFormat Date format 
	 * @return String representing date in specified format
	 */
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
}
