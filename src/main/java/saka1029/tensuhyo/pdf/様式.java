package saka1029.tensuhyo.pdf;

/**
 * 様式一覧をHTML出力するためのレコードです。
 */
public record 様式(String name, String id, int startPage, int endPage, String title) {
}
