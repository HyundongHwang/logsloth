"use strict";
document.write("<script src='LsGitRepo.js'></script>");
document.write("<script src='LsLogLineInfo.js'></script>");
document.write("<script src='LsUtil.js'></script>");
document.write("<script src='LsConst.js'></script>");

let _gitRepoArray = [];
let _srcViewCache = {}
let _curSrcFileName;
let _curLlInfo;
let _llInfoArray = [];
let _curLogFileTxt;

let _filterLogType;
let _filterArray = [];
let _filterViewOnlyEnterLogs;
let _filterExcludeTidArray = [];
let _filterExcludeFileFuncArray = [];
let _filterMaxSameLogCount = 0;
let _filterLogLineNumberStart = 0;

let _lastFilteredLineArray = [];
let _orderedTidArray = [];
let _tidCallDepthMap = {};

let _stopLogLoadingFlag = false;


function _click_btn_back() {
    if (_curLlInfo === undefined) {
        return;
    }

    let idx = _llInfoArray.indexOf(_curLlInfo);
    let newIdx = (idx - 1 + _llInfoArray.length) % _llInfoArray.length;
    _curLlInfo = _llInfoArray[newIdx];
    _syncSrcLog();
}

function _click_btn_forward() {
    if (_curLlInfo === undefined) {
        return;
    }

    let idx = _llInfoArray.indexOf(_curLlInfo);
    let newIdx = (idx + 1) % _llInfoArray.length;
    _curLlInfo = _llInfoArray[newIdx];
    _syncSrcLog();
}

function _click_btn_upload_log_file() {
    LsUtil.uploadTxt2Nubes(_curLogFileTxt,
        (url) => {
            let urlParams = new URLSearchParams(window.location.search);
            urlParams.set("logFilePath", url);
            window.location.search = urlParams.toString();
        },
        (url) => {
            console.warn(`_clickBtnUploadLogFile::onError url[${url}]`);
        });
}

function _click_btn_apply_oss_repo() {
    let urlParams = new URLSearchParams(window.location.search);
    let gitRepoArrayStr = $("#txt_git_repo_array").val();
    urlParams.set("gitRepoArray", encodeURIComponent(gitRepoArrayStr));
    window.location.search = urlParams.toString();
}

function _change_txt_log_line_number() {
    let logLineNum = parseInt($("#txt_log_line_number").val());

    for (let k in _llInfoArray) {
        let llInfo = _llInfoArray[k];

        if (logLineNum >= llInfo.logLineNum) {
            _curLlInfo = llInfo;
            break
        }
    }

    _syncSrcLog();
}

function _document_keyup(event) {
    let keycode = event.keyCode || event.which;
    let idx = _llInfoArray.indexOf(_curLlInfo);

    if (keycode === 37 || keycode === 38) { //LEFT, UP
        let newIdx = (idx - 1 + _llInfoArray.length) % _llInfoArray.length;
        _curLlInfo = _llInfoArray[newIdx];
        _syncSrcLog();
    } else if (keycode === 39 || keycode === 40) { //RIGHT, DOWN
        let newIdx = (idx + 1) % _llInfoArray.length;
        _curLlInfo = _llInfoArray[newIdx];
        _syncSrcLog();
    }
}

function _parseUrl() {
    let urlParams = new URLSearchParams(window.location.search);
    _gitRepoArray = [];

    if (urlParams.has("gitRepoArray")) {
        let gitRepoArrayStr = urlParams.get("gitRepoArray");
        let gitRepoArrayStrDec = decodeURIComponent(gitRepoArrayStr);
        $("#txt_git_repo_array").val(gitRepoArrayStrDec);
        let strArray = gitRepoArrayStrDec.split(",");

        for (let k in strArray) {
            let gitRepo = new LsGitRepo().parse(strArray[k].trim());
            _gitRepoArray.push(gitRepo);
        }
    }

    if (urlParams.has("logFilePath")) {
        let logFilePath = urlParams.get("logFilePath");
        let logFilePathDec = decodeURIComponent(logFilePath);

        $.ajax({
            type: "GET",
            url: logFilePathDec,
            dataType: "text",
            success: function (data, status, jqXHR) {
                _curLogFileTxt = data;
                _renderLogView(_curLogFileTxt);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error(`failed !!! \n${textStatus} \n${contentsUrl}`);
            }
        });
    }


    if (urlParams.has("filterLogType")) {
        _filterLogType = decodeURIComponent(urlParams.get("filterLogType"));
        $(`input[value=${_filterLogType}]`).prop("checked", true);
    }

    if (urlParams.has("filterStr")) {
        let filterStr = decodeURIComponent(urlParams.get("filterStr"));
        $("#txt_filter_str").val(filterStr);

        try {
            _filterArray = JSON.parse(filterStr);
        } catch (e) {
        }

        if ((_filterArray === undefined) || (_filterArray.length === 0)) {
            _filterArray = [];

            if (filterStr.includes("&&")) {
                _filterArray = filterStr.split("&&").map((item) => {
                    return item.trim();
                });
            } else if (filterStr.includes("||")) {
                let orItem = filterStr.split("||").map((item) => {
                    return item.trim();
                });
                _filterArray.push(orItem);
            } else {
                _filterArray.push(filterStr.trim());
            }
        }
    }

    if (urlParams.has("filterViewOnlyEnterLogs")) {
        let filterViewOnlyEnterLogs = decodeURIComponent(urlParams.get("filterViewOnlyEnterLogs"));

        if (filterViewOnlyEnterLogs === "true") {
            _filterViewOnlyEnterLogs = true;
            $("#cb_filter_view_only_enter_logs").prop("checked", true);
        } else {
            _filterViewOnlyEnterLogs = false;
            $("#cb_filter_view_only_enter_logs").prop("checked", false);
        }
    }

    if (urlParams.has("filterExcludeTidArray")) {
        let str = decodeURIComponent(urlParams.get("filterExcludeTidArray"));
        _filterExcludeTidArray = JSON.parse(str);
    }

    if (urlParams.has("filterExcludeFileFuncArray")) {
        let str = decodeURIComponent(urlParams.get("filterExcludeFileFuncArray"));
        _filterExcludeFileFuncArray = JSON.parse(str);
    }

    //_filterMaxSameLogCount
    if (urlParams.has("filterMaxSameLogCount")) {
        let str = decodeURIComponent(urlParams.get("filterMaxSameLogCount"));
        _filterMaxSameLogCount = parseInt(str);

        if (_filterMaxSameLogCount > 0) {
            $("#txt_max_same_log_count").val(_filterMaxSameLogCount);
        }
    }

    //txt_filter_log_line_number_start
    if (urlParams.has("filterLogLineNumberStart")) {
        let str = decodeURIComponent(urlParams.get("filterLogLineNumberStart"));
        _filterLogLineNumberStart = parseInt(str);

        if (_filterLogLineNumberStart > 0) {
            $("#txt_filter_log_line_number_start").val(_filterLogLineNumberStart);
        }
    }


}

