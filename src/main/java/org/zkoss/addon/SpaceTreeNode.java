package org.zkoss.addon;

import java.util.Collection;

import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class SpaceTreeNode<E> extends DefaultTreeNode<E> implements JSONAware {


	public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
		super(data, children);
	}

	public String getJSONId() {
		return OrgChart.getRealRenderer().getJSONId(this);
	}

	public String getJSONName() {
		return OrgChart.getRealRenderer().getJSONName(this);
	}
	
	public JSONObject getJSONData() {
		return OrgChart.getRealRenderer().getJSONData(this);
	}

	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() != 0;
	}

	@Override
	public String toJSONString() {
		return OrgChart.getRealRenderer().toJSONString(this);
	}

	@Override
	public void remove(TreeNode<E> child) {
		if (!((SpaceTreeNode<E>) child).isSpaceTreeRoot()) {
			super.remove(child);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SpaceTreeNode) {
			return toJSONString().equals(((SpaceTreeNode) obj).toJSONString());
		} else {
			return false;
		}
	}

	public boolean isSpaceTreeRoot() {
		return equals(((SpaceTreeModel<E>) getModel()).getSpaceTreeRoot());
	}

}
