package com.codepath.apps.activities;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.DataModel.User;
import com.codepath.apps.restclienttemplate.R;
import com.squareup.picasso.Picasso;

public class UserListAdapter extends ArrayAdapter<User> {
	private Context context;
	public UserListAdapter(Context context, ArrayList<User> users){
		super(context, 0, users);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		User user = this.getItem(position);
		if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_user, null);
        }
		convertView.setTag(convertView.toString());
        // Lookup views within item layout
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvUserSN = (TextView) convertView.findViewById(R.id.tvUserSN);
        TextView tvUserDesc = (TextView) convertView.findViewById(R.id.tvUserDesc);
        ImageView ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
        tvUserName.setText(user.getName());
        tvUserSN.setText(user.getScreenName());
        tvUserDesc.setText(user.getDescription());
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivUserImage);

        setUpItemClickListener(convertView, user);
        
        return convertView;
	}

	private void setUpItemClickListener(View v, final User user) {
		
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchUserProfileActivity(user.getScreenName());
			}
		});
	}

	protected void launchUserProfileActivity(String screenName) {
		Intent i = new Intent(context, UserDetailActivity.class);
		i.putExtra("screen_name", screenName);
		context.startActivity(i);
		
	}

}
