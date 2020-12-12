package com.niallbegley.rangtv.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import com.niallbegley.rangtv.model.Subreddit;
import com.niallbegley.rangtv.model.json.TopLevelObject;
import com.niallbegley.rangtv.model.json.Video;
import com.niallbegley.rangtv.model.json.VideoParent;

@Controller
public class VideosController {

	 @Autowired
	 protected RestOperations restOperations;
	 
	 @GetMapping("/")
	 public String getVideos(Model model, @RequestParam(value="subreddit", required=false) String subreddit) {
		 
		 if(subreddit == null || subreddit.isEmpty()) {
			 subreddit = "videos";
		 }
		 
		 List<String> subreddits = Arrays.asList("videos", "youtubehaiku", "timanderic");

		 model.addAttribute("subreddits", subreddits);
		 model.addAttribute("subreddit", subreddit);
		 
		 List<Video> videos = getVideos(subreddit);
		 
		 model.addAttribute("youtube_url", videos.get(0).getYoutubeId());
		 model.addAttribute("videos", videos);
		 
		 return "index";
	 }
	 
	 private List<Video> getVideos(String subreddit) {
		 UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.reddit.com/r/" + subreddit + "/hot.json")
			        .queryParam("limit", 100);
			        
		 TopLevelObject test = restOperations.getForEntity(builder.build().toUri(), TopLevelObject.class).getBody();
		 
		 List<Video> videos = test.getData().getChildren().stream().map(VideoParent::getVideo).collect(Collectors.toList());
		 
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
