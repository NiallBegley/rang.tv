$(function(){
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
	  var data = $("#" + sender.id).find("#data");
	  
	  currentlySelected = sender;
	  sender.classList.add("selected");
	  
	  $("#title").text(data.attr("title"));
	  $("#title").attr("title", data.attr("title"));
	  $("#title").attr("href", data.attr("permalink"));
	  $("#video_container")[0].scroll(getPosition(sender).x,0);
  }
  
  function changeVideo(sender, videoId) {
	  if(currentlySelected != null) {
		  currentlySelected.classList.remove("selected");
	  }

	  selectVideo(sender);

	  player.loadVideoById(videoId, 0);
	  
	  
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