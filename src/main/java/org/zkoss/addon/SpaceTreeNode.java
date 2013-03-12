package org.zkoss.addon;

import java.util.Collection;

import org.zkoss.zul.DefaultTreeNode;

import test.model.UserDataBean;

public class SpaceTreeNode<E> extends DefaultTreeNode<E> {

	private static int genId = 0;
	private int id;
	private boolean root;

	public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
		super(data, children);
		id = genId++;
		root = false;
	}

	public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children,
			boolean root) {
		super(data, children);
		id = genId++;
		this.root = root;
	}

	@Override
	protected void setParent(DefaultTreeNode<E> parent) {
		// this method is called after the parent add child, so
		// parent.getChildCount() must be equal or grater than 1
		if (((SpaceTreeNode<E>) parent).isRoot() && parent.getChildCount() > 1)
			try {
				throw new Exception("the root has one child at most");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if (isRoot()) {
			try {
				throw new Exception("the root cant be the child");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			super.setParent(parent);
	};

	@Override
	public void add(org.zkoss.zul.TreeNode<E> child) {
		if (isRoot() && getChildCount() >= 1)
			try {
				throw new Exception("the root has one child at most");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if (((SpaceTreeNode<E>) child).isRoot())
			try {
				throw new Exception("the root cant be child");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			super.add(child);
	};

	@Override
	public org.zkoss.zul.DefaultTreeModel<E> getModel() {
		return super.getModel();
	};

	public boolean isRoot() {
		return root;
	}

	public int getId() {
		return id;
	}

	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() != 0;
	}

}
