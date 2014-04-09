package com.codepath.apps.activities;

import com.codepath.apps.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.R;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class UserTweetActivity extends ActionBarActivity {

	private UserTimelineFragment userTimelineFragment;
	String screenName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_user_tweet);		
		screenName = getIntent().getStringExtra("screen_name");
		getActionBar().setTitle(screenName+"'s Tweets");
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		userTimelineFragment = UserTimelineFragment.newInstance(screenName);
		transaction.replace(R.id.frUserTweets, userTimelineFragment);
		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_tweet, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			setResult(RESULT_OK);
            this.finish();
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
    }

}
