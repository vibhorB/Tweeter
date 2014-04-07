package com.codepath.apps.activities;

import com.codepath.apps.fragments.SearchResultsFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.R.layout;
import com.codepath.apps.restclienttemplate.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class SearchResultActivity extends ActionBarActivity {

	private SearchResultsFragment searchFragment;
	String searchQuery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_search_result);
		
		searchQuery = getIntent().getStringExtra("query");
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		searchFragment = SearchResultsFragment.newInstance(searchQuery);
		transaction.replace(R.id.frSearchTweets, searchFragment);
		transaction.commit();
		//searchFragment = (SearchResultsFragment) getSupportFragmentManager().findFragmentById(R.id.frSearchTweets);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
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
