/**
 * 
 * @authors csy
 * @date    2017-02-04 12:05:56
 */
function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return unescape(r[2]); return null; 
} 
$(function(){
	var count = 0;
	var param = getQueryString("v");
	if(!!param){
		$(".last-page").remove();
		$(".page1-logo").remove();
		document.title = '彩印新产品.让您与众不同';
	}
	var mySwiper = new Swiper ('.swiper-container', {
		lazyLoading : true,
		lazyLoadingInPrevNext : true,
		lazyLoadingInPrevNextAmount : 5,
		direction : 'vertical',	
		mousewheelControl : true,
		onInit: function(swiper){ //Swiper2.x的初始化是onFirstInit
			swiperAnimateCache(swiper); //隐藏动画元素 
			
		}, 
		onSlideChangeEnd: function(swiper){ 
			
			swiperAnimate(swiper); //每个slide切换结束时也运行当前slide动画
		},
		onLazyImageReady: function(swiper,slide,image){
		    count += 1;  
		  
		    if(count === 17){
		    	
		    	$('.load').addClass('animated fadeOut');
			    setTimeout(function(){
			        $('.load').remove();
			        swiperAnimate(swiper); //初始化完成开始动画
			    }, 1200);
		    }
		}
	})   


	//audio 操作
	$(".music-switch").click(function(){
		var audio = $("audio")[0];

		if(audio.paused === true){
			audio.play();
			$(this).removeClass("music-switch-off").addClass("music-switch-on");
			$(this).addClass("rotate360");
		} else {
			audio.pause();
			$(this).removeClass("music-switch-on").addClass("music-switch-off");
			$(this).removeClass("rotate360");
		}
	})


})
     

