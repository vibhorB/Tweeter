package com.codepath.apps.activities;

import com.codepath.apps.fragments.UserListFragment;
import com.codepath.apps.restclienttemplate.R;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class UserListActivity extends ActionBarActivity{

	private UserListFragment userListFragment;
	String userId;
	String useCase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_user_list);
		
		userId = getIntent().getStringExtra("userId");
		useCase = getIntent().getStringExtra("useCase");
		if(useCase.equals("Following")){
			getActionBar().setTitle("Following");
		}else if(useCase.equals("Followers")){
			getActionBar().setTitle("Followers");
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		userListFragment = UserListFragment.newInstance(userId, useCase);
		transaction.replace(R.id.flUserList, userListFragment);
		transaction.commit();
		//searchFragment = (SearchResultsFragment) getSupportFragmentManager().findFragmentById(R.id.frSearchTweets);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_list, menu);
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
