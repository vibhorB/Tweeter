package com.codepath.apps.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.DataModel.User;
import com.codepath.apps.activities.EndlessScrollListener;
import com.codepath.apps.activities.UserListAdapter;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclient.TwitterUtils;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class UserListFragment extends Fragment{
	
	private PullToRefreshListView lvUsers;
	protected Context context;
	private UserListAdapter adapterUsers;
	protected TwitterClient client;
	
	private String userId;
	private String useCase;
	private String cursor;
	
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		 userId = getArguments().getString("userId");
		 useCase = getArguments().getString("useCase");
		return inflater.inflate(R.layout.fragment_user_list, parent, false);
	}
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		client = new TwitterClient(context);
		lvUsers = (PullToRefreshListView) getActivity().findViewById(R.id.lvUsers);
		setUsersListView();
		setUpListViewListeners();
		
		startAPICall();
	}
	
	private void startAPICall() {
		if(TwitterUtils.isNetworkAvailable(context)){
			//clear DB so that only new tweets are saved
			//Tweet.deleteAll();
	        fetchUsersListBasedOnUseCase(1);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			//load from DB
			resetAdapter();
			//getAdapter().addAll(Tweet.getAll());
			getListView().onRefreshComplete();
			hideProgressBar();
		}
		
	}

	private void fetchUsersListBasedOnUseCase(final int page) {
		if(useCase.equals("Following")){
			fetchFollowingUsers(page);
		}else if(useCase.equals("Follower")){
			fetchFollowers(page);
		}
	}
	
	private void fetchFollowingUsers(final int page){
		showProgressBar();
		if(page <= 1){
			cursor = null;
		}
		client.getFriendsList(userId, cursor, new JsonHttpResponseHandler() {
			@Override
		            public void onSuccess(int code, JSONObject body) {
				if(page <=1){
					resetAdapter();
					
				}
				JSONArray items = null;
				try {
					items = body.getJSONArray("users");
					cursor = body.getString("next_cursor_str");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "JSON parsing failed", Toast.LENGTH_SHORT).show();
				}
				// Parse json array into array of model objects
				ArrayList<User> users = User.fromJSON(items);
				// Load model objects into the adapter
				for (User usr : users) {
				   getAdapter().add(usr);
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
	
	private void fetchFollowers(final int page){
		showProgressBar();
		if(page <= 1){
			cursor = null;
		}
		client.getFollowersList(userId, cursor, new JsonHttpResponseHandler() {
			@Override
		            public void onSuccess(int code, JSONObject body) {
				if(page <=1){
					resetAdapter();
				}
				JSONArray items = null;
				try {
					items = body.getJSONArray("users");
					cursor = body.getString("next_cursor_str");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getActivity(), "JSON parsing failed", Toast.LENGTH_SHORT).show();
				}
				// Parse json array into array of model objects
				ArrayList<User> users = User.fromJSON(items);
				// Load model objects into the adapter
				for (User usr : users) {
				   getAdapter().add(usr);
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

	private void setUsersListView(){
		ArrayList<User> aTweets = new ArrayList<User>();
		adapterUsers = new UserListAdapter(context, aTweets);
		lvUsers.setAdapter(adapterUsers);
		
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

	protected void doLoadMore(int page, int totalItemsCount) {
		if(TwitterUtils.isNetworkAvailable(context)){
    		User last = (User) getListView().getItemAtPosition(totalItemsCount-1);
        	if(last == null){
        		fetchUsersListBasedOnUseCase(1);
        	}else{
        		fetchUsersListBasedOnUseCase(page);
        	}
    	}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	}

	protected void doRefresh() {
		if(TwitterUtils.isNetworkAvailable(context)){
    		fetchUsersListBasedOnUseCase(1);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			resetAdapter();
			//getAdapter().addAll(Tweet.getAll());
			getListView().onRefreshComplete();
			hideProgressBar();
		}
	}

	public UserListAdapter getAdapter(){
		return adapterUsers;
	}
	
	public PullToRefreshListView getListView(){
		return lvUsers;
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

	public static UserListFragment newInstance(String userId, String useCase) {
		UserListFragment fragmentDemo = new UserListFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("useCase", useCase);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
	}

}
