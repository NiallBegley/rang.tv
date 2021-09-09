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
import com.niallbegley.rangtv.model.Settings.Sort;
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
		 
		 Settings defaultSettings = Settings.defaultSettings();
		 Settings settings = null;

		 if(!settingsCookie.isEmpty()) {
			 byte[] cookieData = Base64.getDecoder().decode(settingsCookie);
			 try {
				 settings = objectReader.readValue(cookieData, Settings.class);
			} catch (IOException e) {
				logger.error("Failed to parse settings from cookie - using defaults", e);
			}

			//Check and account for incomplete cookie data just in case something happens
			if(settings.getSorting() == null || settings.getSubreddits() == null) {
				settings = defaultSettings;
			 }

		 } else {
			 settings = defaultSettings;
		 }
		 
		 model.addAttribute("subreddits", settings.getSubreddits());
		 model.addAttribute("subreddit", subreddit);
		 
		 List<Video> videos = getVideos(subreddit, settings.getSorting().toString());
		 
		 if(videos.size() > 0) {
			 Video firstVideo = videos.get(0);
			 if(firstVideo.getYoutubeId() != null) {
			 	model.addAttribute("initial_url", firstVideo.getYoutubeId());
			 }
			 else if(firstVideo.getFallbackURLVideo() != null) {
				model.addAttribute("initial_url", firstVideo.getFallbackURLVideo());
			 }
			 model.addAttribute("initial_type", firstVideo.getType());
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
	 private List<Video> getVideos(String subreddit, String sorting) {
		 UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
				 String.format("https://www.reddit.com/r/%s/%s.json", subreddit, sorting.toLowerCase()))
			        .queryParam("limit", 100);
			        
		 TopLevelObject topLevel = restOperations.getForEntity(builder.build().toUri(), TopLevelObject.class).getBody();
		 
		 List<Video> videos = topLevel.getData().getChildren().stream().map(VideoParent::getVideo).collect(Collectors.toList());
		 
		 //Remove videos that are self posts or non-supported submissions
		 Iterator<Video> iterator = videos.listIterator();
		 while(iterator.hasNext()) {
			 Video video = iterator.next();
			 if(video.isSelf()) {
				 iterator.remove();
			 } else if(!video.isSupported()) {
				 iterator.remove();
			 }
		 }

		 return videos;
	 }
}
