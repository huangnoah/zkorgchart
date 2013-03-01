package org.zkoss.addon;

import org.zkoss.zk.ui.Component;

public interface SpaceTreeRenderer<T> {
	String render(Component owner, T data);
}
