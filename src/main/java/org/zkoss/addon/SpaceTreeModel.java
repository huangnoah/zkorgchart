package org.zkoss.addon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeNode;

@SuppressWarnings("serial")
public class SpaceTreeModel<E> extends DefaultTreeModel<E> {

	public SpaceTreeModel(SpaceTreeNode<E> root) {
		super(root);
	}

	public SpaceTreeNode<E> getSpaceTreeRoot() {
		List<TreeNode<E>> children = getRoot().getChildren();
		if (children.size() > 0)
			return (SpaceTreeNode<E>) children.get(0);
		else
			return null;
	}
	
	public SpaceTreeNode<E> getSelectedNode() {
		return (SpaceTreeNode<E>) getSelection().iterator().next();
	}

	public SpaceTreeNode<E> find(String id) {
		return query((TreeNode<E>) getRoot(), id,
				new HashMap<String, Boolean>());
	}

	// graph depth-first-search algorithms
	private SpaceTreeNode<E> query(TreeNode<E> node, String id,
			Map<String, Boolean> checked) {

		// check children
		for (TreeNode<E> rawChild : node.getChildren()) {
			SpaceTreeNode<E> child = (SpaceTreeNode<E>) rawChild;
			String childId = child.getId() + ""; 

			if (checked.get(childId) != null) {
				// if it checked, then skip
				continue;
			} else if (child.hasChildren()) {
				// check its children
				return query(child, id, checked);
			} else {
				// check leaf node id
				if (childId.equals(id)) {
					return child;
				} else {
					checked.put(childId, true);
				}
			}
		}

		// check itself after all children is checked
		SpaceTreeNode<E> thisNode = (SpaceTreeNode<E>) node;
		if ((thisNode.getId() + "").equals(id)) {
			return (SpaceTreeNode<E>) node;
		} else {
			checked.put(thisNode.getId() + "", true);
		}

		TreeNode<E> parent = thisNode.getParent();
		if (parent != null) {
			// check bother if parent exists
			return query(parent, id, checked);
		} else {
			// return null if not found
			return null;
		}

	}
	
}
