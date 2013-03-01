package org.zkoss.addon;

import java.util.Collection;

import org.zkoss.zul.DefaultTreeNode;

public class SpaceTreeNode<E> extends DefaultTreeNode<E> {

	private static int genId = 0;
	private int id;
	public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
		super(data, children);
		id = genId++;
	}

	public int getId() {
		return id;
	}

	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() != 0;
	}

}
