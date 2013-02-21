package org.zkoss.addon;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.impl.XulElement;

public class OrgChart<E extends SpaceTreeData<?>> extends XulElement {

	/** Used to render treeitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final SpaceTreeNodeRenderer _renderer;
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

		@SuppressWarnings("unchecked")
		private String render(SpaceTreeNode<?> node) throws Throwable {
			String json = null;
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl) _renderer).doTry();
				_ctrled = true;
			}
			try {
				try {
					json = _renderer.render(node);
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
			return json;
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
	private DefaultTreeModel<E> _model;
	private String _json = "{}";
	private String _selectedNodeId;
	private String _cmd = "";
	private String _addNodeJson = "{}";
	private boolean init = true;
	private transient SpaceTreeNodeRenderer<?> _renderer;
	private transient TreeDataListener _dataListener;
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.Tree.onInitLaterPosted";
	private static final SpaceTreeNodeRenderer _defRend = new SpaceTreeNodeRenderer() {
		@Override
		public String render(SpaceTreeNode node) {
			if (node.getData() == null) {
				return "{}";
			} else {
				return node.toJSONString();
			}
		}
	};

	public SpaceTreeNode<E> find(String id) {
		return query(_model.getRoot(), id, new HashMap<String, Boolean>());
	}

	public String getAddNodeJson() {
		return _addNodeJson;
	}

	public String getAlign() {
		return _align;
	}

	public String getCmd() {
		return _cmd;
	}

	public int getDuration() {
		return _duration;
	}

	public String getJson() {
		return _json;
	}

	public int getLevel() {
		return _level;
	}

	public DefaultTreeModel getModel() {
		return _model;
	}

	public String getNodetype() {
		return _nodetype;
	}

	public String getOrient() {
		return _orient;
	}

	/**
	 * Returns the renderer used to render items.
	 */
	private SpaceTreeNodeRenderer getRealRenderer() {
		return _renderer != null ? _renderer : _defRend;
	}

	public SpaceTreeNode<E> getSelectedNode() {
		return find(_selectedNodeId);
	}

	public String getSelectedNodeId() {
		return _selectedNodeId;
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
	private void onTreeDataChange(TreeDataEvent event) {
		final int type = event.getType();
		final int[] path = event.getPath();
		final TreeModel tm = event.getModel();

		SpaceTreeNode node = (SpaceTreeNode) event.getModel().getChild(path);

		if (node != null)
			switch (type) {
			case TreeDataEvent.STRUCTURE_CHANGED:
				renderTree();
				return;
			case TreeDataEvent.INTERVAL_ADDED:
				SpaceTreeNode lastChild = (SpaceTreeNode) node.getChildAt(node
						.getChildCount() - 1);
				setAddNodeJson(getRealRenderer().render(lastChild));
				renderTree();
				setCmd("add");
				return;
			case TreeDataEvent.INTERVAL_REMOVED:
				renderTree();
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

	// graph depth-first-search algorithms
	@SuppressWarnings("unchecked")
	private SpaceTreeNode<E> query(TreeNode<E> node, String id,
			Map<String, Boolean> checked) {

		// check children
		for (TreeNode<E> rawChild : node.getChildren()) {
			SpaceTreeNode<E> child = (SpaceTreeNode<E>) rawChild;
			String childId = child.getId();

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
		if (thisNode.getId().equals(id)) {
			return (SpaceTreeNode<E>) node;
		} else {
			checked.put(thisNode.getId(), true);
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

	private String renderChildren(Renderer renderer, SpaceTreeNode node)
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
		if (!Objects.equals(_selectedNodeId, ""))
			render(renderer, "selectedNodeId", _selectedNodeId);
		if (!Objects.equals(_addNodeJson, "{}"))
			render(renderer, "addNodeJson", _addNodeJson);

	}

	private void renderTree() {
		SpaceTreeNode node = (SpaceTreeNode) _model.getRoot();
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

	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		SelectEvent evt = SelectEvent.getSelectEvent(request);
		Map data = request.getData();

		if (Events.ON_SELECT.equals(cmd) || Events.ON_USER.equals(cmd)) {
			setSelectedNodeId(data.get("selectedNodeId").toString());
			Events.postEvent(evt);
		} else {
			super.service(request, everError);
		}
	}

	public void setAddNodeJson(String addNodeJson) {
		if (!Objects.equals(_addNodeJson, addNodeJson)) {
			_addNodeJson = addNodeJson;
		}
		smartUpdate("addNodeJson", _addNodeJson);
	}

	public void setAlign(String align) {
		if (!Objects.equals(_align, align)
				&& ("left".equals(align) || "center".equals(align) || "right"
						.equals(align))) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	public void setCmd(String cmd) {
		if (!Objects.equals(_cmd, cmd)) {
			_cmd = cmd;
		}
		smartUpdate("cmd", _cmd);
	}

	public void setDuration(int duration) {
		if (_duration != duration && duration >= 0) {
			_duration = duration;
			smartUpdate("duration", _duration);
		}
	}

	public void setJson(String json) {
		if (!Objects.equals(_json, json)) {
			_json = json;
			smartUpdate("json", _json);
		}
	}

	public void setLevel(int level) {
		if (_level != level) {
			_level = level;
			smartUpdate("level", _level);
		}
	}

	public void setModel(DefaultTreeModel model) {
		if (init) {
			setSelectedNodeId(((SpaceTreeNode) model.getRoot()).getId());
			init = false;
		}

		if (model != null) {
			if (!(model instanceof DefaultTreeModel))
				throw new UiException(model.getClass() + " must implement "
						+ DefaultTreeModel.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeTreeDataListener(_dataListener);
				}

				_model = model;
				initDataListener();
			}
			postOnInitRender();
		} else if (_model != null) {
			_model.removeTreeDataListener(_dataListener);
			_model = null;
		}
	}

	public void setNodetype(String nodetype) {
		if (!Objects.equals(_nodetype, nodetype)) {
			_nodetype = nodetype;
			smartUpdate("nodetype", _nodetype);
		}
	}

	public void setOrient(String orient) {
		if (!Objects.equals(_orient, orient)
				&& ("left".equals(orient) || "right".equals(orient)
						|| "top".equals(orient) || "bottom".equals(orient))) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	public void setSelectedNodeId(String selectedNodeId) {
		if (!Objects.equals(_selectedNodeId, selectedNodeId)) {
			_selectedNodeId = selectedNodeId;
			smartUpdate("selectedNodeId", _selectedNodeId);
		}
	}
}
