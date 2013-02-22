package org.zkoss.addon;

import org.zkoss.zul.DefaultTreeModel;

@SuppressWarnings("serial")
public class SpaceTreeModel<E> extends DefaultTreeModel<E> {

	@SuppressWarnings("rawtypes")
	public SpaceTreeModel(SpaceTreeNode root) {
		super(root);
	}

	@SuppressWarnings("rawtypes")
	public SpaceTreeNode getSpaceTreeRoot() {
		return (SpaceTreeNode) getRoot().getChildren().get(0);
	}
}
