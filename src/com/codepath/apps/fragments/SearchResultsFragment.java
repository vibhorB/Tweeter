package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.activities.SearchResultActivity;
import com.codepath.apps.restclient.TwitterUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchResultsFragment extends TweetListFragment{

	private String searchQuery;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		searchQuery = getArguments().getString("query");
	}
	
	public static SearchResultsFragment newInstance(String query){
		SearchResultsFragment fragmentDemo = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		fetchSearchResults(searchQuery, 1, 0);
		
//		if(TwitterUtils.isNetworkAvailable(context)){
//			//clear DB so that only new tweets are saved
//			//Tweet.deleteAll();
//	        fetchSearchResults(searchQuery, 1, 0);
//		}else{
//			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
//			//load from DB
//			resetAdapter();
//			getAdapter().addAll(Tweet.getAll());
//			getListView().onRefreshComplete();
//			hideProgressBar();
//		}
	}
	
	@Override
	protected void doRefresh() {
		super.doRefresh();
		if(TwitterUtils.isNetworkAvailable(context)){
    		Tweet.deleteAll();
	        fetchSearchResults(searchQuery, 1, 0);
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
        		fetchSearchResults(searchQuery, 1,0);
        	}else{
        		fetchSearchResults(searchQuery, page, last.getTweetId()-1);
        	}
    	}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	};
	
//	public void startSearch(String query){
//		this.searchQuery = query;
//		fetchSearchResults(searchQuery, 1, 0);
//	}
	
	
	
	public void fetchSearchResults(String query, final int page, long l) {
		
		showProgressBar();
		client.searchTweets(query, l, new JsonHttpResponseHandler() {
			@Override
		            public void onSuccess(int code, JSONObject body) {
				if(page <=1){
					resetAdapter();
				}
				JSONArray items = null;
				try {
					items = body.getJSONArray("statuses");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "JSON parsing failed", Toast.LENGTH_SHORT).show();
				}
	            //JSONArray items = null;
				//items = body;
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
			   Toast.makeText(context, "Excepton : "+err, Toast.LENGTH_SHORT).show();
			   hideProgressBar();
			  }				
			});	
	}
}
