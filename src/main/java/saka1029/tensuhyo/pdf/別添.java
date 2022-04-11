package saka1029.tensuhyo.pdf;

public class 別添 {

    public final String pdfFileName;
    public final String header;
    public final String title;

    public 別添(String pdfFileName, String header, String title) {
        this.pdfFileName = pdfFileName;
        this.header = header;
        this.title = title;
    }
    
    @Override
    public String toString() {
        return String.format("別添(%s,%s,%s)", pdfFileName, header, title);
    }
}