function _createDevTab() {
    let TEST_FUNC_ARRAY = [];

    for (const key in TEST_FUNC_ARRAY) {
        let testFunc = TEST_FUNC_ARRAY[key];

        let newBtn = $("<button></button>")
            .css("margin", "1px")
            .html(testFunc.name)
            .click(testFunc);

        $("#tab_dev_tests").append(newBtn);
    }
}

function _click_btn_gitAccount_login() {
    let gitAccount_userName = $("#txt_gitAccount_userName").val();
    let gitAccount_acessToken = $("#txt_gitAccount_acessToken").val();
    let url = `https://oss.aaacorp.com/api/v3/users/${gitAccount_userName}`;
    let authStr = `Basic ${btoa(`${gitAccount_userName}:${gitAccount_acessToken}`)}`;

    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        headers: {
            "Authorization": authStr,
        },
        success: function (data, status, jqXHR) {
            localStorage.setItem("gitAccount_userName", gitAccount_userName);
            localStorage.setItem("gitAccount_acessToken", gitAccount_acessToken);
            localStorage.setItem("gitAccount_desc", `${data.login}:${data.email}:${data.id}:${data.name}`);
            _updateLoginStatus();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            _updateLoginStatus();
        }
    });
}

function _click_btn_gitAccount_logout() {
    localStorage.removeItem("gitAccount_userName");
    localStorage.removeItem("gitAccount_acessToken");
    localStorage.removeItem("gitAccount_desc");
    _updateLoginStatus();
}

function _getAuthorizationHeader() {
    let gitAccount_userName = localStorage.getItem("gitAccount_userName");
    let gitAccount_acessToken = localStorage.getItem("gitAccount_acessToken");
    let resStr = `Basic ${btoa(`${gitAccount_userName}:${gitAccount_acessToken}`)}`;
    return resStr;
}

function _updateLoginStatus() {
    let gitAccount_userName = localStorage.getItem("gitAccount_userName");
    let gitAccount_acessToken = localStorage.getItem("gitAccount_acessToken");
    let gitAccount_desc = localStorage.getItem("gitAccount_desc");

    if (gitAccount_userName) {
        $("#div_gitAccount_login").css("display", "none");
        $("#div_gitAccount_logout").css("display", "block");
        $("#txt_gitAccount_userName").val(gitAccount_userName);
        $("#txt_gitAccount_acessToken").val(gitAccount_acessToken);
        $("#div_gitAccount_info").html(gitAccount_desc);
    } else {
        $("#div_gitAccount_login").css("display", "block");
        $("#div_gitAccount_logout").css("display", "none");
        $("#txt_gitAccount_userName").val("");
        $("#txt_gitAccount_acessToken").val("");
        $("#div_gitAccount_info").html("");
    }
}

function _click_panel_top() {
    _onResize();
}

function _click_btn_apply_filter() {
    let filter_log_type = $("input[name=radio_filter_log_type]:checked").val();
    let urlParams = new URLSearchParams(window.location.search);
    urlParams.set("filterLogType", encodeURIComponent(filter_log_type));



    let filter_str = $("#txt_filter_str").val();

    if (!LsUtil.isStrNullOrEmpty(filter_str)){
        urlParams.set("filterStr", encodeURIComponent(filter_str));
    } else {
        urlParams.delete("filterStr");
    }



    let filter_view_only_enter_logs = $("#cb_filter_view_only_enter_logs").prop("checked");
    urlParams.set("filterViewOnlyEnterLogs", encodeURIComponent(filter_view_only_enter_logs));



    let tArray = $(":input:checkbox[id^=cb_filter_tid_]:checked");
    let tValueArray = [];

    for (let i = 0; i < tArray.length; i++) {
        let tid = tArray[i].id.replace("cb_filter_tid_", "");
        tValueArray.push(tid);
    }

    if (tValueArray.length > 0) {
        urlParams.set("filterExcludeTidArray", encodeURIComponent(JSON.stringify(tValueArray)));
    } else {
        urlParams.delete("filterExcludeTidArray");
    }



    let ffArray = $(":input:checkbox[id^=cb_filter_file_func_]:checked");
    let ffValueArray = [];

    for (let i = 0; i < ffArray.length; i++) {
        let ff = ffArray[i].id.replace("cb_filter_file_func_", "");
        ffValueArray.push(ff);
    }

    if (ffValueArray.length > 0) {
        urlParams.set("filterExcludeFileFuncArray", encodeURIComponent(JSON.stringify(ffValueArray)));
    } else {
        urlParams.delete("filterExcludeFileFuncArray");
    }


    let maxCount = parseInt($("#txt_max_same_log_count").val());

    if (maxCount > 0) {
        urlParams.set("filterMaxSameLogCount", maxCount);
    } else {
        urlParams.delete("filterMaxSameLogCount");
    }



    let llnStart = parseInt($("#txt_filter_log_line_number_start").val());

    if (llnStart > 0) {
        urlParams.set("filterLogLineNumberStart", llnStart);
    } else {
        urlParams.delete("filterLogLineNumberStart");
    }


    window.location.search = urlParams.toString();
}

