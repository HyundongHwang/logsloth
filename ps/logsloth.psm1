<#
.SYNOPSIS
.EXAMPLE
#>
function logsloth {
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory = $true, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [string]
        $LINE,

        [Parameter(Mandatory = $false, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [switch]
        $SHOW_FUNC_COUNT = $false,

        [Parameter(Mandatory = $false, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [switch]
        $RETURN_LOG_AS_TABLE = $false,

        [Parameter(Mandatory = $false, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [switch]
        $OUT_AS_HTML = $false,

        [Parameter(Mandatory = $false, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [int]
        $THREAD_INDENT = 4,

        [Parameter(Mandatory = $false, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [int]
        $PERIOD_HIGHTLIGHT_MS = 5
    )

    begin {
        $_oldTime = $null
        $_oldTid = 0
        $_threadIndentDic = @{}
        $_funcCountDic = @{}
        $_oldLog
        $_funcIndentDic = @{}
        $_oldfuncName = $null
		
        $_COLOR_LIST = @("Green", "DarkGray", "DarkGreen", "DarkYellow", "Gray", "Magenta", "Cyan", "DarkCyan", "DarkRed", "Yellow")
        $_oldLogLineNum = 0

        if ($OUT_AS_HTML) {
            $_htmlFilePath = "logsloth-$([datetime]::Now.ToString("yyMMdd-HHmmss")).html"
            $_HTML_FILE_TOP | Out-File -Append $_htmlFilePath
        }
    }

    process {
        # Write-Host "LINE : $LINE"
        $logLineNum = $_oldLogLineNum + 1
        $_oldLogLineNum = $logLineNum

        $canParse = $LINE -match "\d\d-\d\d\s\d\d:\d\d:\d\d.\d\d\d"

        if (!$canParse) {
            Write-Host $LINE -ForegroundColor White

            if ($OUT_AS_HTML) {
                $htmlRow = $LINE
                $htmlRow | Out-File $_htmlFilePath -Append
            }

            return
        }



        $curTime = [DateTime]::Parse("2018-$($matches[0])")

        if ($_oldTime -eq $null) {
            $_oldTime = $curTime
        }

        $period = $curTime - $_oldTime
        $_oldTime = $curTime



        $lineTokens = $LINE.Split(" ", [System.StringSplitOptions]::RemoveEmptyEntries)
        $tid = $lineTokens[3]
        $tag = $lineTokens[5]



        $funcName = $null
        $lineNum = 0
        $fileName = $null
        $simpleFuncName = $null
        
        for ( $i = 0; $i -lt $lineTokens.Length; $i++ ) {
            $token = $lineTokens[$i]
            if ($token -match "[.cpp|.h|.cc|.c|.cxx|.hpp|.java]:\d+:") {
                $res = $token -split ":"
                $fileName = $res[0]
                $lineNum = $res[1]
                $simpleFuncName = $res[2]
                $funcName = "$($res[0])::$($res[2])"
                break
            }
        }

        if ($funcName -eq $null) {
            $funcName = "$tag$([string]::Join( " ", $lineTokens[5..6] ))"
        }










        $isLeave = $false
        $funcIndent = 0

        if ($_funcIndentDic.ContainsKey($tid)) {
            $funcIndent = $_funcIndentDic[$tid]
        } else {
            $funcIndent = -1
            $_funcIndentDic[$tid] = $funcIndent
        }

        if ($LINE -like "*↘↘↘*") {
            $funcIndent = $funcIndent + 1
            $_funcIndentDic[$tid] = $funcIndent
        }

        if ($LINE -like "*↗↗↗*") {
            $_funcIndentDic[$tid] = $funcIndent - 1
            $isLeave = $true
        }

        $funcIndent = [System.Math]::Max($funcIndent, 0)
        # Write-Host "funcIndent[$funcIndent]"



        $isNewTid = $tid -ne $_oldTid
        $_oldTid = $tid

        $canSkipFuncName = ($_oldfuncName -eq $funcName) -and (!$isNewTid)
        $_oldfuncName = $funcName



        $callerFuncName = $null
        $isCallerLog = $LINE -match "→→→.*→→→"

        if ($isCallerLog) {
            $callerFuncName = $matches[0].Replace("→→→", "")
        }



        $calleeFuncName = $null
        $isCalleeLog = $LINE -match "←←←.*←←←"

        if ($isCalleeLog) {
            $calleeFuncName = $matches[0].Replace("←←←", "")
        }



        if (!$_funcCountDic.ContainsKey($funcName)) {
            $_funcCountDic[$funcName] = 1
        }
        else {
            $_funcCountDic[$funcName] = $_funcCountDic[$funcName] + 1
        }



        $padCount = 0

        if (!$_threadIndentDic.ContainsKey($tid)) {
            $_threadIndentDic[$tid] = $_threadIndentDic.Count
        }

        $padCount = ($_threadIndentDic[$tid] % 10) * $THREAD_INDENT + 1
        $leftPadding = " " * $padCount;
        $leftPadding += "·" * $funcIndent;



        $hashInt = _logsloth_str_2_byte_sum($funcName)
        $clrIdx = $hashInt % ($_COLOR_LIST.Length)
        $color = $_COLOR_LIST[$clrIdx]



        $remainLog = [string]::Join(" ", $lineTokens[7..$($lineTokens.Length)] ) 
        $newLineStr = " " * 30 + $leftPadding
        $remainLog = $remainLog.Replace("↓↓↓", "`n" + $newLineStr)














        $cmdRow = $null

        if ($period.TotalMilliseconds -ge $PERIOD_HIGHTLIGHT_MS) {
            $cmdRow += "`n`n`n`n`n"
        }

        if (!$canSkipFuncName) {
            $cmdRow += "`n"
        }

        if ($isNewTid) {
            $cmdRow += "TID[$tid]`n"
        }

        $cmdRow += "[$($lineTokens[0]) $($lineTokens[1])]"
        $cmdRow += "[$([string]::Format("{0,5:N0}", $period.TotalMilliseconds))ms]"
        $cmdRow += $leftPadding

        if (!$isLeave) {
            if ($canSkipFuncName) {
                $cmdRow += "`:$lineNum "
            }
            else {
                $cmdRow += "$fileName`:$lineNum`:$simpleFuncName "
            } 
        }

        if ($isCallerLog) {
            $cmdRow += "→→→ $callerFuncName"
        }
        elseif ($isCalleeLog) {
            $cmdRow += "←←← $calleeFuncName"
        }
        else {
            $cmdRow += $remainLog
        }

        Write-Host $cmdRow -ForegroundColor $color



        if ($RETURN_LOG_AS_TABLE) {
            $row = New-Object psobject
            $row | Add-Member TimeStamp "$($lineTokens[0]) $($lineTokens[1])"
            $row | Add-Member Period "$([string]::Format("{0,5:N0}", $period.TotalMilliseconds))"
            $row | Add-Member TID $tid
            $row | Add-Member Color $color
            $row | Add-Member FileName $fileName
            $row | Add-Member LineNum $lineNum
            $row | Add-Member SimpleFuncName $simpleFuncName
            $row | Add-Member Log $remainLog
            $row | Add-Member OldLog $_oldLog
            $_oldLog = $remainLog
            return $row
        }



        if ($OUT_AS_HTML) {
            $htmlRow = $null

            if ($period.TotalMilliseconds -ge $PERIOD_HIGHTLIGHT_MS) {
                $htmlRow += "`n`n`n`n`n"
            }

            if (!$canSkipFuncName) {
                $htmlRow += "`n"
            }

            if ($isNewTid) {
                $htmlRow += "TID[$tid]`n"
            }

            $htmlRow += "<span style='color`: $color;'>"
            $htmlRow += "[$($lineTokens[0]) $($lineTokens[1])]"
            $htmlRow += "[$([string]::Format("{0,5:N0}", $period.TotalMilliseconds))ms]"
            $htmlRow += $leftPadding

            if (!$isLeave) {
                if ($canSkipFuncName) {
                    $htmlRow += "`:$lineNum "
                }
                else {
                    if ($isCalleeLog) {
                        $htmlRow += "$fileName`:$lineNum`:$simpleFuncName "
                    }
                    else {
                        $htmlRow += "$fileName`:$lineNum`:<span class='funcNameEnter'>$simpleFuncName</span> "
                    }
                }
            }

            if ($isCallerLog) {
                $htmlRow += "<span class='funcNameCall'>$callerFuncName</span>"
            }
            elseif ($isCalleeLog) {
                $htmlRow += "<span class='funcNameEnter'>$calleeFuncName</span>"
            }
            else {
                $htmlRow += $remainLog
            }

            $htmlRow += "</span>"
            $htmlRow | Out-File $_htmlFilePath -Append
        } 
    }

    end {
        if ($SHOW_FUNC_COUNT) {

            $_funcCountDic.Keys | 
                Sort-Object -Descending { 
                $funcName = $_
                $funcCount = $_funcCountDic[$funcName]
                return $funcCount } |
                foreach {
                $funcName = $_
                $funcCount = $_funcCountDic[$funcName]
                $statLog = "$([string]::Format("{0,-100} | {1,10}", $funcName, $funcCount))"
                Write-Host $statLog
            } # foreach
        } # if

        if ($OUT_AS_HTML) {
            $_HTML_FILE_BOTTOM | Out-File -Append $_htmlFilePath
        }


    } # end
}



function _logsloth_str_2_byte_sum {
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory = $true, ValueFromPipeline = $true, ValueFromPipelinebyPropertyName = $true)]
        [string]
        $STR
    )
    
    $strBuf = [System.Text.Encoding]::UTF8.GetBytes($STR)
    $byteSum = 0
    
    foreach ($byte in $strBuf) {
        $byteSum += $byte
    }

    return $byteSum
}





$_HTML_FILE_TOP = 
@"
<html>

<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <!-- https://github.com/a115/HTML-SVG-connect -->
    <script src="https://hhdpublish.blob.core.windows.net/pub/LeaticIcible/jquery.html-svg-connect.js"></script>
    <style>
        pre {
            color: white;
            background-color: black;
            font-family: 'Courier New', Courier, monospace;
        }

        .my-line-style {
            stroke-dasharray: 3px;
            stroke-width: 3px;
        }

        #svgContainer {
            z-index: 10;
            position: absolute;
            pointer-events:none;
        }
    </style>
    <script>

        function connectAll() {
            let paths = [];
            let funcNameCallArray = `$(".funcNameCall");

            for (let i = 0; i < funcNameCallArray.length; i++) {
                let caller = funcNameCallArray.eq(i);
                let next = caller.parent().next();

                for (let j = 0; j < 1000; j++) {
                    if (next.find(".funcNameEnter").length > 0) {
                        let callee = next.find(".funcNameEnter");
                        if (callee.text() == caller.text()) {
                            let callerClr = caller.css("color");
                            caller.css("border-style", "solid").css("border-width", "1px");
                            callee.css("border-style", "solid").css("border-width", "1px");
                            paths.push({ start: caller, end: callee, stroke: callerClr });
                            break;
                        }
                    }

                    next = next.next();

                    if (next.length == 0) {
                        break;
                    }
                }
            }


            `$("#svgContainer").HTMLSVGconnect({
                orientation: "auto",
                class: "my-line-style",
                paths: paths,
            });
        }

        `$(function () {
            console.log("hello jq");
            connectAll();
        });

        `$(window).resize(function () {
            connectAll();
        });
    </script>
</head>

<body>
    <div>
        <div id="svgContainer"></div>

        <pre>
"@



$_HTML_FILE_BOTTOM = 
@"
</pre>
</body>
</html>
"@