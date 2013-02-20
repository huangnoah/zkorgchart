package org.zkoss.addon;

import org.zkoss.zul.DefaultTreeModel;

public interface SpaceTreeNodeRenderer<T extends SpaceTreeData<?>> {
	String render(SpaceTreeNode<T> node);
}
