package saka1029.tensuhyo.pdf;

public interface StringFunction {

    public static final String 頁番号 = "^\\s*[-ー‐－―]\\s*[0-9０-９]+\\s*[-ー‐－―]\\s*$";
    public static final String 漢字頁番号 = "^\\s*[〇一二三四五六七八九]+頁\\s*$";
    
    String eval(String line);
    
}
