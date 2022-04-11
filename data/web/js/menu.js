(function() {
    const menu = document.getElementById("menu");
    if (menu == null) return;
    var historyMenu = "";
    historyMenu += ""
        + "<form id='cse-search-box' action='http://google.com/cse'>\n"
        + "    <input type='hidden' name='cx' value='008433866737420098736:zxo34cxrlkc' />\n"
        + "    <input type='hidden' name='ie' value='UTF-8' />\n"
        + "    <input type='text' name='q' size='50' placeholder='サイト内検索' />\n"
        + "    <input type='submit' name='sa' value='検索' />\n"
        + "    <img src='http://www.google.com/cse/images/google_custom_search_smwide.gif' align='middle'>\n"
        + "</form>\n";
    historyMenu += "<br>";
    const link = " <a class='link-menu' target='_top' href='";
    const path = location.pathname;
    historyMenu += link + path.replace(/(\/\d\d\/.\/).*/, "$1index.html") + "'>本文</a>";
    historyMenu += link + path.replace(/(\/\d\d\/.\/)(.*)\.html/, "$1kubun.html#$2") + "'>区分番号一覧</a>";
    historyMenu += link + "yoshiki.html'>様式一覧</a>";
    historyMenu += link + "../k/0.html'>施設基準</a>";
    historyMenu += " | ";
//  historyMenu += link= + path.replace(/(\/\d\d\/.\/)(.*)\.html/, "$1hikaku.html?$2") + "'>比較</a>";
    const rightYear = path.replace(/.*\/(\d\d)\/.*/, "$1");
    const nendoArray = ["平成26", "平成28", "平成30", "令和01", "令和02", "令和04"];
    const nendoNew = "令和04";
    nendoArray.forEach(n => {
        const y = n.substring(n.length - 2);
        if (y != rightYear) {
            const r = path.replace(/\/\d\d\//, "/" + y + "/");
            if (n == nendoNew)
                historyMenu += " <a class='link-menu-new' target='_top' href='" + r + "'>" + n + "年版</a>"
            else
                historyMenu += link + r + "'>" + n + "年版</a>"
        }
    });
    historyMenu += " | ";
    nendoArray.forEach(n => {
        const y = n.substring(n.length - 2);
        if (y != rightYear) {
            const yy = path.replace(/.*\/(\d\d)\/.\/.*\.html/, "$1");
            const tp = path.replace(/.*\/\d\d(\/.\/.*)\.html/, "$1");
            const url = link + "../../hikaku.html"
                + "?l=" + y + tp
                + "&r=" + yy + tp
                + "'>" + n + "年と比較</a>";
            historyMenu += url;
        }
    });
    menu.innerHTML = historyMenu;
})();
