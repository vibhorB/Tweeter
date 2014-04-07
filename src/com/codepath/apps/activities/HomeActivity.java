package com.codepath.apps.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.codepath.apps.fragments.HomeTimelineFragment;
import com.codepath.apps.fragments.MentionsTimelineFragment;
import com.codepath.apps.fragments.SupportFragmentTabListener;
import com.codepath.apps.restclient.TwitterUtils;
import com.codepath.apps.restclienttemplate.R;

public class HomeActivity extends ActionBarActivity {

	private Context context;
	private HomeTimelineFragment homeFragment;
	private MentionsTimelineFragment mentionsFragment;
	
	private int TWEET_REQUEST_CODE = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		context = this;
		//flTweetContainer
		//homeFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flTweetContainer);
		setupTabs();
	}
	
	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
		    .newTab()
		    .setText("Home")
		    .setIcon(R.drawable.ic_home_tab)
		    .setTag("HomeTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<HomeTimelineFragment>(R.id.flTweetContainer, this,
                        "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
		    .newTab()
		    .setText("Mentions")
		    .setIcon(R.drawable.ic_mention_tab)
		    .setTag("MentionsTimelineFragment")
		    .setTabListener(new SupportFragmentTabListener<MentionsTimelineFragment>(R.id.flTweetContainer, this,
                        "mentions", MentionsTimelineFragment.class));
		actionBar.addTab(tab2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        
        try{
	    	searchView.setOnQueryTextListener(new OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					Intent i = new Intent(HomeActivity.this, SearchResultActivity.class);
					i.putExtra("query", query);
					startActivity(i);
					return true;
				}
				
				@Override
				public boolean onQueryTextChange(String arg0) {
					// TODO Auto-generated method stub
					return false;
				}
			});
	    }catch(Exception ex){
	    	System.out.println(ex.getStackTrace());
	    }
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		if(item.getItemId() ==  R.id.action_settings){
			postTweet();
			//startSearchActivity();
            return true;
		}else if(item.getItemId() == R.id.action_profile){
			launchUserDetailActivity("");
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void launchUserDetailActivity(String screen_name){
		Intent i = new Intent(HomeActivity.this, UserDetailActivity.class);
		i.putExtra("screen_name", screen_name);
		startActivity(i);
	}

	private void postTweet() {
		if(TwitterUtils.isNetworkAvailable(context)){
			Intent i = new Intent(HomeActivity.this, TweetActivity.class);
			startActivityForResult(i, TWEET_REQUEST_CODE);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TWEET_REQUEST_CODE && resultCode == RESULT_OK){
			//homeFragment.resetAdapter();
			//homeFragment.fetchHomeTimeLine(1, 0);
			String openTab = getSupportActionBar().getSelectedTab().getTag().toString();
			if(openTab.equals("HomeTimelineFragment")){
				homeFragment = (HomeTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flTweetContainer);
				homeFragment.fetchHomeTimeLine(1, 0);
			}else{
				mentionsFragment = (MentionsTimelineFragment) getSupportFragmentManager().findFragmentById(R.id.flTweetContainer);
				mentionsFragment.fetchMentionsTimeLine(1, 0);
			}
		}
	}
}
