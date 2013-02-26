package org.zkoss.addon;

import org.zkoss.json.JSONObject;

public interface SpaceTreeNodeRenderer<T> {
	String render(SpaceTreeNode<T> node);
	String getJSONId(SpaceTreeNode node);
	String getJSONName(SpaceTreeNode node);
	JSONObject getJSONData(SpaceTreeNode node);
	String toJSONString(SpaceTreeNode node);
}
