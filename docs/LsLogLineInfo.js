"use strict";

const LogLineInfo_Types = {
    NONE: "NONE",
    LOGCAT: "LOGCAT",
    LOGSLOTH: "LOGSLOTH",
}

const LogLineInfo_LogSloth_Types = {
    NONE: "NONE",
    ENTER: "ENTER",
    LEAVE: "LEAVE",
    VALUE: "VALUE",
    CALLER: "CALLER",
    CALLEE: "CALLEE",
}

function LsLogLineInfo() {
    this.line;
    this.logLineNum;
    this.type = LogLineInfo_Types.NONE;
    this.logslothType = LogLineInfo_LogSloth_Types.NONE;
    this.dateStr;
    this.timeStr;
    this.dateTimeStr;
    this.dateTime;
    this.ellapsedTimeMs;
    this.pid;
    this.tid;
    this.logLevel;
    this.tag;
    this.fileName;
    this.lineNum;
    this.funcName;
    this.remain;
    this.valueName;
    this.valueValue;

    this.tidIdx = 0;
    this.callDepth = -1;

    this.fullFilePath;

    this.callerLlInfo;
    this.callParam;
}