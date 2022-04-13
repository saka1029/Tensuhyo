package saka1029.tensuhyo.run;

import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.parser.ParseException;

public class R0404 {
    Facade f = new Facade();
    {
        f.元号 = "令和";
        f.年度 = "04";
        f.旧元号 = "令和";
        f.旧年度 = "02";
        f.医科告示PDF = new String[] {
                "000907834.pdf", // 目次 本文
        };
        f.医科通知PDF = new String[] {
                "000907838.pdf", // 通知
        };
        f.医科様式PDF = new String[] {
                "000907839.pdf", // 様式
        };
        f.歯科告示PDF = new String[] {
                "000907835.pdf", // 別表第2 （歯科点数表）
        };
        f.歯科通知PDF = new String[] {
                "000907840.pdf", // 別添2（歯科点数表）
        };
        f.歯科様式PDF = new String[] {
                "000907841.pdf",
        };
        f.調剤告示PDF = new String[] {
                "000907836.pdf", // 別表第3 （調剤点数表）
        };
        f.調剤通知PDF = new String[] {
                "000907842.pdf", // 別添3（調剤点数表）
        };
        f.調剤様式PDF = new String[] {
                "000907843.pdf",
        };
        f.施設基準告示PDF = new String[] {
                "000907845.pdf",
                "000908781.pdf",

        };
        f.施設基準通知PDF = new String[] {
                "000907989.pdf",
                "000907862.pdf",
        };
        f.施設基準基本様式PDF = new String[] {
                "betten/000907989-y.pdf", // 別添７
        };
        f.施設基準特掲様式PDF = new String[] {
                "betten/000907862-y.pdf", // 別添２
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
