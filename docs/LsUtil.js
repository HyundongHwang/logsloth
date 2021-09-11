"use strict";

let LsUtil = {

    MD5_SALT : "MD5_SALT",

    appendOpacityToColorStr : (clrStr, opacity) => {
        let r = parseInt(clrStr.substr(1, 2), 16);
        let g = parseInt(clrStr.substr(3, 2), 16);
        let b = parseInt(clrStr.substr(5, 2), 16);
        let rgba = `rgba(${r}, ${g}, ${b}, ${opacity})`;
        return rgba;
    },

    str2FgBg : (str) => {

        str += LsUtil.MD5_SALT;
        let hash = md5(str);
        let hashInt = parseInt(`0x${hash.substr(0, 4)}`);
        const HEX_ARRAY = ["00", "33", "66", "99", "cc", "ff"];
        let r = parseInt(hashInt / 36) % 6;
        let g = parseInt(hashInt / 6) % 6;
        let b = hashInt % 6;
        let bg = `#${HEX_ARRAY[r]}${HEX_ARRAY[g]}${HEX_ARRAY[b]}`;
        let fg;

        if (g > 3) {
            fg = "#000000";
        } else {
            fg = "#ffffff";
        }

        return {fg : fg, bg : bg,};
    },

    openSelectRadioDlg: (title, content, selArray, onOk, onCancel) => {

        let dlg = $("<div></div>")
            .attr("title", title)
            .css("font-size", "20px");

        let contentTag = $("<div></div>")
            .css("margin-top", "10px")
            .css("font-size", "20px")
            .html(content);

        dlg.append(contentTag);

        for (let k in selArray) {
            let item = selArray[k];

            let rowTag = $("<div></div>")
                .css("margin-top", "10px");

            dlg.append(rowTag);

            let inputTag = $(`<input type='radio' name='openSelectRadioDlg' value='${item}'>`)
                .css("vertical-align", "middle");

            let labelTag = $(`<label>${item}</label>`)
                .css("font-size", "15px")
                .css("vertical-align", "middle")
                .css("margin-left", "10px");

            labelTag.prepend(inputTag);
            rowTag.append(labelTag);
        }

        dlg.dialog({
            resizable: true,
            modal: true,
            minWidth: 1000,
            buttons: {
                Ok: () => {
                    dlg.dialog("close");
                    let selItem = $(":input:radio[name=openSelectRadioDlg]:checked").val();
                    onOk(selItem);
                },
                Cancel: () => {
                    dlg.dialog("close");
                    onCancel();
                }
            },
            open: ()=> {
                $("input[type='checkbox']").checkboxradio();
                $("input[type='radio']").checkboxradio();
            },
        });
    },

    uploadTxt2Nubes : (txt, onSucess, onError) => {
        let hash = md5(txt);
        let url = `http://10.105.64.25:8000/v1/logsloth/${hash}.log?callback=?`;

        $.ajax({
            type: "POST",
            url: url,
            dataType: "text",
            data: txt,
            success: function (data, status, jqXHR) {
                onSucess(url);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.statusCode().status === 409) {
                    onSucess(url);
                } else {
                    console.error(`failed !!! \n${textStatus} \n${url}`);
                }
            }
        });

    },

    isStrNullOrEmpty : (str) => {
        if (str === undefined)
            return true;

        if (str === null)
            return true;

        if (str === "")
            return true;

        return false;
    },
}