function _click_btn_stop_load() {
    _stopLogLoadingFlag = true;
}

$(function _onLoad() {
    _testAtOnLoad();

    $("button").button();

    $("#panel_top").tabs().click(_click_panel_top);
    $("#btn_upload_log_file").click(_click_btn_upload_log_file);
    $("#btn_apply_oss_repo").click(_click_btn_apply_oss_repo);
    $("#btn_back").click(_click_btn_back);
    $("#txt_log_line_number").on("propertychange change paste", _change_txt_log_line_number);
    $("#btn_forward").click(_click_btn_forward);
    $("#file_log").on("change", _change_file_log);
    $("#btn_gitAccount_login").click(_click_btn_gitAccount_login);
    $("#btn_gitAccount_logout").click(_click_btn_gitAccount_logout);
    $(document).keyup(_document_keyup);
    $("#btn_apply_filter").click(_click_btn_apply_filter);
    $("#btn_stop_load").click(_click_btn_stop_load);

    _parseUrl();
    _createDevTab();

    _updateLoginStatus();

    $(window).resize(function () {
        _onResize();
    });

    _onResize();
});

function _onResize() {
    let fullCenterHeight = $(window).height() - $("#panel_left").offset().top;
    $("#panel_left").height(fullCenterHeight - 20);
    $("#panel_right").height(fullCenterHeight - 10);
}

function _change_file_log(e) {
    let file = e.target.files[0];
    let reader = new FileReader();

    reader.onload = function (e) {
        let fileTxt = e.target.result;
        _curLogFileTxt = fileTxt;
        _renderLogView(fileTxt);
        $("#btn_upload_log_file")
            .css("display", "inline");
    };

    reader.readAsText(file);
}

function _renderSrcView(llInfo, srcFileTxt) {
    let newSrcFileTxt = srcFileTxt.split("\n").map((item) => {
        if (item.includes("LogSloth")) {
            let idx = item.indexOf("LogSloth");
            let newLineStr = `${" ".repeat(idx)}//`;
            return newLineStr;
        } else {
            return item;
        }
    }).join("\n");

    $("#code_src").detach();

    let codeTag = $("<code></code>")
        .attr("id", "code_src")
        .text(newSrcFileTxt)
        .css("margin-bottom", $("#panel_left").height());

    _srcViewCache[llInfo.fileName] = codeTag;
    $("#panel_left").append(codeTag);

    $("#code_src").each(function (i, block) {
        hljs.highlightBlock(block);
        hljs.lineNumbersBlock(block);
    });

}

function _hightlightSrcRow(llInfo) {
    let rowSel = $(`#code_src > table > tbody > tr:nth-child(${llInfo.lineNum})`);
    $("#code_src > table > tbody > tr").css("background-color", $("#code_src").css("background-color"))
    let bg = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.tid).bg, 0.5);
    rowSel.css("background-color", bg);
    let rowTop = $("#code_src > table > tbody > tr:nth-child(1)");
    let scrollTop = rowSel.offset().top - rowTop.offset().top - $("#panel_left").height() / 2;
    $("#panel_left").scrollTop(scrollTop);

    ////////////////////////////////////////////////////////////////////////////////
    // valueName : valueValue 표시
    if (llInfo.logslothType === LogLineInfo_LogSloth_Types.VALUE) {
        let srcRowContentTag = $(`#code_src > table > tbody > tr:nth-child(${llInfo.lineNum}) > td.hljs-ln-line.hljs-ln-code`);
        let indentStr = "";

        for (let c of srcRowContentTag.html()) {
            if (c !== ' ') {
                break;
            }

            indentStr += ' ';
        }

        srcRowContentTag.empty();

        let newSrcRowTag = $("<span></span>")
            .css("background-color", "gold");

        srcRowContentTag.append(indentStr);
        srcRowContentTag.append(newSrcRowTag);

        let valueNameTag = $("<span></span>")
            .addClass("logsloth_value")
            .css("color", "black")
            .html(`${llInfo.valueName} : `);

        newSrcRowTag.append(valueNameTag);
        let valueValueHtml;

        if (llInfo.valueValue.includes("↓↓↓")) {
            try {
                valueValueHtml = llInfo.valueValue.replace(/↓↓↓/gi, " ");
                valueValueHtml = JSON.stringify(JSON.parse(valueValueHtml), null, 2);
                let strArray = valueValueHtml.split("\n");
                valueValueHtml = "";

                for (let k in strArray) {
                    let item = strArray[k];

                    if (k !== "0") {
                        valueValueHtml += indentStr;
                    }

                    valueValueHtml += item;
                    valueValueHtml += "\n";
                }
            } catch (e) {
                valueValueHtml = undefined;
            }
        }

        if (LsUtil.isStrNullOrEmpty(valueValueHtml)) {
            valueValueHtml = llInfo.valueValue.replace(/↓↓↓/gi, "\n");
            let strArray = valueValueHtml.split("\n");
            valueValueHtml = "";

            for (let k in strArray) {
                let item = strArray[k];

                if (k !== "0") {
                    valueValueHtml += indentStr;
                }

                valueValueHtml += item;
                valueValueHtml += "\n";
            }
        }

        let valueValueTag = $("<span></span>")
            .addClass("logsloth_value")
            .css("color", "blue")
            .html(valueValueHtml);

        newSrcRowTag.append(valueValueTag);
    }


    ////////////////////////////////////////////////////////////////////////////////
    // caller callee 표시
    $("#div_overlay_src").remove();

    //#code_src > table > tbody > tr:nth-child(423) > td.hljs-ln-line.hljs-ln-code > span

    if (llInfo.callerLlInfo && (llInfo.fileName === llInfo.callerLlInfo.fileName)) {
        let bgCallee = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.tid).bg, 0.5);
        let bgCaller = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.callerLlInfo.tid).bg, 0.25);
        let callee = $(`#code_src > table > tbody > tr:nth-child(${llInfo.lineNum}) > td.hljs-ln-line.hljs-ln-code > span:nth-child(1)`);
        let caller = $(`#code_src > table > tbody > tr:nth-child(${llInfo.callerLlInfo.lineNum}) > td.hljs-ln-line.hljs-ln-code > span:nth-child(1)`);
        caller.css("background-color", bgCaller);

        let div_overlay_src = $("<div></div>")
            .attr("id", "div_overlay_src")
            .addClass("div_overlay");

        $("#panel_left").append(div_overlay_src);

        if (llInfo.logslothType === LogLineInfo_LogSloth_Types.CALLEE) {
            $("#div_overlay_src").HTMLSVGconnect({
                orientation: "auto",
                class: "call_rope_cross_thread",
                paths: [{start: caller, end: callee, stroke: "yellow"}],
            });
        } else {
            $("#div_overlay_src").HTMLSVGconnect({
                orientation: "auto",
                class: "call_rope_normal",
                paths: [{start: caller, end: callee, stroke: bgCallee}],
            });
        }
    }
}

