package com.univ.helsinki.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.univ.helsinki.app.R;
import com.univ.helsinki.app.core.Feed;
import com.univ.helsinki.app.db.ResourcePool;

public class RecentActivityAdapter extends BaseAdapter {

	private List<Feed> mFeedList;
	private LayoutInflater mInflater;

	public RecentActivityAdapter(Context context) {
		
		this.mFeedList = ResourcePool.getInstance().getAllFeed();

		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeedList.size();
	}

	@Override
	public Feed getItem(int position) {
		// TODO Auto-generated method stub
		return mFeedList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mFeedList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.listview_feed_item,
					parent, false);
			
			holder = new ViewHolder();
			
	        holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
	        holder.tvContent = (TextView) convertView.findViewById(R.id.content);
	        holder.tvTimestamp = (TextView) convertView.findViewById(R.id.timestamp);

	        convertView.setTag(holder);
	    } else {
	        holder = (ViewHolder) convertView.getTag();
	    }

		holder.tvTitle.setText(mFeedList.get(position).getTitle());
		holder.tvContent.setText(mFeedList.get(position).getContent());
		holder.tvTimestamp.setText(mFeedList.get(position).getUpdatedTimestamp());

		return convertView;
	}

	private static class ViewHolder {
	    public TextView tvTitle;
	    public TextView tvContent;
	    public TextView tvTimestamp;
	}
	
	public void notifyDataChanged(){
		this.mFeedList = ResourcePool.getInstance().getAllFeed();
		this.notifyDataSetChanged();
	}
}
