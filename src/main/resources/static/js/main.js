//
var currentlySelected;
var playerdiv;
$(function(){
			
	
	//Hide the youtube player - we'll unhide it if we need it
	  playerdiv = $("#player")[0];
	  playerdiv.style.display = "block";
	  var modal = $("#modalView")[0];
	  var span = $("span.close")[0];
	  var settingsGear = $("#settings_gear")[0];
	  
      $('#subreddit_selector').on('change', function () {
          var subreddit = $(this).val(); 
          
          if (subreddit) { 
          	window.location = document.location.origin + "?subreddit=" + subreddit;
          }
          return false;
      });
      
      span.onclick = function() {
        modal.style.display = "none";
      }
      
      window.onclick = function(event) {
        if (event.target == modal) {
          modal.style.display = "none";
        }
      }
      
      settingsGear.onclick = function() {
    	  modal.style.display = "block";
      }

    });

	function handle(e){
        if(e.keyCode === 13){
            e.preventDefault(); 
            addSubreddit();
        }
    }
	
  function addSubreddit() {
	  var index = parseInt($("#subreddits_table input:last").attr("index")) + 1;
	  
	  var subreddit = $("#newSubredditTextField")[0].value;
	  if(subreddit && subreddit.trim().length !== 0) {
		  $('#subreddits_table tr:last').after('<tr><td><input id="subreddits' + index + '" index="' + index + '" name="subreddits[' + index + ']" value="' + subreddit + '"></tr></td>');
		  $("#newSubredditTextField")[0].value = "";
	  }
  }
  
  function removeSubreddit(sender) {
	  var index = parseInt(sender.attributes["index"].value);
	  $("#subreddits" + index).hide();
	  $("#subreddits" + index).val("");
  }
  
  
  function selectVideo(sender) {
	  var data = $(sender).find("#data");
	  
	  var currentlySelected = sender;
	  sender.classList.add("selected");
	  
	  $("#title").text(data.attr("title"));
	  $("#title").attr("title", data.attr("title"));
	  $("#title").attr("href", data.attr("permalink"));
	  $("#video_container")[0].scroll(getPosition(sender).x,0);

	  if(data.attr("type") == "reddit") {
		playerdiv.style.display = "none";
	  } else {
		playerdiv.style.display = "block";
	  }
  }
  
  function changeRedditVideo(sender, video, audio) {
	  
	  selectVideo(sender);

	  var container = $("#player_container")[0];

	  var oldVideo = $("#player_container video");
	  if(oldVideo != null) {
	  	oldVideo.remove();
	  }
	  var oldAudio = $("#player_container audio");
	  if(oldAudio != null) {
		oldAudio.remove();
	  }

	  var video = $('<video />', {
		id: 'video',
		src: video,
		type: 'video/mp4',
		controls: true,
	});
	video.appendTo($("#player_container"));

	//Adapted from https://github.com/ubershmekel/redditp/blob/3641c615abd3fe56f6d8f9332696cfec2777026f/js/script.js#L788
	$("<audio><source src='" + audio + "' type='audio/aac'/></audio>").appendTo($("#player_container"));

	var $audioTag = $("audio", $("#player_container")).get(0);
	var $videoTag = $("video", $("#player_container")).get(0);

	// sync reddit audio and video tracks
	$audioTag.currentTime = $videoTag.currentTime;
	$videoTag.onplay = function () {
		$audioTag.play();
	};
	$videoTag.onpause = function () {
		$audioTag.pause();
	};
	$videoTag.onseeking = function () {
		$audioTag.currentTime = $videoTag.currentTime;
	}

	$videoTag.onended = function() {
		var nextElement = currentlySelected.nextElementSibling;
		if(nextElement != null) {
			nextElement.click();
		}
	}
	
  }

  function changeVideo(sender, videoId, startTime) {
	  if(currentlySelected != null) {
		  currentlySelected.classList.remove("selected");
	  }
	  
	  selectVideo(sender);

	  player.loadVideoById(videoId, startTime);
	  
	  
  }
  
  //Adapted from https://www.kirupa.com/html5/get_element_position_using_javascript.htm
  function getPosition(el) {
	  var xPos = 0;
	  var yPos = 0;
	 
	  while (el) {
	      xPos += (el.offsetLeft - el.scrollLeft + el.clientLeft);
	      yPos += (el.offsetTop - el.scrollTop + el.clientTop);
	 
	      el = el.offsetParent;
	  }
	  
	  return {
	    x: xPos,
	    y: yPos
	  };
	}