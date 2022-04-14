package saka1029.tensuhyo.run;

import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.parser.ParseException;

public class R0110 {
    Facade f = new Facade();
    {
        f.元号 = "令和";
        f.年度 = "01";
        f.旧元号 = "平成";
        f.旧年度 = "30";
        f.医科告示PDF = new String[] {
    //		"0000196284.pdf",	// 本文
    //		"0000196285.pdf",	// 目次
            "0000196286.pdf",	// ＜第1章＞ 初・再診料
            "0000196287.pdf",	// 入院料等
            "0000196288.pdf",	// ＜第2章＞ 医学管理等
            "0000196289.pdf",	// 在宅医療
            "0000196290.pdf",	// 検査
            "0000196291.pdf",	// 画像診断
            "0000196292.pdf",	// 投薬
            "0000196293.pdf",	// 注射
            "0000196294.pdf",	// リハビリテーション
            "0000196295.pdf",	// 精神科専門医療療法
            "0000196296.pdf",	// 処置
            "0000196297.pdf",	// 手術
            "0000196298.pdf",	// 麻酔
            "0000196299.pdf",	// 放射線治療
            "0000196300.pdf",	// 病理診断
            "0000196301.pdf",	// ＜第3章＞ 介護老人保健施設入所者に係る診察料
            "0000196302.pdf",	// ＜第4章＞ 経過措置
        };
        f.医科通知PDF = new String[] {
            "0000196307.pdf",	// 別添1（医科点数表）
        };
        f.医科様式PDF = new String[] {
            "0000196308.pdf",
            "2018-03-30-改訂.pdf"  // 改訂版を下に書く
        };
        f.歯科告示PDF = new String[] {
            "0000196303.pdf",	// 別表第2 （歯科点数表）
        };
        f.歯科通知PDF = new String[] {
            "0000196309.pdf",	// 別添2（歯科点数表）
        };
        f.歯科様式PDF = new String[] {
            "0000196310.pdf",
        };
        f.調剤告示PDF = new String[] {
            "0000196304.pdf",	// 別表第3 （調剤点数表）
        };
        f.調剤通知PDF = new String[] {
            "0000196311.pdf",	// 別添3（調剤点数表）
        };
        f.調剤様式PDF = new String[] {
            "0000196312.pdf",
        };
        f.施設基準告示PDF = new String[] {
            "0000196314.pdf",
            "0000196317.pdf",
        };
        f.施設基準通知PDF = new String[] {
            "0000196315.pdf",
            "0000196318.pdf",
        };
        f.施設基準基本様式PDF = new String[] {
            "betten/0000196315.pdf",	// 別添７
            "betten/2018-03-30-改訂-基本診療料.pdf",
            "betten/2018-05-01-改訂-基本診療料.pdf",
        };
        f.施設基準特掲様式PDF = new String[] {
            "betten/0000196318.pdf",	// 別添２
            "betten/2018-03-30-改訂-特掲診療料.pdf",
            "betten/2018-05-01-改訂-特掲診療料.pdf",
        };
    }

    @Test public void k_kokuji() throws IOException { f.施設基準告示変換(); }
    @Test public void k_tuti() throws IOException { f.施設基準通知変換(); }
    @Test public void k_html() throws IOException, ParseException { f.施設基準HTML変換(); }
    @Test public void k_kihon() throws IOException, COSVisitorException { f.施設基準基本様式(); }
    @Test public void k_tokkei() throws IOException, COSVisitorException { f.施設基準特掲様式(); }

    @Test public void i_kokuji() throws IOException { f.医科告示変換(); }
    @Test public void i_tuti() throws IOException { f.医科通知変換(); }
    @Test public void i_html() throws IOException, ParseException { f.医科HTML変換(); }
    @Test public void i_kubun() throws IOException, ParseException { f.医科区分一覧(); }
    @Test public void i_yoshiki() throws IOException, COSVisitorException { f.医科様式(); }

    @Test public void s_kokuji() throws IOException { f.歯科告示変換(); }
    @Test public void s_tuti() throws IOException { f.歯科通知変換(); }
    @Test public void s_html() throws IOException, ParseException { f.歯科HTML変換(); }
    @Test public void s_kubun() throws IOException, ParseException { f.歯科区分一覧(); }
    @Test public void s_yoshiki() throws IOException, COSVisitorException { f.歯科様式(); }

    @Test public void t_kokuji() throws IOException { f.調剤告示変換(); }
    @Test public void t_tuti() throws IOException { f.調剤通知変換(); }
    @Test public void t_html() throws IOException, ParseException { f.調剤HTML変換(); }
    @Test public void t_kubun() throws IOException, ParseException { f.調剤区分一覧(); }
    @Test public void t_yoshiki() throws IOException, COSVisitorException { f.調剤様式(); }
}
