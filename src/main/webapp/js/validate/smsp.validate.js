

var Validate = (function ($, win, JSON) {
	var Validate = {};
	
	Validate.clearInput = function(content){
		var inputs = content.find("[name]");
		inputs.val("");
		inputs.text("");
	};

	//验证是否为正确的手机号码
	Validate.isValidMobile = function(mobile){
		var regList = [];
		regList.push(/^00\d{8,20}$/);
		regList.push(/^13\d{9}$/);
		regList.push(/^14[5|7|9]\d{8}$/);
		regList.push(/^15[0|1|2|3|5|6|7|8|9]\d{8}$/);
		regList.push(/^170[0|1|2|3|4|5|6|7|8|9]\d{7}$/);
		regList.push(/^17[1|5|6|7|8]\d{8}$/);
		regList.push(/^173\d{8}$/);
		regList.push(/^18\d{9}$/);
		
		for(var pos = 0; pos < regList.length; pos++){
			if(regList[pos].test(mobile)){
				return true;
			}
		}
		return false;
	};
	
	
	
	//验证是否为正确的邮箱
	Validate.isValidEmail = function(email){
		var reg = /^[a-zA-Z0-9\._-]+@[a-zA-Z0-9_-]+\.[a-zA-Z0-9_-]{2,4}/;
		if(reg.test(email)){
			return true;
		}
		 return false;
	};
	
	Validate.isValidValue = function(value){
		value = $.trim(value);
		if(value.length === 0){
			return false;
		}else{
			return true;
		}
	};
	
	Validate.isCheckCodeRight = function(url, value){
		var result;
 		$.ajax({
			url: url,
			data: {
				randCheckCode : value
			},
			async: false,
			success: function(data){
				result = data.result;
			}
		});
 		return result;
	}

	return Validate;
})($, window, JSON);
