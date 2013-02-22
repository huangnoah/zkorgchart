package org.zkoss.addon;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONObject;
import org.zkoss.zul.DefaultTreeNode;

public class SpaceTreeNode<E extends SpaceTreeData<?>> extends
		DefaultTreeNode<E> implements JSONAware {

	public SpaceTreeNode(E data, Collection<? extends SpaceTreeNode<E>> children) {
		super(data, children);
	}

	public String getId() {
		return getData().getId();
	}

	public String getName() {
		return getData().getName();
	}

	public boolean hasChildren() {
		return getChildren() != null && getChildren().size() != 0;
	}

	public void setName(String name) {
		getData().setName(name);
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("name", getName());
		E dataObj = getData();

		List<Field> fields = Arrays.asList(dataObj.getClass()
				.getDeclaredFields());
		JSONObject props = new JSONObject();
		String name = null;

		for (Field field : fields) {
			name = field.getName();
			if ("id".equals(name) || "name".equals(name)) {
				continue;
			}

			field.setAccessible(true);
			try {
				props.put(name, field.get(dataObj));
			} catch (IllegalArgumentException e) {
				props.put(name, null);
			} catch (IllegalAccessException e) {
				props.put(name, null);
			} finally {
				field.setAccessible(false);
			}
		}

		json.put("data", props);
		json.put("children", getChildren());
		return json.toJSONString();
	}

}
