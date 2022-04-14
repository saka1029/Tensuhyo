package saka1029.tensuhyo.run;

import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.parser.ParseException;

public class R0204 {
    Facade f = new Facade();
    {
        f.元号 = "令和";
        f.年度 = "02";
        f.旧元号 = "令和";
        f.旧年度 = "01";
        f.医科告示PDF = new String[] {
    //       "000603745.pdf", // 本文
    //       "000603746.pdf", // 目次
            "000603747.pdf", // 第1章 初・再診料
            "000603748.pdf", //       入院料等
            "000603749.pdf", // 第2章 医学管理等
            "000603750.pdf", //       在宅医療
            "000603751.pdf", //       検査
            "000603753.pdf", //       画像診断
            "000603756.pdf", //       投薬
            "000603757.pdf", //       注射
            "000603758.pdf", //       リハビリテーション
            "000603759.pdf", //       精神科専門療法
            "000603760.pdf", //       処置
            "000603762.pdf", //       手術
            "000603763.pdf", //       麻酔
            "000603764.pdf", //       放射線治療
            "000603765.pdf", //       病理診断
            "000603766.pdf", // 第3章 介護老人保健施設入所者に係る診察料
            "000603769.pdf", // 第4章 経過措置
        };
        f.医科通知PDF = new String[] {
            "000603981.pdf", // 通知
        };
        f.医科様式PDF = new String[] {
            "000603917.pdf", // 様式
        };
        f.歯科告示PDF = new String[] {
            "000603770.pdf",	// 別表第2 （歯科点数表）
        };
        f.歯科通知PDF = new String[] {
            "000603918.pdf",	// 別添2（歯科点数表）
        };
        f.歯科様式PDF = new String[] {
            "000603919.pdf",
        };
        f.調剤告示PDF = new String[] {
            "000603771.pdf",	// 別表第3 （調剤点数表）
        };
        f.調剤通知PDF = new String[] {
            "000603920.pdf",	// 別添3（調剤点数表）
        };
        f.調剤様式PDF = new String[] {
            "000603921.pdf",
        };
        f.施設基準告示PDF = new String[] {
            "000602943.pdf",
            "000602944.pdf",
        };
        f.施設基準通知PDF = new String[] {
            "000603890.pdf",
            "000603894.pdf",
        };
        f.施設基準基本様式PDF = new String[] {
            "betten/000603890.pdf",	// 別添７
        };
        f.施設基準特掲様式PDF = new String[] {
            "betten/000603894.pdf",	// 別添２
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
