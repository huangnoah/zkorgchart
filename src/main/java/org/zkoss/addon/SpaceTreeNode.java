package org.zkoss.addon;

import java.util.Collection;

import org.zkoss.zul.DefaultTreeNode;

import test.model.UserDataBean;

public class SpaceTreeNode<E> extends DefaultTreeNode<E> {

	private static int genId = 0;
	private int id;

	public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
		super(data, children);
		id = genId++;
	}

	@Override
	protected void setParent(DefaultTreeNode<E> parent) {
		// this method is called after the parent add child, so parent.getChildCount() must be equal or grater than 1
		if (isRoot(parent) && parent.getChildCount() > 1)
			try {
				throw new Exception("the root has one child at most");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			super.setParent(parent);
	};
	
	@Override
	public void add(org.zkoss.zul.TreeNode<E> child) {
		if(isRoot(this) && getChildCount() >= 1)
			try {
				throw new Exception("the root has one child at most");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			super.add(child);
	};

	private static boolean isRoot(DefaultTreeNode node) {
		return node != null && node.getData() == null
				&& node.getParent() == null;
	}

	public int getId() {
		return id;
	}

	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() != 0;
	}

}
