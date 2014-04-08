package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.activities.EndlessScrollListener;
import com.codepath.apps.activities.TwitterHomeFeedAdapter;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclient.TwitterUtils;
import com.codepath.apps.restclienttemplate.R;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class TweetListFragment extends Fragment {
	
	private PullToRefreshListView lvTweets;
	protected Context context;
	private TwitterHomeFeedAdapter adapterTweets;
	protected TwitterClient client;
	
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.fragment_tweets_list, parent, false);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		return v;
	}
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		client = new TwitterClient(context);
		setTweetListView();
		setUpListViewListeners();
		checkNetworkAndMakeApiCall();
	}
	
	private void checkNetworkAndMakeApiCall() {
		if(TwitterUtils.isNetworkAvailable(context)){
			makeAPICall(1, 0);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			resetAdapter();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
		
	}

	public abstract void makeAPICall(int page, long l);
	
	private void setTweetListView(){
		ArrayList<Tweet> aTweets = new ArrayList<Tweet>();
		adapterTweets = new TwitterHomeFeedAdapter(context, aTweets);
		lvTweets.setAdapter(adapterTweets);
		
	}
	
	private void setUpListViewListeners() {
		getListView().setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	checkNetworkAndMakeApiCall();
            }
        });
		
		getListView().setOnScrollListener(new EndlessScrollListener() {
	        @Override
	        public void onLoadMore(int page, int totalItemsCount) {
	        	doLoadMore(page, totalItemsCount);
	        }
	        });
	}

	protected void doLoadMore(int page, int totalItemsCount) {
		if(TwitterUtils.isNetworkAvailable(context)){
    		Tweet last = (Tweet) getListView().getItemAtPosition(totalItemsCount-1);
        	if(last == null){
        		makeAPICall(1,0);
        	}else{
        		makeAPICall(page, last.getTweetId()-1);
        	}
    	}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	}

	public TwitterHomeFeedAdapter getAdapter(){
		return adapterTweets;
	}
	
	public PullToRefreshListView getListView(){
		return lvTweets;
	}
	
	public void showProgressBar() {
        getActivity().setProgressBarIndeterminateVisibility(true); 
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
        getActivity().setProgressBarIndeterminateVisibility(false); 
    }
	
	public void resetAdapter(){
		getAdapter().clear();
		getAdapter().notifyDataSetInvalidated();
		
	}
	
	protected void jsonHandlerSuccessFunction(int page, JSONArray body){
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
		   twt.save();
		}
		getListView().onRefreshComplete();
		hideProgressBar();
	}
	
	protected void jsonHandlerFailureFunction(String err){
		if(err.trim().equals("Client Error (429)")){
			err = "Rate limiting, try later";
		}
	   Toast.makeText(context, "Exception : "+err, Toast.LENGTH_SHORT).show();
	   hideProgressBar();
	}
}
