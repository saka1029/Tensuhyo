package saka1029.tensuhyo.parser;

public class 漢数字 extends StencilOrdered {

    public static final String 漢数9 = "一二三四五六七八九";
    public static final String 漢数10 = 漢数9 + "十";
    public static final String 漢数 = "(一|[二三四五六七八九](十[一二三四五六七八九]?)?|十[一二三四五六七八九]?)";
    public static final String 漢数_ORDER_PAT = "(?<" + ORDER + ">" + 漢数 + ")";
    public static final String 漢数の漢数_ORDER_PAT = "(?<" + ORDER + ">" + 漢数 + "(の" + 漢数 + ")?)";
    public static final String 漢数の漢数の漢数_ORDER_PAT = "(?<" + ORDER + ">" + 漢数 + "(の" + 漢数 + "(の" + 漢数 + ")?" + ")?)";

    public static int numberize(String s) {
        s = s.replaceFirst("^十$", "10");
        s = s.replaceFirst("^十", "1");
        s = s.replaceFirst("十$", "0");
        s = s.replaceFirst("十", "");
        s = s.replaceAll("一", "1");
        s = s.replaceAll("二", "2");
        s = s.replaceAll("三", "3");
        s = s.replaceAll("四", "4");
        s = s.replaceAll("五", "5");
        s = s.replaceAll("六", "6");
        s = s.replaceAll("七", "7");
        s = s.replaceAll("八", "8");
        s = s.replaceAll("九", "9");
        return Integer.parseInt(s);
    }

    public static class MainSub {
        public final int main, sub, minor;
        public MainSub(int main, int sub, int minor) {
            this.main = main;
            this.sub = sub;
            this.minor = minor;
        }
    }
    
    public static MainSub mainSub(String order) {
        int main;
        int sub = 0;
        int minor = 0;
        String[] nos = order.split("の");
        main = numberize(nos[0]);
        if (nos.length > 1) sub = numberize(nos[1]);
        if (nos.length > 2) minor = numberize(nos[2]);
        return new MainSub(main, sub, minor);
    }
    
    public static String fileNo(Node node) {
        String[] nos = node.order().split("の");
        StringBuilder sb = new StringBuilder();
        for (String no : nos)
            sb.append("-").append(numberize(no));
        return sb.substring(1);
    }

	@Override
	String fileName(Node node) {
	    return node.parent().fileName() + "." + 漢数字.fileNo(node);
	}

    public static int mainSub(Node node, Node prev) {
        if (prev != null && !node.nodeName().equals(prev.nodeName())) return -1;
        MainSub n = mainSub(node.order());
        MainSub p = new MainSub(0, 0, 0);
        if (prev != null) p = mainSub(prev.order());
        if (n.main == p.main + 1)
            return n.main;
        else if (n.main == p.main &&
                (n.sub == p.sub + 1 ||
                n.sub == p.sub + 2 ||
                n.sub == p.sub && (n.minor == p.minor + 1 || n.minor == p.minor + 2)))
            return n.main;
        else
            return -1;
    }

    @Override protected int no(Node node, Node prev) { return mainSub(node, prev); }
    
	public static final 漢数字 value = new 漢数字();
	
	private 漢数字() {
		super("漢数字", MARGIN_PAT +
			"(?<" + NUMBER + ">" + 漢数の漢数の漢数_ORDER_PAT  + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(漢数字第.value);
		addCandidateParent(漢数字別表第.value);
	}

}
