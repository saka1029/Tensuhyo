package saka1029.tensuhyo.generator;

import saka1029.tensuhyo.dictionary.WordAccessor;

public class NodeWordAccessor implements WordAccessor<NodeWord> {

	@Override
	public String getType(NodeWord bean) { return bean.type(); }

	@Override
	public String getName(NodeWord bean) { return bean.name(); }

}
