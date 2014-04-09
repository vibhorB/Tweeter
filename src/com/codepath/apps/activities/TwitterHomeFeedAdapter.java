package com.codepath.apps.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Picasso;


public class TwitterHomeFeedAdapter extends ArrayAdapter<Tweet>{

//	private Button btRetweet;
//	private Button btFav;
//	private Button btReply;
//	private Tweet tweet;
	private TwitterClient client = new TwitterClient(getContext());
	
	public TwitterHomeFeedAdapter(Context context, ArrayList<Tweet> tweets){
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Tweet tweet = this.getItem(position);
		ViewHolder holder;
		//tweet = Tweet.get(tweetId);
		if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvHandle = (TextView) convertView.findViewById(R.id.tvHandle);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.ivPosterImage = (ImageView) convertView.findViewById(R.id.ivPosterImage);
            holder.ivMedia = (SmartImageView) convertView.findViewById(R.id.ivMedia);
            holder.tvWhen = (TextView) convertView.findViewById(R.id.tvWhen);
            holder.btRetweet = (Button) convertView.findViewById(R.id.btRetweet);
            holder.btReply = (Button) convertView.findViewById(R.id.btReply);
            holder.btFav = (Button) convertView.findViewById(R.id.btFav);
            convertView.setTag(holder);
        }
        else 
        {
             holder=(ViewHolder) convertView.getTag();
        }
		
        holder.tvName.setText(tweet.getName());
        holder.tvHandle.setText(tweet.getHandle());
        holder.tvContent.setText(tweet.getContent());
        Picasso.with(getContext()).load(tweet.getImageUrl()).into(holder.ivPosterImage);
        setRetweetedIcon(holder.btRetweet, tweet.isRetweeted());
        setFavoritedIcon(holder.btFav, tweet.isFavorited());
        setUpActionButtons(holder.btReply, holder.btRetweet, holder.btFav, tweet);
        if(tweet.getRetweet_count() > 0){
        	holder.btRetweet.setText(" "+tweet.getRetweet_count());
        }
        if(tweet.getFavourites_count() > 0){
        	holder.btFav.setText(" "+tweet.getFavourites_count());
        }
        
        if(tweet.getMediaUrl() != null && tweet.getMediaUrl().length() > 0){
        	//Picasso.with(getContext()).load(tweet.getMediaUrl()).into(ivMedia);
        	//holder.ivMedia.setImageUrl(tweet.getMediaUrl());
        }
        holder.tvWhen.setText(getWhenCreated(tweet.getCreatedAt()));
        setUpImageViewListener(holder.ivPosterImage, tweet.getHandle());
        setUpItemClickListener(convertView, tweet);
        
        return convertView;
	}
	
	private void setUpActionButtons(Button btReply, final Button btRetweet,final Button btFav, final Tweet twt) {
//		btReply.setTag(tweet.getTweetId());
//		btRetweet.setTag(tweet.getTweetId());
//		btFav.setTag(tweet.getTweetId());
		btReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTweetActivity(twt);	
			}
		});
		
		btRetweet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmRetweet(btRetweet, twt);
			}
		});
		
		btFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postFavorite(btFav, twt);
			}
		});
	}
	
	private void startTweetActivity(Tweet tweet) {
		Intent reply = new Intent(getContext(), TweetActivity.class);
		reply.putExtra("purpose", "reply");
		reply.putExtra("screen_name", tweet.getHandle());
		reply.putExtra("id", tweet.getTweetId());
		getContext().startActivity(reply);
		
	}
	
	protected void confirmRetweet(final Button btRetweet, final Tweet tweet) {
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
	    b.setTitle("Retweet this to your followers?");
	    b.setPositiveButton("Retweet", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {
	        	if(!tweet.isRetweeted()){
		        	retweet(btRetweet, tweet);
	        	}else{
	        		Toast.makeText(getContext(), "Already retweeted", Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    b.setNegativeButton("Cancel", null);
	    b.create().show();
	 }
	
	protected void postFavorite(final Button btFav, final Tweet tweet) {

		client.postFavoriteChange(tweet.isFavorited(), String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				//finishActivity();
				if(!tweet.isFavorited()){
					tweet.setFavorited(true);
					setFavoritedIcon(btFav, true);
				}else{
					tweet.setFavorited(false);
					setFavoritedIcon(btFav, false);
				}
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(getContext(),
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void retweet(final Button btRetweet, final Tweet tweet) {
		
		if(!tweet.isRetweeted()){
			client.retweet(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject body) {
					
						tweet.setRetweeted(true);
						setRetweetedIcon(btRetweet, true);
					// ComposeTweetActivity.this.finish();
					super.onSuccess(body);
				}

				public void onFailure(Throwable e, JSONObject error) {
					// Handle the failure and alert the user to retry
					Toast.makeText(getContext(),
							"Excepton : " + e.getLocalizedMessage(),
							Toast.LENGTH_SHORT).show();
				}
			});
		}	
	}
	
	private void setRetweetedIcon(Button btRetweet, boolean isRetweeted){
		if(isRetweeted){
			btRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_done_24, 0, 0, 0);
		}else{
			btRetweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_retweet, 0, 0, 0);
		}
	}
	
	private void setFavoritedIcon(Button btFav, boolean isFavorited){
		if(isFavorited){
			btFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav_done_24_red, 0, 0, 0);
		}else{
			btFav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fav, 0, 0, 0);
		}
	}
	
	private void setUpItemClickListener(View v, final Tweet clicked){
		
			v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				launchDetailsActivity(clicked);
			}
		});
	}
	
	protected void launchDetailsActivity(Tweet clicked) {
		Intent detailView = new Intent(getContext(), TweetDetailActivity.class);
		detailView.putExtra("tweet", clicked);
		getContext().startActivity(detailView);
	}
	
	private void setUpImageViewListener(ImageView iv, final String screen_handle){
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchUserDetailActivity(screen_handle);
				
			}
		});
	}
	
	private void launchUserDetailActivity(String screen_name){
		Intent i = new Intent(getContext(), UserDetailActivity.class);
		i.putExtra("screen_name", screen_name.substring(1));
		getContext().startActivity(i);
	}
	
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}
	
	private String getWhenCreated(String time){
		
		//return "2m";
		final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		  SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
		  sf.setLenient(true);
		Date d = null;
		try {
			d = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long currentTime = System.currentTimeMillis();
		long created = d.getTime();
		String timeAgo = DateUtils.getRelativeDateTimeString(getContext(), created, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
		//String timeAgo = TimeUtils.millisToLongDHMS(currentTime - created);
		if(timeAgo.contains("/")){
			SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
		    SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
		    try {
				Date date = format1.parse(timeAgo);
				return format2.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(timeAgo.contains("Yesterday")){
			return "1d";
		}
		String result = timeAgo.substring(0,  timeAgo.indexOf(","));
		int indexOfSpace = result.indexOf(" ");
		String t1 = result.substring(0,indexOfSpace);
		String t2 = result.substring(indexOfSpace+1, indexOfSpace+2);
		return t1+t2;
		//return "2h";
	}
	
	 public static class ViewHolder
	    {
		 	TextView tvName;
	        TextView tvHandle;
	        TextView tvContent;
	        ImageView ivPosterImage;
	        SmartImageView ivMedia;
	        TextView tvWhen;
	        Button btRetweet;
	        Button btReply;
	        Button btFav;
	    }
}
