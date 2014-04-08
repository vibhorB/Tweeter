package com.codepath.apps.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsTimelineFragment extends TweetListFragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public void fetchMentionsTimeLine(final int page, long l) {
		
			showProgressBar();
			client.getMentions(l, new JsonHttpResponseHandler() {
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
		fetchMentionsTimeLine(page, l);
		
	}

}
