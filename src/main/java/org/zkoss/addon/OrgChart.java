package org.zkoss.addon;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.impl.XulElement;

@SuppressWarnings("serial")
public class OrgChart extends XulElement {

	/** Used to render treeitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final SpaceTreeRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}

		private void doCatch(Throwable ex) {
			if (_ctrled) {
				try {
					((RendererCtrl) _renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}

		private void doFinally() {
			if (_ctrled)
				((RendererCtrl) _renderer).doFinally();
		}

		private String render(SpaceTreeNode<?> node, String defVal) {
			try {
				String reuslt = render(node);
				return reuslt;
			} catch (Throwable e) {
				return defVal;
			}
		}

		private String render(SpaceTreeNode<?> node) throws Throwable {
			if (node == null || node.getData() == null) {
				return "{}";
			}

			JSONObject json = new JSONObject();
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl) _renderer).doTry();
				_ctrled = true;
			}
			try {
				try {
					json.put("id", node.getId());
					json.put("name", _renderer.render(node.getData()));

					if (node.isLeaf())
						json.put("children", "null");
					else {
						boolean first = true;
						StringBuffer sb = new StringBuffer();
						Iterator iter = node.getChildren().iterator();

						sb.append('[');
						while (iter.hasNext()) {
							if (first)
								first = false;
							else
								sb.append(',');

							Object value = iter.next();
							if (value == null) {
								sb.append("null");
								continue;
							}
							sb.append(render((SpaceTreeNode) value));
						}
						sb.append(']');
						json.put("children", JSONValue.parse(sb.toString()));
					}
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass().getMethod("render",
							new Class<?>[] { SpaceTreeNode.class });
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] { node });
				}
			} catch (Throwable ex) {
				throw ex;
			}
			_rendered = true;
			return json.toJSONString();
		}
	}

	/* Here's a simple example for how to implements a member field */

	static {
		addClientEvent(OrgChart.class, Events.ON_SELECT, CE_DUPLICATE_IGNORE
				| CE_IMPORTANT);
		addClientEvent(OrgChart.class, Events.ON_USER, CE_IMPORTANT);
	}
	private String _align = "center";
	private int _duration = 700;
	private int _level = 2;
	private String _nodetype = "rectangle";
	private String _orient = "left";
	private SpaceTreeModel<?> _model;
	private String _json = "{}";
	private SpaceTreeNode<?> _sel = null;
	private String _cmd = "";
	private String _addNodeJson = "{}";
	private boolean init = true;
	private transient SpaceTreeRenderer _renderer;
	private transient TreeDataListener _dataListener;
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.Tree.onInitLaterPosted";
	private static final SpaceTreeRenderer _defRend = new SpaceTreeRenderer() {
		@Override
		public String render(Object node) {
			return Objects.toString(node);
		}
	};

	public SpaceTreeNode<?> find(String id) {
		return _model.find(id);
	}

	public String getAddNodeJson() {
		return _addNodeJson;
	}
	
	public void setAddNodeJson(String addNodeJson) {
		if (!Objects.equals(_addNodeJson, addNodeJson)) {
			_addNodeJson = addNodeJson;
		}
		smartUpdate("addNodeJson", _addNodeJson);
	}

