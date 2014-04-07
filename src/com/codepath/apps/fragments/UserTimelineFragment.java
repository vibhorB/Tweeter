package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclient.TwitterUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetListFragment{
	
	private String screenName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		screenName = getArguments().getString("screen_name");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		if(TwitterUtils.isNetworkAvailable(context)){
			//clear DB so that only new tweets are saved
			//Tweet.deleteAll();
	        fetchUserTimeLine(1, 0);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			//load from DB
			resetAdapter();
			//getAdapter().addAll(Tweet.getAll());
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	}
	
	public static UserTimelineFragment newInstance(String screenName){
		UserTimelineFragment userTimeLineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimeLineFragment.setArguments(args);
        return userTimeLineFragment;
	}
	
	@Override
	protected void doRefresh() {
		super.doRefresh();
		if(TwitterUtils.isNetworkAvailable(context)){
    		Tweet.deleteAll();
	        fetchUserTimeLine(1, 0);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			resetAdapter();
			getAdapter().addAll(Tweet.getAll());
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	};
	
	@Override
	protected void doLoadMore(int page, int totalItemsCount) {
		super.doLoadMore(page, totalItemsCount);
		if(TwitterUtils.isNetworkAvailable(context)){
    		Tweet last = (Tweet) getListView().getItemAtPosition(totalItemsCount-1);
        	if(last == null){
        		fetchUserTimeLine(1,0);
        	}else{
        		fetchUserTimeLine(page, last.getTweetId()-1);
        	}
    	}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	};
	
	
	
	
	public void fetchUserTimeLine(final int page, long l) {
		
		showProgressBar();
		
			client.getUserTimeline(screenName, l, new JsonHttpResponseHandler() {
				@Override
			            public void onSuccess(int code, JSONArray body) {
					if(page <=1){
						resetAdapter();
					}
		            JSONArray items = null;
					items = body;
					// Parse json array into array of model objects
					ArrayList<Tweet> tweets = Tweet.fromJSON(items);
					// Load model objects into the adapter
					for (Tweet twt : tweets) {
					   getAdapter().add(twt);
					   //twt.save();
					}
					getListView().onRefreshComplete();
					hideProgressBar();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
					String err = e.getLocalizedMessage();
					if(err.trim().equals("Client Error (429)")){
						err = "Rate limiting, try later";
					}
				   Toast.makeText(context, "Excepton :" + err, Toast.LENGTH_SHORT).show();
				   hideProgressBar();
				  }				
				});	
	}

}
