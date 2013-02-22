package org.zkoss.addon;

public interface SpaceTreeNodeRenderer<T extends SpaceTreeData> {
	String render(SpaceTreeNode<T> node);
}
