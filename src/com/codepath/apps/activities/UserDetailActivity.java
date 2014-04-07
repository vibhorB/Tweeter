package com.codepath.apps.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.DataModel.User;
import com.codepath.apps.fragments.UserTimelineFragment;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

public class UserDetailActivity extends FragmentActivity {

	private TwitterClient client;
	protected Context context;
	private RelativeLayout rlProfileHeader;
	private ImageView ivUserProfile;
	private TextView tvUserName;
	private TextView tvUserScreenName;
	private TextView tvDescription;
	
	private TextView tvtwtStats;
	private TextView followingStats;
	private TextView followerStats;
	
	private LinearLayout twtStatsItem;
	private LinearLayout followingStatsItem;
	private LinearLayout followerStatsItem;
	
	private UserTimelineFragment userTimelineFragment;
	private String screenName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_user_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		client = new TwitterClient(context);
		
		screenName = getIntent().getStringExtra("screen_name");
		initUIComponents();
		getUserInfo();
		setUpMoreActions();
	}

	private void setUpMoreActions() {
		twtStatsItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		followingStatsItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchUsersListActivity("Following");
				
			}
		});
		
		followerStatsItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchUsersListActivity("Follower");
				
			}
		});
	}

	protected void launchUsersListActivity(String useCase) {
		Intent i = new Intent(UserDetailActivity.this, UserListActivity.class);
		i.putExtra("userId", screenName);
		i.putExtra("useCase", useCase);
		startActivity(i);
		
	}

	private void initUIComponents() {
		rlProfileHeader = (RelativeLayout) findViewById(R.id.rlProfile);
		ivUserProfile = (ImageView) findViewById(R.id.ivUser);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvtwtStats = (TextView) findViewById(R.id.tvStat);
		followingStats = (TextView) findViewById(R.id.tvFollowingStat);
		followerStats = (TextView) findViewById(R.id.tvFollowersStat);
		
		twtStatsItem = (LinearLayout) findViewById(R.id.llTwtStats);
		followingStatsItem = (LinearLayout) findViewById(R.id.llFollowingStats);
		followerStatsItem = (LinearLayout) findViewById(R.id.llFollowerStats);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_detail, menu);
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
	
	private void getUserInfo(){
		showProgressBar();
		if(screenName.equals("")){
			client.verifyCredentials(new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONObject json) {
		            User currentUser = User.fromJson(json);
		            setUpProfileHeader(currentUser);
		            if(screenName == null || screenName.equals("")){
		            	screenName = currentUser.getScreenName().substring(1);
		            }
		            initFragment();
		            hideProgressBar();
		            //setUpProfileInfo();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
				   Toast.makeText(context, "Excepton : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		           hideProgressBar();
				  }
				});
		}else{
			client.getUserProfile(screenName, new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONObject json) {
		            User currentUser = User.fromJson(json);
		            setUpProfileHeader(currentUser);
		            if(screenName == null || screenName.equals("")){
		            	screenName = currentUser.getScreenName().substring(1);
		            }
		            initFragment();
		            hideProgressBar();
		            //setUpProfileInfo();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
				   Toast.makeText(context, "Excepton : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		           hideProgressBar();
				}
				});
		}
		
	}
	
	protected void initFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		userTimelineFragment = UserTimelineFragment.newInstance(screenName);
		transaction.replace(R.id.frUserTimeline, userTimelineFragment);
		transaction.commit();
	}

	private void setUpProfileHeader(User user){
		//getActionBar().setTitle(user.getScreenName());
		
		if(user.getProfileBannerUrl() != null && !user.getProfileBannerUrl().equals("")){
			ImageLoader imgLoader = ImageLoader.getInstance();
			
			imgLoader.loadImage(user.getProfileBannerUrl(), new SimpleImageLoadingListener() {
			    @Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			    	Drawable dr = new BitmapDrawable(loadedImage);
				    rlProfileHeader.setBackgroundDrawable(dr);
			    }
			});
		}else{
			rlProfileHeader.setBackgroundColor(getResources().getColor(R.color.LightSlateGray));
		}
		Picasso.with(this).load(user.getProfileImageUrl()).into(ivUserProfile);
		tvUserName.setText(user.getName());
		tvUserScreenName.setText(user.getScreenName());
		tvDescription.setText(user.getDescription());
		
		tvtwtStats.setText(withSuffix(user.getStatuses_count()));
		followingStats.setText(withSuffix(user.getFriends_count()));
		followerStats.setText(withSuffix(user.getFollowers_count()));
	}
	
	private String withSuffix(long count) {
	    if (count < 1000) return "" + count;
	    int exp = (int) (Math.log(count) / Math.log(1000));
	    return String.format("%.1f %c",
	                         count / Math.pow(1000, exp),
	                         "KMGTPE".charAt(exp-1));
	}
	
	public Bitmap getBitmapFromURL(String imageUrl) {
	    try {
	        URL url = new URL(imageUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true); 
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false); 
    }
}
