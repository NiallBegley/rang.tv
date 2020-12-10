package com.niallbegley.rangtv.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestOperations;

import com.niallbegley.rangtv.model.TopLevelObject;
import com.niallbegley.rangtv.model.Video;
import com.niallbegley.rangtv.model.VideoParent;

@Controller
public class VideosController {

	 @Autowired
	 protected RestOperations restOperations;
	 
	 @GetMapping("/")
	 public String getVideos(Model model) {
		 
		 TopLevelObject test = restOperations.getForEntity("https://www.reddit.com/r/videos/hot.json", TopLevelObject.class).getBody();
		 
		 List<Video> videos = test.getData().getChildren().stream().map(VideoParent::getVideo).collect(Collectors.toList());
		 
		 videos.remove(0);
		 model.addAttribute("youtube_url", videos.get(0).getYoutubeId());
		 model.addAttribute("videos", videos);
		 
		 return "index";
	 }
}
