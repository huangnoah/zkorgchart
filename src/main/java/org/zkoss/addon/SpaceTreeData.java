package org.zkoss.addon;

import org.zkoss.json.JSONObject;

public interface SpaceTreeData<E extends JSONObject> {

	String getId();
	void setId(String id);
	String getName();
	void setName(String name);
	E getJsonData();
}
