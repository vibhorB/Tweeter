package com.codepath.apps.fragments;

import java.util.ArrayList;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.activities.EndlessScrollListener;
import com.codepath.apps.activities.TwitterHomeFeedAdapter;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclienttemplate.R;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TweetListFragment extends Fragment {
	
	private PullToRefreshListView lvTweets;
	protected Context context;
	private TwitterHomeFeedAdapter adapterTweets;
	protected TwitterClient client;
	
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tweets_list, parent, false);
	}
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		client = new TwitterClient(context);
		lvTweets = (PullToRefreshListView) getActivity().findViewById(R.id.lvTweets);
		setTweetListView();
		setUpListViewListeners();
	}
	
	private void setTweetListView(){
		ArrayList<Tweet> aTweets = new ArrayList<Tweet>();
		adapterTweets = new TwitterHomeFeedAdapter(context, aTweets);
		lvTweets.setAdapter(adapterTweets);
		
	}
	
	private void setUpListViewListeners() {
		getListView().setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	doRefresh();
            }
        });
		
		getListView().setOnScrollListener(new EndlessScrollListener() {
	        @Override
	        public void onLoadMore(int page, int totalItemsCount) {
	        	doLoadMore(page, totalItemsCount);
	        }
	        });
	}

	protected void doLoadMore(int page, int totalItemsCount) {}

	protected void doRefresh() {}

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
}
