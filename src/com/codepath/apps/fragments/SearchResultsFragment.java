package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
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
	
	public void fetchSearchResults(final int page, long l) {
		
		showProgressBar();
		client.searchTweets(searchQuery, l, new JsonHttpResponseHandler() {
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
				ArrayList<Tweet> tweets = Tweet.fromJSON(items);
				for (Tweet twt : tweets) {
				   getAdapter().add(twt);
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
	
	@Override
	public void makeAPICall(int page, long l) {
		fetchSearchResults(page, l);
		
	}
}
