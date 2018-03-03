/* 表格加载 */
var vm;
function loadFirst(){
	var opt = {
		url : '../template.json',
		type : 'GET',
		data : {}
	}
	var option = $.extend(opt, option);
	 $.ajax({
        url : option.url,
        type : option.type,
        data : option.data,
        dataType :'json',
        success : function(res){
        	vm = avalon.define({
        		$id : 'my-app',
		        jumpto : '',
		        table : res
        	})
        	avalon.scan(document.body);
        	console.log(vm)
        }
    })
}