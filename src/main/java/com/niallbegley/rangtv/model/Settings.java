package com.niallbegley.rangtv.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.ListUtils;

public class Settings {
	Logger logger = LoggerFactory.getLogger(Settings.class);

	private boolean useOldReddit;
	private List<String> subreddits;
	
	public Settings() { }
	
	public Settings(boolean useOldReddit, List<String> subreddits) {
		this.useOldReddit = useOldReddit;
		this.subreddits = subreddits;
	}
	public static Settings defaultSettings() {
		return new Settings(false, Arrays.asList("videos", "youtubehaiku", "timanderic"));
	}
	public boolean isUseOldReddit() {
		return useOldReddit;
	}
	public void setUseOldReddit(boolean useOldReddit) {
		this.useOldReddit = useOldReddit;
	}
	public List<String> getSubreddits() {
		return subreddits;
	}
	public void setSubreddits(List<String> subreddits) {
		this.subreddits = subreddits;
	}
	
	public void validate() {
		//This regex will validate subreddits.  They must be between 3 and 21 characters and only contain letters, numbers and underscores (as long as they don't start with the underscore)
		String regex = "^(?![_])([a-zA-Z0-9_]){3,21}";
		Pattern compiledPattern = Pattern.compile(regex);
        
		//Loop through the chosen subreddits and remove empty or invalid entries
		Iterator<String> iter = subreddits.iterator();
		while(iter.hasNext()) {
			String subreddit = iter.next();
			if(subreddit.isEmpty()) {
				logger.info("Removed empty subreddit entry from settings");
				iter.remove();
			} else {
				Matcher matcher = compiledPattern.matcher(subreddit); 
		        if (!matcher.find()) {
					logger.info("Following subreddit failed regex validation: " + subreddit);
		        	iter.remove();
		        }
		        
			}
		}
	}
}
