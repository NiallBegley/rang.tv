package com.niallbegley.rangtv.model.json;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Video {
	public enum VideoType {
		YOUTUBE("youtube"),
		REDDIT("reddit");

		private final String val;

		VideoType(String val) {
			this.val = val;
		}

		public String getType() {
			return val;
		}
	}

	private String id;
	private String title;
	private String url;
	private String permalink;
	private String thumbnail;
	private Logger logger = LoggerFactory.getLogger(Video.class);
	private VideoType type;

	@JsonProperty(value="is_self")
	private boolean isSelf;

	private String fallbackURLVideo;
	private String fallbackURLAudio;
	
	private String domain;
	
	public Video() {} 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		
		this.title =  StringEscapeUtils.unescapeHtml(title);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = String.format("https://old.reddit.com/%s", permalink);
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean isSelf() {
		return isSelf;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getFallbackURLVideo() {
		return fallbackURLVideo;
	}

	
	public String getFallbackURLAudio() {
		return fallbackURLAudio;
	}

	public String getType() {
		return type.getType();
	}

	@JsonProperty("media")
	public void unpackRedditVideoURL(Map<String, Object> media) {
		if(media != null) {
			if(media.get("reddit_video") != null && media.get("reddit_video") instanceof Map<?,?>) {
				Map<String, String> redditVideo = (Map<String, String>) media.get("reddit_video");
				if(redditVideo != null) {
					fallbackURLVideo = redditVideo.get("fallback_url");
					type = VideoType.REDDIT;
					if(fallbackURLVideo != null) {
						fallbackURLAudio = fallbackURLVideo.replaceAll("DASH_\\d+", "DASH_audio");
					}
				}
			}
		}

	}
	public String getYoutubeId() {
		String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
			type = VideoType.YOUTUBE;
             return matcher.group();
        }
        
        return null;
	}

	// public String getRedditVideoId() {

	// }

	public boolean isSupported() {
		if(getYoutubeId() != null) {
			return true;
		} else if(fallbackURLVideo != null) {
			return true;
		}

		return false;
	}
	
	public boolean requiresPlaceholder() {
		return thumbnail.equals("nsfw") || thumbnail.equals("spoiler") || thumbnail.equals("default");
	}

	public int getStartTime() {
		String pattern = "[?]{1}t=(\\d+)";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
			try {
             	return Integer.parseInt(matcher.group(1));
			} catch (NumberFormatException e) {
				logger.error("Failed to parse timestamp in youtube URL", e);
			}
		}
			return 0;
        
	}
}