function _hightlightLogRow(llInfo) {
    let cellLog = $(`#cellLog_${llInfo.logLineNum}`);
    let bg = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.tid).bg, 0.5);

    $("#row_selected_log")
        .css("background-color", bg)
        .css("top", cellLog.css("top"));

    let scrollTop = $("#row_selected_log").offset().top - $("#colLog > div:nth-child(1)").offset().top - $("#panel_right").height() / 2;
    $("#panel_right").scrollTop(scrollTop);

    $("#div_cur_log_0")
        .css("background-color", LsUtil.str2FgBg(llInfo.tid).bg)
        .css("color", LsUtil.str2FgBg(llInfo.tid).fg)
        .html(`#${llInfo.logLineNum} ${llInfo.dateTimeStr}[${llInfo.ellapsedTimeMs}ms] ${llInfo.tid}[T${llInfo.tidIdx}]`);

    $("#div_cur_log_1")
        .css("background-color", LsUtil.str2FgBg(`${llInfo.fileName}:${llInfo.funcName}`).bg)
        .css("color", LsUtil.str2FgBg(`${llInfo.fileName}:${llInfo.funcName}`).fg)
        .html(`${llInfo.fileName}:${llInfo.lineNum}:${llInfo.funcName}`);

    let remainStr = LsUtil.isStrNullOrEmpty(llInfo.remain) ? "..." : llInfo.remain;

    $("#div_cur_log_2")
        .css("background-color", LsUtil.str2FgBg(llInfo.tid).bg)
        .css("color", LsUtil.str2FgBg(llInfo.tid).fg)
        .html(remainStr);


    ////////////////////////////////////////////////////////////////////////////////
    // caller callee 표시
    $("#div_overlay_log").remove();
    $("[id^='spanFileLineFunc']").css("background-color", "");

    if (llInfo.callerLlInfo) {
        let bgCallee = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.tid).bg, 0.5);
        let bgCaller = LsUtil.appendOpacityToColorStr(LsUtil.str2FgBg(llInfo.callerLlInfo.tid).bg, 0.25);
        let callee = $(`#spanFileLineFunc_${llInfo.logLineNum}`);
        let caller = $(`#spanFileLineFunc_${llInfo.callerLlInfo.logLineNum}`);
        caller.css("background-color", bgCaller);

        let div_overlay_log = $("<div></div>")
            .attr("id", "div_overlay_log")
            .addClass("div_overlay");

        $("#panel_right").append(div_overlay_log);


        if (llInfo.logslothType === LogLineInfo_LogSloth_Types.CALLEE) {
            $("#div_overlay_log").HTMLSVGconnect({
                orientation: "auto",
                class: "call_rope_cross_thread",
                paths: [{start: caller, end: callee, stroke: "yellow"}],
            });
        } else {
            $("#div_overlay_log").HTMLSVGconnect({
                orientation: "auto",
                class: "call_rope_normal",
                paths: [{start: caller, end: callee, stroke: bgCallee}],
            });
        }
    }
}

function _reqSrcContent(llInfo, gitRepo, srcFileUrl) {

    //android-sample/app/src/main/java/com/aaa/bbb/logslothsampleand/MainActivity.java
    let contentsUrl = `https://oss.aaacorp.com/api/v3/repos/${gitRepo.owner}/${gitRepo.repo}/contents/${srcFileUrl}?ref=${gitRepo.ref}`;

    $.ajax({
        type: "GET",
        url: contentsUrl,
        dataType: "json",
        headers: {
            "Authorization": _getAuthorizationHeader(),
        },
        success: function (data, status, jqXHR) {
            let dataBuf = Uint8Array.from(atob(data.content), c => c.charCodeAt(c));
            let srcFileTxt = new TextDecoder("utf-8").decode(dataBuf);
            _renderSrcView(llInfo, srcFileTxt);

            setTimeout(() => {
                _hightlightLogRow(llInfo);
                _hightlightSrcRow(llInfo);
            }, 1000);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error(`failed !!! \n${textStatus} \n${contentsUrl}`);
        }
    });
}

