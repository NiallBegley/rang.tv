package com.niallbegley.rangtv.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.niallbegley.rangtv.model.Settings;
import com.niallbegley.rangtv.model.json.TopLevelObject;
import com.niallbegley.rangtv.model.json.Video;
import com.niallbegley.rangtv.model.json.VideoParent;

@Controller
public class VideosController {

	 private Logger logger = LoggerFactory.getLogger(VideosController.class);
	 
	 @Autowired
	 protected RestOperations restOperations;
	
	 private ObjectWriter objectWriter = null;
	 private ObjectReader objectReader = null;
			 ;
	 @PostConstruct
	 public void initialize() {
		 ObjectMapper mapper = new ObjectMapper();
		 objectWriter = mapper.writerWithDefaultPrettyPrinter();
		 objectReader = mapper.reader();
	 }
	 
	 //
	 @GetMapping("/")
	 public String getVideos(Model model,  @CookieValue(value="settings", defaultValue="") String settingsCookie, @RequestParam(value="subreddit", required=false) String subreddit) {
		 
		 if(subreddit == null || subreddit.isEmpty()) {
			 subreddit = "videos";
		 }
		 
		 Settings settings = Settings.defaultSettings();
		 
		 if(!settingsCookie.isEmpty()) {
			 byte[] cookieData = Base64.getDecoder().decode(settingsCookie);
			 try {
				 settings = objectReader.readValue(cookieData, Settings.class);
			} catch (IOException e) {
				logger.error("Failed to parse settings from cookie - using defaults", e);
			}
		 }
		 
		 model.addAttribute("subreddits", settings.getSubreddits());
		 model.addAttribute("subreddit", subreddit);
		 
		 List<Video> videos = getVideos(subreddit);
		 
		 if(videos.size() > 0) {
			 model.addAttribute("youtube_url", videos.get(0).getYoutubeId());
		 } 

		 model.addAttribute("videos", videos);
		 model.addAttribute("settings", settings);
		 
		 return "index";
	 }
	 
	 @PostMapping("/settings")
	 public void settingsSubmit(@ModelAttribute Settings settings, Model model, HttpServletResponse response) {
		 
		 settings.validate();
		 try {
			 String cookieData = objectWriter.writeValueAsString(settings);
			 
			 Cookie cookie = new Cookie("settings", new String(Base64.getEncoder().encode(cookieData.getBytes())));
			 response.addCookie(cookie);
		} catch (JsonProcessingException e1) {
			logger.error("Failed to parse settings", e1);
		}
		 
		 try {
			response.sendRedirect("/");
		} catch (IOException e) {
			logger.error("Failed to redirect user from settings", e);
		}
	 }
	 private List<Video> getVideos(String subreddit) {
		 UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
				 String.format("https://www.reddit.com/r/%s/hot.json", subreddit))
			        .queryParam("limit", 100);
			        
		 TopLevelObject topLevel = restOperations.getForEntity(builder.build().toUri(), TopLevelObject.class).getBody();
		 
		 List<Video> videos = topLevel.getData().getChildren().stream().map(VideoParent::getVideo).collect(Collectors.toList());
		 
		 //Remove videos that are self posts or non-youtube submissions
		 Iterator<Video> iterator = videos.listIterator();
		 while(iterator.hasNext()) {
			 Video video = iterator.next();
			 if(video.isSelf()) {
				 iterator.remove();
			 } else if(video.getYoutubeId() == null) {
				 iterator.remove();
			 }
		 }

		 return videos;
	 }
}
