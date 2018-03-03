/**
 * Created by caosiyuan on 2017/7/15.
 */
(function(){
    /*
    * params @target jquery包装过的DOM对象
    * */
    var auth = {
        isNull : function(val){
            if(val === "" || val === undefined || val === null){
                return true;
            } else {
                return false;
            }
        },
        isUsername : function(val){
            if(val.length > 20){
                return false;
            } else {
                return true;
            }
        },
        isNum : function(val){
            var i = /^[0-9]+$/;
            return i.test(val);
        },
        isMobile : function(val){
            var i = /^1\d{10}$/;
            return i.test(val);
        },
        isPassword : function(val){
            return /^[0-9A-Za-z]{8,16}$/.test(val);
        },
        isAddress : function(val){
            if(val.length > 50){
                return false;
            } else {
                return true;
            }
        },
        isEmail : function (val) {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(val);
        },
        isPrice : function(val){
            return /^0\.[0-9]{1,4}$/.test(val);
        },
        isIp : function (val) {
            return /^((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/.test(val);
        },
        isPort : function(val){
            if(!this.isNum(val)){
                return false;
            } else if (val > 65535){
                return false;
            }
            return true;
        },
        isExtend : function(val){
            if(!this.isNum(val) && this.isNull(val)){
                return false;
            } else if (val > 10 || val < 0){
                return false;
            }
            return true;
        },
        isNodeId : function(val){
            return /^[0-9A-Za-z]{0,10}$/.test(val)
        }

    }

    window._Auth = auth;
})(window)