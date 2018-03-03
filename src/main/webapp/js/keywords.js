var tblRoot;
/*
 * 函数: makeTree
 * 注释: 将关键字生成一颗树
 */
function makeTree(strKeys) {
    var arrKeys = strKeys.split("");
    var tblCur = tblRoot = {};
    var key;

    for (var i = 0, n = arrKeys.length; i < n; i++) {
        key = arrKeys[i];

        if (key == ';')		//完成当前关键字
        {
            tblCur.end = true;
            tblCur = tblRoot;
            continue;
        }

        if (key in tblCur)	//生成子节点
            tblCur = tblCur[key];
        else
            tblCur = tblCur[key] = {};
    }

    tblCur.end = true;		//最后一个关键字没有分割符
}

/*
 * 函数: search
 * 注释: 标记出内容中关键字的位置
 */
function search(content) {
    var tblCur;
    var i = 0;
    var n = content.length;
    var p, v;
    var arrMatch = [];

    while (i < n) {
        tblCur = tblRoot;
        p = i;
        v = 0;

        for (; ;) {
            if (!(tblCur = tblCur[content.charAt(p++)])) {
                i++;
                break;
            }

            if (tblCur.end)		//找到匹配关键字
                v = p;
        }

        if (v)					//最大匹配
        {
            arrMatch.push(i - 1, v);
            i = v;
        }
    }

    return arrMatch;
}

/*
 * 还原关键字色彩
 */
function restoreContent(str){
    str = str.replace(/\<em\s[^\>]*\>/,"");
    str = str.replace(/\<\/em\>/,"");
    return str;
}

function handleKeyWords(contentEle,txtKeys) {
    var strContent = $(contentEle).html();
    // var txtKeys = $(keyEle).html();
    var arrMatch;
    var arrHTML = [];
    var strHTML;
    var mid;
    var p = 0;
    makeTree(txtKeys);
	/*
	 * 开始搜索!
	 */
    var t = +new Date();

    arrMatch = search(strContent);

	/*
	 * 标记关键字
	 */
    for (var i = 0, n = arrMatch.length; i < n; i += 2) {
        mid = arrMatch[i];
        arrHTML.push(strContent.substring(p, mid),
            "<em style='color: #ff5454'>",
            strContent.substring(mid, p = arrMatch[i + 1]),
            "</em>");
    }
    arrHTML.push(strContent.substring(p));

    strHTML = arrHTML.join("").replace(/\n/g, "<br>");

	/*
	 * 显示结果
	 */
    $(contentEle).html(strHTML);
}
