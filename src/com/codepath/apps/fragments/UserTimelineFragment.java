package com.codepath.apps.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetListFragment {

	private String screenName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		screenName = getArguments().getString("screen_name");
	}

	public static UserTimelineFragment newInstance(String screenName) {
		UserTimelineFragment userTimeLineFragment = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putString("screen_name", screenName);
		userTimeLineFragment.setArguments(args);
		return userTimeLineFragment;
	}

	public void fetchUserTimeLine(final int page, long l) {

		showProgressBar();

		client.getUserTimeline(screenName, l, new JsonHttpResponseHandler() {
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
		fetchUserTimeLine(page, l);
	}

}
