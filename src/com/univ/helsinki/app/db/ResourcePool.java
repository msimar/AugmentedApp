package com.univ.helsinki.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.univ.helsinki.app.core.Feed;

public class ResourcePool {
	
	private static ResourcePool INSTANCE;
	
	private List<Feed> mFeedList;
	
	private RecentActivityDataSource mDatasource;
	
	private ResourcePool() {
		this.mFeedList = new ArrayList<Feed>();
	}
	
	public static synchronized ResourcePool getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ResourcePool();
		return INSTANCE;
	}
	
	public void inti(Context context) {
		this.mDatasource = new RecentActivityDataSource(context);
		this.mDatasource.open();
	}
	
	public List<Feed> getAllFeed(){
		this.mFeedList = this.mDatasource.getAllFeeds();;
		return this.mFeedList;
	}
	
	public void addFeed(int location, Feed feed){
		this.mFeedList.add(location, feed);
	}
	
	public void removeFeed(int location){
		long id = this.mFeedList.get(location).getId();
		
		if(mDatasource != null){
			mDatasource.delete(id);
		}
		mFeedList.remove(location);
	}
	
	public Feed createFeed(String title, String content) {
		return this.mDatasource.createFeed(title, content);
	}
	
	public void destroy(){
		this.mDatasource.close();
		this.mFeedList.clear();
	}
}