	public String getAlign() {
		return _align;
	}
	
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)
				&& ("left".equals(align) || "center".equals(align) || "right"
						.equals(align))) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	public SpaceTreeRenderer getSpaceTreeRenderer() {
		return _renderer;
	}

	public void setSpaceTreeRenderer(SpaceTreeRenderer _renderer) {
		this._renderer = _renderer;
	}

	public String getCmd() {
		return _cmd;
	}
	
	public void setCmd(String cmd) {
		if (!Objects.equals(_cmd, cmd)
				&& ("add".equals(cmd) || "remove".equals(cmd) || "refresh"
						.equals(cmd))) {
			_cmd = cmd;
		}
		smartUpdate("cmd", _cmd);
	}

	public int getDuration() {
		return _duration;
	}
	
	public void setDuration(int duration) {
		if (_duration != duration && duration >= 0) {
			_duration = duration;
			smartUpdate("duration", _duration);
		}
	}

	public String getJson() {
		return _json;
	}
	
	public void setJson(String json) {
		if (!Objects.equals(_json, json)) {
			_json = json;
			smartUpdate("json", _json);
		}
	}

	public int getLevel() {
		return _level;
	}
	
	public void setLevel(int level) {
		if (_level != level) {
			_level = level;
			smartUpdate("level", _level);
		}
	}

	public SpaceTreeModel<?> getModel() {
		return _model;
	}
	
	public void setModel(SpaceTreeModel<?> model) {
		if (model != null) {
			if (!(model instanceof SpaceTreeModel))
				throw new UiException(model.getClass() + " must implement "
						+ SpaceTreeModel.class);

			SpaceTreeNode<?> root = (SpaceTreeNode<?>) model.getRoot();
			SpaceTreeNode<?> spacetreeRoot = model.getSpaceTreeRoot();
			if (init) {
				setSelectedNode(spacetreeRoot);
				init = false;
			}
			if (_model != model) {
				if (_model != null) {
					_model.removeTreeDataListener(_dataListener);
				}
				// to ensure spacetree root should be unique
				for (TreeNode<?> child : root.getChildren()) {
					if (spacetreeRoot == child) {
						continue;
					}
					root.remove((SpaceTreeNode) child);
				}
				_model = (SpaceTreeModel<?>) model;
				initDataListener();
			}
			postOnInitRender();
		} else if (_model != null) {
			_model.removeTreeDataListener(_dataListener);
			_model = null;
		}
	}

	public String getNodetype() {
		return _nodetype;
	}
	
	public void setNodetype(String nodetype) {
		if (!Objects.equals(_nodetype, nodetype)) {
			_nodetype = nodetype;
			smartUpdate("nodetype", _nodetype);
		}
	}

	public String getOrient() {
		return _orient;
	}
	
	public void setOrient(String orient) {
		if (!Objects.equals(_orient, orient)
				&& ("left".equals(orient) || "right".equals(orient)
						|| "top".equals(orient) || "bottom".equals(orient))) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}
	
	public SpaceTreeNode<?> getSelectedNode() {
		return _sel;
	}

	public void setSelectedNode(SpaceTreeNode<?> sel) {
		if (!Objects.equals(_sel, sel)) {
			_sel = sel;
			smartUpdate("selectedNode", new Renderer().render(_sel, "{}"));
		}
	}
	
	private void setJsonWithoutUpdate() {
		SpaceTreeNode<?> spacetreeRoot = (SpaceTreeNode<?>) _model
				.getSpaceTreeRoot();
		final Renderer renderer = new Renderer();
		try {
			_json = renderChildren(renderer, spacetreeRoot);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Returns the renderer used to render items.
	 */
	@SuppressWarnings("unchecked")
	private SpaceTreeRenderer getRealRenderer() {
		return _renderer != null ? _renderer : _defRend;
	}

	/**
	 * The default zclass is "z-orgchart"
	 */
	public String getZclass() {
		return (this._zclass != null ? this._zclass : "z-orgchart");
	}

	/*
	 * Initial Tree data listener
	 */
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new TreeDataListener() {
				public void onChange(TreeDataEvent event) {
					onTreeDataChange(event);
				}
			};

		_model.addTreeDataListener(_dataListener);
	}

	/**
	 * Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 * 
	 * @since 6.0.0
	 */
	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		renderTree();
	}

	// -- ComponentCtrl --//
	/**
	 * Handles when the tree model's content changed
	 */
	@SuppressWarnings("unchecked")
	private void onTreeDataChange(TreeDataEvent event) {
		final int type = event.getType();
		final int[] path = event.getPath();
		final TreeModel<?> tm = event.getModel();

		SpaceTreeNode<?> node = null;
		if (path == null) {
			node = (SpaceTreeNode<?>) _model.getSpaceTreeRoot();
		} else {
			node = (SpaceTreeNode<?>) event.getModel().getChild(path);
		}

		if (node != null)
			switch (type) {
			case TreeDataEvent.INTERVAL_ADDED:
				SpaceTreeNode<?> lastChild = (SpaceTreeNode<?>) node
						.getChildAt(node.getChildCount() - 1);
				setAddNodeJson(new Renderer().render(lastChild, "{}"));
				setJsonWithoutUpdate();
				setCmd("add");
				return;
			case TreeDataEvent.INTERVAL_REMOVED:
				_model.addToSelection((TreeNode) node);
				setJsonWithoutUpdate();
				setCmd("remove");
				return;
			case TreeDataEvent.CONTENTS_CHANGED:
				renderTree();
				setCmd("refresh");
				return;
			}

	}

	private void postOnInitRender() {
		// 20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	private String renderChildren(Renderer renderer, SpaceTreeNode<?> node)
			throws Throwable {
		return renderer.render(node);
	}

	// super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		if (_level != 2)
			render(renderer, "level", _level);
		if (_duration != 700)
			render(renderer, "duration", _duration);
		if (!Objects.equals(_orient, "left"))
			render(renderer, "orient", _orient);
		if (!Objects.equals(_align, "center"))
			render(renderer, "align", _align);
		if (!Objects.equals(_nodetype, "rectangle"))
			render(renderer, "nodetype", _nodetype);
		if (!Objects.equals(_json, "{}"))
			render(renderer, "json", _json);
		if (!Objects.equals(_cmd, ""))
			render(renderer, "cmd", _cmd);
		if (!Objects.equals(_sel, ""))
			render(renderer, "selectedNode", new Renderer().render(_sel, "{}"));
		if (!Objects.equals(_addNodeJson, "{}"))
			render(renderer, "addNodeJson", _addNodeJson);

	}

	private void renderTree() {
		SpaceTreeNode<?> node = (SpaceTreeNode<?>) _model.getSpaceTreeRoot();
		final Renderer renderer = new Renderer();
		try {
			setJson(renderChildren(renderer, node));
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		// notify the tree when items have been rendered.
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);
	}

	/**
	 * After remove(child) is completed in client, the selected node should be
	 * the parent node. In this moment, onSelect event should not be trigger, so
	 * we have to replace onSelect event with onUser event.
	 */
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		SelectEvent evt = SelectEvent.getSelectEvent(request);
		Map data = request.getData();

		if (Events.ON_SELECT.equals(cmd) || Events.ON_USER.equals(cmd)) {
			String seldNodeStr = data.get("selectedNode").toString();
			JSONObject json = (JSONObject) JSONValue.parse(seldNodeStr);
			SpaceTreeNode<?> seldNode = find(json.get("id").toString());
			_model.addToSelection((TreeNode) seldNode);
			setSelectedNode(seldNode);
			Events.postEvent(evt);
		} else {
			super.service(request, everError);
		}
	}
	
}
