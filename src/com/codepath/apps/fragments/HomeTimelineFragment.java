package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclient.TwitterUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.widget.Toast;

public class HomeTimelineFragment extends TweetListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		if(TwitterUtils.isNetworkAvailable(context)){
			//clear DB so that only new tweets are saved
			//Tweet.deleteAll();
	        fetchHomeTimeLine(1, 0);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			//load from DB
			resetAdapter();
			//getAdapter().addAll(Tweet.getAll());
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	}
	
	@Override
	protected void doRefresh() {
		super.doRefresh();
		if(TwitterUtils.isNetworkAvailable(context)){
    		Tweet.deleteAll();
	        fetchHomeTimeLine(1, 0);
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
        		fetchHomeTimeLine(1,0);
        	}else{
        		fetchHomeTimeLine(page, last.getTweetId()-1);
        	}
    	}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	};
	
	
	
	
	public void fetchHomeTimeLine(int page, long l) {
		
		showProgressBar();
		if(page <= 1){
			client.getHomeTimeline(new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONArray body) {
					resetAdapter();
		            JSONArray items = null;
					items = body;
					// Parse json array into array of model objects
					ArrayList<Tweet> tweets = Tweet.fromJSON(items);
					// Load model objects into the adapter
					for (Tweet twt : tweets) {
					   getAdapter().add(twt);
					   twt.save();
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
				   Toast.makeText(context, "Excepton : "+err, Toast.LENGTH_SHORT).show();
				   hideProgressBar();
				  }				
				});	
		}else{
			client.getMoreHomeTimeline(l, new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONArray body) {
		            JSONArray items = null;
		            // Get the movies json array
					items = body;
					// Parse json array into array of model objects
					ArrayList<Tweet> tweets = Tweet.fromJSON(items);
					// Load model objects into the adapter
					for (Tweet twt : tweets) {
					   getAdapter().add(twt);
					   //twt.save();
					}
					hideProgressBar();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
					String err = e.getLocalizedMessage();
					if(err.trim().equals("Client Error (429)")){
						err = "Rate limiting, try later";
					}
				   Toast.makeText(context, "Excepton : "+err, Toast.LENGTH_SHORT).show();
				   hideProgressBar();
				  }
				});	
		}
	}
	
//	public void showProgressBar() {
//        getActivity().setProgressBarIndeterminateVisibility(true); 
//    }
//
//    // Should be called when an async task has finished
//    public void hideProgressBar() {
//        getActivity().setProgressBarIndeterminateVisibility(false); 
//    }
//	
//	public void resetAdapter(){
//		getAdapter().clear();
//		getAdapter().notifyDataSetInvalidated();
//		
//	}
}
