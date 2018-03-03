	var Public = function() {
		var Public = {};
	}

	//点击的时候显示或隐藏相应区域
	Public.showOrHiden = function(){
		$("body").delegate(".box2 .tabs .tab", "click", function(){
			var sibTarget = $(this).parent("li").siblings().children(".tab");
			sibTarget.each(function(){
				var t = $(this).attr("data-item");
				var st = $("#" + t);
				var stt = $("#" + t + "2");
				st.removeClass('cur');
				stt.removeClass('active');
			});
			sibTarget.removeClass("active");
			var target = $(this).attr("data-item");
			var tt = $("#" + target);
			var ttt = $("#" + target + "2");
			tt.addClass('cur');
			ttt.addClass('active').removeClass('type-first type-last');
			$(this).addClass("active");
			if (target == "userReg") {
				$(".devices .device").last().addClass('type-first').removeClass('active type-last').siblings().removeClass('type-first');
				ttt.next().addClass('type-last').siblings().removeClass('type-last');
			} else if (target == "bigEvent") {
				$(".devices .device").first().addClass('type-last').removeClass('active type-first').siblings().removeClass('type-last');
				ttt.prev().addClass('type-first').siblings().removeClass('type-first');
			} else {
				ttt.prev().addClass('type-first').siblings().removeClass('type-first');
				ttt.next().addClass('type-last').siblings().removeClass('type-last');
			}
		});
	};

	Public.toggleBtn = function() {
		$("body").delegate('.control-btn div', 'click', function() {
			var all = $(".devices .device")
			var ele = $(".devices .active");
			var tarData = ele.attr("id");

			if ($(this).hasClass('s-prev')) {
				if (tarData == "userReg") {
					ele.addClass('type-last').removeClass('active').siblings().removeClass('type-last');
					$(".devices .device").last().addClass('active').siblings().removeClass('active');
					$(".devices .device").eq(4).addClass('type-first').siblings().removeClass('type-first');
				} else if (tarData == "payValid") {
					ele.addClass('type-last').removeClass('active').siblings().removeClass('type-last');
					ele.prev().addClass('active').removeClass('type-first').siblings().removeClass('active');
					$(".devices .device").eq(5).addClass('type-first').siblings().removeClass('type-first');

				} else {
					ele.addClass('type-first').removeClass('active').siblings().removeClass('type-first');
					ele.prev().addClass('active').removeClass('type-first').siblings().removeClass('active');
					ele.prev().prev().addClass('type-last').siblings().removeClass('type-last');					
				}
			} else {

			}
		});

	}

	Public.dialogHtml = function() {
		var html =  '<div class="idx-dialog-bg"> </div>' +
				    '<div class="idx-dialog">' +
				        '<div class="idx-dialog-wp">' +
				            '<h2>代理商申请<i></i></h2>' +
				            '<form id="agentApplay" action="/agentApply" method="post">' +
				                '<div class="group">' +
				                    '<label for="cmpy-name"><b>*</b>企业名称</label>' +
				                    '<input type="text" id="cmpy-name" name="companyName" maxlength="60" value=""/>' +
				                    '<span class="companyName error"></span>' +
				                '</div>' +
				                '<div class="group">' +
				                    '<label for="con-user"><b>*</b>联系人</label>' +
				                    '<input type="text" id="con-user" name="realName" maxlength="60"/>' +
				                    '<span class="realName error"></span>' +
				                '</div>' +
				                '<div class="group">' +
				                    '<label for="email"><b>*</b>邮箱</label>' +
				                    '<input type="text" id="email" name="email" maxlength="100"/>' +
				                    '<span class="email error"></span>' +
				                '</div>' +
				                '<div class="group">' +
				                    '<label for="phone-num"><b>*</b>手机号</label>' +
				                    '<input type="text" id="phone-num" name="mobile" maxlength="20"/>' +
				                    '<span class="mobile error"></span>' +
				                '</div> ' +
				                '<div class="group">' +
				                    '<label for="city"><b>*</b>城市</label>' +
				                    '<input type="text" id="city" name="city" maxlength="20"/>' +
				                    '<span class="city error"></span>' +
				                '</div>' +
								'<div class="group">' +
									'<label for="addr"><b>*</b>地址</label>' +
									'<input type="text" id="addr" name="address" maxlength="200"/>' +
									'<span class="address error"></span>' +
								'</div>' +
				                '<div class="group">' +
				                    '<label for="ab-more">备注</label>' +
				                    '<input type="text" id="ab-more" name="remark" maxlength="100"/>' +
				                    '<span class="error"></span>' +
				                '</div>' +
				                '<div class="group btn">' +
				                    '<input id="submitAgentApplay" type="button" value="提交申请" />' +
				                '</div>' +
				            '</form>' +
				        '</div>' +
				    '</div>'
		$("#agentApplayBox").html(html);
		$("body").delegate('.idx-dialog h2 i', 'click', function(event) {
			$(".idx-dialog-bg").hide();
			$(".idx-dialog").hide();
			document.getElementById("agentApplay").reset();// 充值表单
		});

		// $("body").delegate('#submitAgentApplay', 'click', function(event) {
		$("body #submitAgentApplay").click(function(event) {
			var options = {
					beforeSubmit : function(e) {
						var data = $("#agentApplay").serializeArray();
						if(!formValidate(data)){
							return false;
						}
						$("#submitAgentApplay").attr("disabled", true);
					},
					success : function(result) {
						$("#submitAgentApplay").attr("disabled", false);
						if(result.success){
							$(".idx-dialog-bg").hide();
							$(".idx-dialog").hide();
							// $("#agentApplayBox").html("");
							layer.alert('代理商申请完成！', {icon: 1});
						}else{
							if(result.errorcode == 1){
								$(".email.error").html(result.msg).show();
							}
							if(result.errorcode == 2){
								$(".mobile.error").html(result.msg).show();
							}
						}
						
						
					},
					async: false,
					url : "/agentApply?" + Math.random(),
					type : "post",
					timeout : 30000
				};
			$("#agentApplay").ajaxSubmit(options);
		});
		
		function formValidate(data){
			var companyName = $("#cmpy-name").val();
			var realName = $("#con-user").val();
			var email = $("#email").val();
			var mobile = $("#phone-num").val();
			var address = $("#addr").val();
			var city = $("#city").val();
			var result = true;
			$.each(data, function (index, ele) {
				// ele == this 
				$("."+ele.name+".error").hide();
				
				if(ele.name != "remark"){
					if($.trim(ele.value) == ""){
						$("."+ele.name+".error").html("必须填写").show();
						result = false;
					}
				}
				  
				if(ele.name == "email"){
					if(!Validate.isValidEmail(email)){
						$("."+ele.name+".error").html("请输入合法的邮箱地址").show();
						result = false;
					}
				}
				
				if(ele.name == "mobile"){
					if(!Validate.isValidMobile(mobile)){
						$("."+ele.name+".error").html("请输入合法的手机号码").show();
						result = false;
					}
				}
				
			});
			
			return result;
		}
		
		
	}
	
	$(function() {
		Public.showOrHiden();
		$(".banner_box").hover(function() {
			$(".swiper-btn").show();
		}, function() {
			$(".swiper-btn").hide();
		});
//		$(".login-con .for-password span").click(function() {
//			$(this).children('i').toggleClass('selected');
//		});
		
		$("body").delegate('.slide2 a,.nav .alert-d', 'click', function(event) {
			Public.dialogHtml();
			$(".idx-dialog-bg").show();
			$(".idx-dialog").show();
		});
		
		

	})