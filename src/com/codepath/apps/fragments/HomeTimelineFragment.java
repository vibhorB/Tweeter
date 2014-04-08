package com.codepath.apps.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;

public class HomeTimelineFragment extends TweetListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void fetchHomeTimeLine(final int page, long l) {
		
			showProgressBar();
			client.getHomeTimeline(l, new JsonHttpResponseHandler() { 
				@Override
			    public void onSuccess(int code, JSONArray body) {
					jsonHandlerSuccessFunction(page, body);
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
					jsonHandlerFailureFunction(e.getLocalizedMessage());	
				}
				});
	}

	@Override
	public void makeAPICall(int page, long l) {
		fetchHomeTimeLine(page, l);	
	}
}