function _reqSearchSrcFile(llInfo) {
    let searchCnt = 0;
    let srcFileInfoArray = [];

    for (let k in _gitRepoArray) {
        let gitRepo = _gitRepoArray[k];
        let searchUrl = `https://oss.aaacorp.com/api/v3/search/code?q=filename:${llInfo.fileName}+repo:${gitRepo.owner}/${gitRepo.repo}`;

        $.ajax({
            type: "GET",
            url: searchUrl,
            dataType: "json",
            headers: {
                "Authorization": _getAuthorizationHeader(),
            },
            success: function (data, status, jqXHR) {
                for (let k in data.items) {
                    let url = data.items[k].path;
                    let fileName = url.substring(url.lastIndexOf('/')+1);

                    if (llInfo.fileName === fileName) {
                        srcFileInfoArray.push({
                            gitRepo: gitRepo,
                            srcFileUrl: url,
                        });
                    }
                }

                searchCnt += 1;

                if (searchCnt < _gitRepoArray.length) {
                    return;
                }

                if (srcFileInfoArray.length === 0) {
                    console.warn(`File not found !!! ${llInfo.fileName}`);
                } else if (srcFileInfoArray.length === 1) {
                    _reqSrcContent(llInfo, srcFileInfoArray[0].gitRepo, srcFileInfoArray[0].srcFileUrl);
                } else {
                    let selArray = srcFileInfoArray.map((item) => {
                        let res = `${item.gitRepo.toString()} ${item.srcFileUrl}`;
                        return res;
                    });

                    LsUtil.openSelectRadioDlg("logsloth", "2 more files are founded. Select 1 file.", selArray, (item) => {
                        let selSrcGitRepo = new LsGitRepo().parse(item.split(" ")[0]);
                        let selSrcFilePath = item.split(" ")[1];
                        _reqSrcContent(llInfo, selSrcGitRepo, selSrcFilePath);
                    });
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error(`failed !!! \n${textStatus} \n${searchUrl}`);
            }
        });
    }
}

function _syncSrcLog() {
    if (_llInfoArray.length === 0) {
        return;
    }

    if (_curLlInfo === undefined) {
        _curLlInfo = _llInfoArray[0];
    }

    $("#txt_log_line_number").val(_curLlInfo.logLineNum);

    if (_curLlInfo.fileName === undefined) {
        console.warn(`fileName is empty !!! : ${llInfo.log}`);
        $("#code_src").detach();
        _hightlightLogRow(_curLlInfo);
    } else if (_curLlInfo.fileName === _curSrcFileName) {
        console.info(`${_curLlInfo.fileName} is aleady loaded .`)
        _hightlightLogRow(_curLlInfo);
        _hightlightSrcRow(_curLlInfo);
    } else if (_srcViewCache[_curLlInfo.fileName]) {
        $("#code_src").detach();
        $("#panel_left").append(_srcViewCache[_curLlInfo.fileName]);
        _hightlightLogRow(_curLlInfo);
        _hightlightSrcRow(_curLlInfo);
    } else {
        _reqSearchSrcFile(_curLlInfo);
    }

    _curSrcFileName = _curLlInfo.fileName;
}

function _renderLogView_applyFilter(fileText) {
    let lineArray = fileText.split("\n");
    let tidDic = [];
    let fileFuncDic = {};
    let fileLineDic = {};

    _llInfoArray = [];
    _lastFilteredLineArray = [];
    _orderedTidArray = [];
    _tidCallDepthMap = {};

    ////////////////////////////////////////////////////////////////////////////////
    // 로그 필터
    for (let i = 0; i < lineArray.length; i++) {
        if (_filterLogLineNumberStart > 0 &&
        i < _filterLogLineNumberStart) {
            continue;
        }

        let line = lineArray[i];

        let wordArray = line.split(" ").filter((item) => {
            return item;
        });

        let type;
        let dateStr = wordArray[0];
        let timeStr = wordArray[1];
        let dateTimeStr = `${new Date().getFullYear()}-${dateStr} ${timeStr}`;
        let dateTime = Date.parse(dateTimeStr);
        let tid;
        let tag;
        let fileFunc;
        let fileLine;

        if (isNaN(dateTime)) {
            type = LogLineInfo_Types.NONE;
        } else {
            tid = wordArray[7];
            tag = wordArray[5];

            if (tag === "LOGSLOTH:" || tag === "LogEx:") {
                type = LogLineInfo_Types.LOGSLOTH;
                let fileName = wordArray[6].split(":")[0];
                let lineNum = parseInt(wordArray[6].split(":")[1]);
                let funcName = wordArray[6].split(":")[2];
                fileFunc = `${fileName}_${funcName}`;
                fileLine = `${fileName}_${lineNum}`;
                let remain = wordArray.slice(8).join(" ");

                if (fileFuncDic[fileFunc] === undefined) {
                    fileFuncDic[fileFunc] = 0;
                }

                fileFuncDic[fileFunc] += 1;


                if (fileLineDic[fileLine] === undefined) {
                    fileLineDic[fileLine] = 0;
                }

                fileLineDic[fileLine] += 1;

                if (_filterMaxSameLogCount > 0 &&
                    fileLineDic[fileLine] > _filterMaxSameLogCount) {
                    continue;
                }

                if (tidDic[tid] === undefined) {
                    tidDic[tid] = 0;
                }

                tidDic[tid] += 1;
            } else {
                type = LogLineInfo_Types.LOGCAT;
            }
        }

        if (_filterLogType === "logcat_logsloth") {
            if (type === LogLineInfo_Types.NONE) {
                continue;
            }
        } else if (_filterLogType === "all_logs") {
        } else {
            if (type === LogLineInfo_Types.NONE ||
                type === LogLineInfo_Types.LOGCAT) {
                continue;
            }
        }

        if (_filterExcludeTidArray.length > 0) {
            if (_filterExcludeTidArray.includes(tid)) {
                continue;
            }
        }

        if (_filterExcludeFileFuncArray.length > 0) {
            if (_filterExcludeFileFuncArray.includes(fileFunc)) {
                continue;
            }
        }

        let canInclude = true;

        if (_filterArray &&
            _filterArray.length > 0) {
            canInclude = false;

            for (let k in _filterArray) {
                let andItem = _filterArray[k];

                if (Array.isArray(andItem)) {
                    for (let k2 in andItem) {
                        let orItem = andItem[k2];

                        if (line.includes(orItem)) {
                            canInclude = true;
                            break;
                        }
                    }
                } else {
                    if (andItem.startsWith("~")) {
                        let notAndItem = andItem.replace(/~/gi, "");
                        canInclude = !line.includes(notAndItem);
                    } else {
                        canInclude = line.includes(andItem);
                    }

                }

                if (!canInclude) {
                    break;
                }
            }
        }

        if (canInclude === true) {
            _lastFilteredLineArray.push({logLineNum: i, line: line});
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    // tid 필터 UI 준비
    let sortedTidArray = [];

    for (let k in tidDic) {
        let tid = k;
        let count = tidDic[k];
        sortedTidArray.push({tid : tid, count: count});
    }

    sortedTidArray = sortedTidArray.sort((a, b)=>{ return b.count - a.count;});

    for (let k in sortedTidArray) {
        let tid = sortedTidArray[k].tid;
        let count = sortedTidArray[k].count;
        let checked = _filterExcludeTidArray.includes(tid);

        let cb = $("<input/>")
            .attr("id", `cb_filter_tid_${tid}`)
            .attr("type", "checkbox")
            .prop("checked", checked);

        let label = $("<label></label>")
            .append(cb)
            .append(`T${k} ${tid} (${count})`)
            .css("display", "block")
            .css("line-height", "40px")
            .css("background-color", LsUtil.str2FgBg(tid).bg)
            .css("color", LsUtil.str2FgBg(tid).fg);

        $("#fs_filter_tid").append(label);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // func 필터 UI 준비
    let sortedFileFuncArray = [];

    for (let k in fileFuncDic) {
        let fileFunc = k;
        let count = fileFuncDic[k];
        sortedFileFuncArray.push({fileFunc : fileFunc, count: count});
    }

    sortedFileFuncArray = sortedFileFuncArray.sort((a, b)=>{ return b.count - a.count;});

    for (let k in sortedFileFuncArray) {
        let fileFunc = sortedFileFuncArray[k].fileFunc;
        let count = sortedFileFuncArray[k].count;
        let checked = _filterExcludeFileFuncArray.includes(fileFunc);

        let cb = $("<input/>")
            .attr("id", `cb_filter_file_func_${fileFunc.replace(/:/gi, "_")}`)
            .attr("type", "checkbox")
            .prop("checked", checked)
            .css("margin", 10);

        let label = $("<label></label>")
            .append(cb)
            .append(`${fileFunc} (${count})`)
            .css("display", "block")
            .css("line-height", "40px")
            .css("background-color", LsUtil.str2FgBg(fileFunc).bg)
            .css("color", LsUtil.str2FgBg(fileFunc).fg);


        $("#fs_filter_file_func").append(label);
    }
}

function _renderLogView_createLlInfoArray() {
    ////////////////////////////////////////////////////////////////////////////////
    // 로그 분류
    for (let i = 0; i < _lastFilteredLineArray.length; i++) {
        let llInfo = new LsLogLineInfo();
        _llInfoArray.push(llInfo);
        llInfo.line = _lastFilteredLineArray[i].line;
        llInfo.logLineNum = _lastFilteredLineArray[i].logLineNum;
        let wordArray = llInfo.line.split(" ").filter((item) => {
            return item;
        });
        llInfo.dateStr = wordArray[0];
        llInfo.timeStr = wordArray[1];
        llInfo.dateTimeStr = `${new Date().getFullYear()}-${llInfo.dateStr} ${llInfo.timeStr}`;
        llInfo.dateTime = Date.parse(llInfo.dateTimeStr);

        if (isNaN(llInfo.dateTime)) {
            console.warn("can not parse");
            llInfo.type = LogLineInfo_Types.NONE;
            continue;
        }

        llInfo.pid = wordArray[2];
        llInfo.tid = wordArray[7];
        llInfo.logLevel = wordArray[4];
        llInfo.tag = wordArray[5];

        if (_orderedTidArray.includes(llInfo.tid)) {
            llInfo.callDepth = _tidCallDepthMap[llInfo.tid];
        } else {
            _orderedTidArray.push(llInfo.tid);
            _tidCallDepthMap[llInfo.tid] = llInfo.callDepth;
        }

        llInfo.tidIdx = _orderedTidArray.indexOf(llInfo.tid);

        if (llInfo.tag === "LOGSLOTH:" || llInfo.tag === "LogEx:") {
            llInfo.type = LogLineInfo_Types.LOGSLOTH;
            llInfo.fileName = wordArray[6].split(":")[0];
            llInfo.lineNum = parseInt(wordArray[6].split(":")[1]);
            llInfo.funcName = wordArray[6].split(":")[2];
            llInfo.remain = wordArray.slice(8).join(" ");

            if (llInfo.remain.includes("↘↘↘")) {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.ENTER;
                llInfo.callDepth += 1;
                _tidCallDepthMap[llInfo.tid] = llInfo.callDepth;
            } else if (llInfo.remain.includes("↗↗↗")) {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.LEAVE;
                _tidCallDepthMap[llInfo.tid] = Math.max(llInfo.callDepth - 1, -1);
            } else if (llInfo.remain.includes(":::")) {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.VALUE;
                let valueArray = llInfo.remain.split(":::").filter((item) => {
                    return item;
                });
                llInfo.valueName = valueArray[0];
                llInfo.valueValue = valueArray[1];
            } else if (llInfo.remain.includes("→→→")) {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.CALLER;
                llInfo.callParam = llInfo.remain.split("→→→").filter((item) => {
                    return item;
                })[0];
            } else if (llInfo.remain.includes("←←←")) {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.CALLEE;
                llInfo.callParam = llInfo.remain.split("←←←").filter((item) => {
                    return item;
                })[0];
            } else {
                llInfo.logslothType = LogLineInfo_LogSloth_Types.NONE;
            }

            if (llInfo.logslothType === LogLineInfo_LogSloth_Types.CALLEE) {

                for (let j = i - 1; j >= 0; j--) {
                    let oldLlInfo = _llInfoArray[j];

                    if (oldLlInfo.callParam === llInfo.callParam) {
                        llInfo.callerLlInfo = oldLlInfo;
                        break;
                    }
                }

            } else {
                for (let j = i - 1; j >= 0; j--) {
                    let oldLlInfo = _llInfoArray[j];

                    if ((oldLlInfo.type === LogLineInfo_Types.LOGSLOTH) &&
                        (oldLlInfo.tid === llInfo.tid)) {
                        llInfo.callerLlInfo = oldLlInfo;
                        break;
                    }
                }
            }
        } else {
            llInfo.type = LogLineInfo_Types.LOGCAT;
            llInfo.remain = wordArray.slice(8).join(" ");
        }
    }
}

function _renderLogView_ui_middle(totalIndex, offsetY, colLogLineNum, colTime, colLog, tidCurOpenCellLogMap) {
    $("#txt_loading_status").html(`${totalIndex}/${_llInfoArray.length}`);

    for (let index = totalIndex; index < Math.min(totalIndex + LsConst.RENDER_LOG_VIEW_SINGLE_TASK_COUNT, _llInfoArray.length); index++) {
        let llInfo = _llInfoArray[index];
        let llInfo_m1 = _llInfoArray[index - 1];

        ////////////////////////////////////////////////////////////////////////////////
        // 로그 라인 번호 셀
        {
            let cell = $("<div></div>")
                .attr("id", `cellLogLineNum_${llInfo.logLineNum}`)
                .css("position", "absolute")
                .css("left", 0)
                .css("top", offsetY)
                .css("text-align", "right")
                .width(colLogLineNum.width())
                .html(llInfo.logLineNum);

            colLogLineNum.append(cell);
        }

        ////////////////////////////////////////////////////////////////////////////////
        // 경과시간 셀
        {

            if (llInfo_m1) {
                llInfo.ellapsedTimeMs = llInfo.dateTime - llInfo_m1.dateTime;
            }

            let cell = $("<div></div>")
                .attr("id", `celTime_${llInfo.logLineNum}`)
                .css("position", "absolute")
                .css("left", 0)
                .css("top", offsetY)
                .css("text-align", "right")
                .width(colTime.width())
                .html(llInfo.ellapsedTimeMs);

            colTime.append(cell);
        }

        ////////////////////////////////////////////////////////////////////////////////
        // 로그본문 셀
        {
            let logOffsetX = llInfo.tidIdx * LsConst.INDENT_THREAD + Math.max(llInfo.callDepth, 0) * LsConst.INDENT_FUNC;

            let cell = $("<div></div>")
                .attr("id", `cellLog_${llInfo.logLineNum}`)
                .css("position", "absolute")
                .css("left", logOffsetX)
                .css("top", offsetY)
                .css("color", LsUtil.str2FgBg(`${llInfo.fileName}:${llInfo.funcName}`).fg)
                .css("background-color", LsUtil.str2FgBg(`${llInfo.fileName}:${llInfo.funcName}`).bg)
                .css("padding-left", "2px")
                .width(colLog.width() - logOffsetX)
                .on("click", () => {
                    _curLlInfo = llInfo;
                    _syncSrcLog();
                });


            if (llInfo.type === LogLineInfo_Types.LOGSLOTH) {

                let bg = LsUtil.str2FgBg(llInfo.tid).bg;
                cell.css("border-left", `5px solid ${bg}`);

                if (llInfo.logslothType === LogLineInfo_LogSloth_Types.ENTER) {
                    cell.css("border-top", `2px solid ${bg}`);
                } else if (llInfo.logslothType === LogLineInfo_LogSloth_Types.LEAVE) {
                    cell.css("border-bottom", `2px solid ${bg}`);
                }

                let fileLineFuncStr;

                if (llInfo_m1 &&
                    (`${llInfo.fileName}:${llInfo.funcName}` === `${llInfo_m1.fileName}:${llInfo_m1.funcName}`) &&
                    llInfo.logslothType !== LogLineInfo_LogSloth_Types.ENTER) {
                    fileLineFuncStr = `:${llInfo.lineNum}`;
                } else {
                    fileLineFuncStr = `${llInfo.fileName}:${llInfo.lineNum}:${llInfo.funcName}`;
                }

                let spanFileLineFunc = $("<span></span>")
                    .attr("id", `spanFileLineFunc_${llInfo.logLineNum}`)
                    .css("text-decoration", "underline")
                    .css("color", "blue")
                    .html(fileLineFuncStr);

                cell.append(spanFileLineFunc);
            }

            let spanRemain = $("<span></span>")
                .attr("id", `spanRemain_${llInfo.logLineNum}`)
                .html(` ${llInfo.remain}`);

            cell.append(spanRemain);
            colLog.append(cell);


            let cellLog = cell;

            if (cellLog.height() === 0) {
                cellLog.height(LsConst.ROW_HEIGHT);
            }

            offsetY += cellLog.height();

            if (llInfo.logslothType === LogLineInfo_LogSloth_Types.ENTER) {
                if (tidCurOpenCellLogMap[llInfo.tid] === undefined) {
                    tidCurOpenCellLogMap[llInfo.tid] = [];
                }
                tidCurOpenCellLogMap[llInfo.tid].push(cellLog);
            } else if (llInfo.logslothType === LogLineInfo_LogSloth_Types.LEAVE) {
                try {
                    let openedCelLog = tidCurOpenCellLogMap[llInfo.tid].pop();
                    let newHeight = offsetY - parseInt(openedCelLog.css("top"));
                    openedCelLog.height(newHeight);
                } catch (e) {
                    console.error(e.toString());
                    console.error(JSON.stringify(llInfo));
                }
            }
        }

        offsetY += LsConst.ROW_MARGIN;
        colLogLineNum.height(offsetY);
        colTime.height(offsetY);
        colLog.height(offsetY);
    }
    return offsetY;
}

function _renderLogView_ui_bottom(offsetY) {
    let rowBuffer = $("<div></div>")
        .css("position", "absolute")
        .css("left", 0)
        .css("top", offsetY)
        .width($("#panel_right").width())
        .height($("#panel_right").height());

    $("#panel_right").append(rowBuffer);


    ////////////////////////////////////////////////////////////////////////////////
    // 후처리 필터

    if (_filterViewOnlyEnterLogs === true) {
        $("[id^='cellLog_']").css("border-style", "");

        for (let k in _llInfoArray) {
            let llInfo = _llInfoArray[k];
            let spanFileLineFunc = $(`#spanFileLineFunc_${llInfo.logLineNum}`);
            let spanRemain = $(`#spanRemain_${llInfo.logLineNum}`);

            if (llInfo.logslothType == LogLineInfo_LogSloth_Types.ENTER) {
                spanFileLineFunc.css("display", "");
                spanRemain.css("display", "none");
            } else {
                spanFileLineFunc.css("display", "none");
                spanRemain.css("display", "none");
            }
        }
    }
}

function _renderLogView_ui() {
    $("#panel_right_top").empty();
    $("#panel_right").empty();

    let offsetX = 0;
    let offsetY = 0;

    let colLogLineNum;
    let colTime;
    let colLog;

    ////////////////////////////////////////////////////////////////////////////////
    // 로그 라인 넘버 헤더, 컬럼 생성
    {
        let cellHeader = $("<div></div>")
            .attr("id", "headerLogLineNum")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", 0)
            .css("text-align", "center")
            .css("background-color", "powderblue")
            .width(LsConst.COL_WIDTH_LOG_LINE_NUM)
            .height($("#panel_right_top").height())
            .html("#");

        $("#panel_right_top").append(cellHeader);

        let col = $("<div></div>")
            .attr("id", "colLogLineNum")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", offsetY)
            .css("background-color", "powderblue")
            .width(LsConst.COL_WIDTH_LOG_LINE_NUM)
            .height($("#panel_right_top").height());

        $("#panel_right").append(col);

        offsetX += LsConst.COL_WIDTH_LOG_LINE_NUM;
        offsetX += LsConst.COL_MARGIN;

        colLogLineNum = col;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // 경과시간 헤더, 컬럼 생성
    {
        let cellHeader = $("<div></div>")
            .attr("id", "headerTime")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", 0)
            .css("text-align", "center")
            .css("background-color", "lemonchiffon")
            .width(LsConst.COL_WIDTH_TIME)
            .height($("#panel_right_top").height())
            .html("time(ms)");

        $("#panel_right_top").append(cellHeader);

        let col = $("<div></div>")
            .attr("id", "colTime")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", offsetY)
            .css("background-color", "lemonchiffon")
            .width(LsConst.COL_WIDTH_TIME);

        $("#panel_right").append(col);

        offsetX += LsConst.COL_WIDTH_TIME;
        offsetX += LsConst.COL_MARGIN;

        colTime = col;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // 로그본문 헤더, 컬럼 생성,
    // tid 컬럼 생성
    {
        let cellHeader = $("<div></div>")
            .attr("id", "headerLog")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", 0)
            .css("text-align", "center")
            .width($("#panel_right").width() - offsetX)
            .height($("#panel_right_top").height());

        $("#panel_right_top").append(cellHeader);
        let cellHeaderLog = cellHeader;

        for (const key in _orderedTidArray) {
            let tid = _orderedTidArray[key];
            let htmlStr = `${tid} <br> T${key}`;

            let cellTid = $("<div></div>")
                .attr("id", `tidSpan_${tid}`)
                .css("position", "absolute")
                .css("left", key * LsConst.INDENT_THREAD)
                .css("top", 0)
                .css("color", LsUtil.str2FgBg(tid).fg)
                .css("background-color", LsUtil.str2FgBg(tid).bg)
                .width(LsConst.INDENT_THREAD)
                .height($("#panel_right_top").height())
                .html(htmlStr);

            cellHeaderLog.append(cellTid);
        }

        let col = $("<div></div>")
            .attr("id", "colLog")
            .css("position", "absolute")
            .css("left", offsetX)
            .css("top", offsetY)
            .width(10000);

        $("#panel_right").append(col);
        colLog = col;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // 로그 라인 선택 표시
    {
        let row_selected_log = $("<div></div>")
            .attr("id", "row_selected_log")
            .css("position", "absolute")
            .css("left", 0)
            .css("top", 0)
            .css("pointer-events", "none")
            .width($("#panel_right").width())
            .height(LsConst.ROW_HEIGHT)
            .off("click");

        $("#panel_right").append(row_selected_log);
    }

    offsetX = 0;
    offsetY = 0;
    let tidCurOpenCellLogMap = {};


    ////////////////////////////////////////////////////////////////////////////////
    // 로그 본문 내용 렌더링
    let totalIndex = 0;

    let timerId = setInterval(() => {
        offsetY = _renderLogView_ui_middle(totalIndex, offsetY, colLogLineNum, colTime, colLog, tidCurOpenCellLogMap);
        totalIndex += LsConst.RENDER_LOG_VIEW_SINGLE_TASK_COUNT;

        if (totalIndex >= _llInfoArray.length || _stopLogLoadingFlag === true) {
            _stopLogLoadingFlag = false;
            clearInterval(timerId);
            _renderLogView_ui_bottom(offsetY);
        }

    }, LsConst.RENDER_LOG_VIEW_TASK_INTERVAL_MS);
}

function _renderLogView(fileText) {
    _renderLogView_applyFilter(fileText);
    _renderLogView_createLlInfoArray();
    _renderLogView_ui();
}

function _testAtOnLoad() {
    let res;
    console.log(res);
}