package com.codepath.apps.DataModel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String name;
	private String profileImageUrl;
	private String screenName;
	private String location;
	private String description;
	private String profileBannerUrl;
	private int followers_count;
	private int friends_count;
	private int favourites_count;
	private int listed_count;
	private int statuses_count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public static User fromJson(JSONObject user){
		User t = new User();
		 try {
	            // Deserialize json into object fields
	            t.name = user.getString("name");
	            t.screenName = "@" + user.getString("screen_name");
	            t.profileImageUrl = user.getString("profile_image_url");
	            t.description = user.getString("description");
	            t.location = user.getString("location");
	            t.favourites_count = user.getInt("favourites_count");
	            t.followers_count = user.getInt("followers_count");
	            t.friends_count = user.getInt("friends_count");
	            t.listed_count = user.getInt("listed_count");
	            t.statuses_count = user.getInt("statuses_count");
	            t.profileBannerUrl = user.getString("profile_banner_url");
	        } catch (JSONException e) {
	        	t.profileBannerUrl = null;
	            e.printStackTrace();
	        }
	        // Return new object
	        return t;
	}
	
	public static ArrayList<User> fromJSON(JSONArray jsonArray){
		ArrayList<User> users = new ArrayList<User>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            User usr = User.fromJson(businessJson);
            if (usr != null) {
                users.add(usr);
            }
        }

        return users;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFollowers_count() {
		return followers_count;
	}
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}
	public int getFriends_count() {
		return friends_count;
	}
	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}
	public int getFavourites_count() {
		return favourites_count;
	}
	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}
	public int getListed_count() {
		return listed_count;
	}
	public void setListed_count(int listed_count) {
		this.listed_count = listed_count;
	}
	public int getStatuses_count() {
		return statuses_count;
	}
	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}
	public String getProfileBannerUrl() {
		return profileBannerUrl;
	}
	public void setProfileBannerUrl(String backgroundImage) {
		this.profileBannerUrl = backgroundImage;
	}
}
