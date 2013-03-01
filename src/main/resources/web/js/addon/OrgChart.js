jQuery.noConflict()

var labelType, useGradients, nativeTextSupport, animate;

(function() {
	var ua = navigator.userAgent, iStuff = ua.match(/iPhone/i)
			|| ua.match(/iPad/i), typeOfCanvas = typeof HTMLCanvasElement, nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'), textSupport = nativeCanvasSupport
			&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	// I'm setting this based on the fact that ExCanvas provides text support
	// for IE
	// and that as of today iPhone/iPad current text support is lame
	labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native'
			: 'HTML';
	nativeTextSupport = labelType == 'Native';
	useGradients = nativeCanvasSupport;
	animate = !(iStuff || !nativeCanvasSupport);
})();

addon.OrgChart = zk.$extends(zul.Widget, {
	_level : 2,
	_orient : 'left',
	_align : 'center',
	_nodetype : 'rectangle',
	_duration : 700,
	_oriJson : '',
	_json : '{"id": 1, "name": 1}',
	_st : '',
	_selectedNode : '{}',
	_cmd : '',
	_removing : false,
	_adding : false,
	_orienting : false,
	_aligning : false,
	_addNodeJson : '{"id": 2, "name": 2}',

	$define : {
		selectedNode : null,
		addNodeJson : null,
		level : _render = function() {
			if (this.desktop) {
				this.rerender();
			}
		},
		nodetype : _render,
		duration : _render,
		orient : function() {
			if (this.desktop) {
				var component = this;
				component._orienting = true;
				component._st.switchPosition(this._orient, "animate", {
					onComplete : function() {
						component._orienting = false;
					}
				});
			}
		},
		align : function() {
			if (this.desktop) {
				var component = this;
				component._aligning = true;
				component._st.switchAlignment(this._align, "replot", {
					onComplete : function() {
						component._aligning = false;
					}
				});
			}
		},
		json : function(val) {
			this._oriJson = this._json;
			this._json = val;
			if (this.desktop) {
			}
		},
	},

	getCmd : function() {
		return this._cmd;
	},

	// it will not setCmd("add") at second time if uses coding sugar
	setCmd : function(val) {
		var component = this;
		this._cmd = val;
		if (this.desktop) { // update the UI here.
			if (this._cmd === 'remove') {
				this.remove_(jq.evalJSON(this._selectedNode));
			} else if (this._cmd === 'add') {
				this.add_(this._addNodeJson, jq.evalJSON(this._selectedNode));
			} else if (this._cmd === 'refresh') {
				component._st.loadJSON(jq.evalJSON(this._json));
				var selJSON = jq.evalJSON(this._selectedNode);
				component._st.select(selJSON["id"], { Move : { enable : false }});
				component._st.refresh();
			}
		}
	},

	remove_ : function(node) {
		var component = this;
		var nodeid = node["id"];
		var oriJson = jq.evalJSON(component._oriJson);
		if (!component._removing) {
			component._removing = true;
			var parent = $jit.json.getParent(oriJson, nodeid);
			component._st.removeSubtree(nodeid, true, 'animate', {
				hideLabels : false,
				onComplete : function() {
					var parentNode = component.getSubTree_(parent["id"]);
					var parentNodeStr = JSON.stringify(parentNode);
					component._removing = false;
					component._selectedNode = "{}";
					component._json = JSON.stringify(component._st
							.toJSON("tree"));
				}
			});
		}
	},

	add_ : function(nodeJson, parent) {
		var component = this;
		var parentid = parent["id"];
		if (!component._adding) {
			component._adding = true;
			component._st.addSubtree({
				id : parentid,
				children : [ jq.evalJSON(nodeJson) ]
			}, 'animate', {
				hideLabels : false,
				onComplete : function() {
					component._adding = false;
					component._json = JSON.stringify(component._st
							.toJSON("tree"));
				}
			});
		}
	},

	getSubTree_ : function(nodeid) {
		var component = this;
		return $jit.json.getSubtree(jq.evalJSON(component._json), nodeid);
	},

	bind_ : function() {

		this.$supers(addon.OrgChart, 'bind_', arguments);

		this._st = new $jit.ST(this.initSTOpts_());

		var treeJson = jq.evalJSON(this._json);
		// load json data
		this._st.loadJSON(treeJson);
		// compute node positions and layout
		this._st.compute();
		// emulate a click on the node.
		if (this._selectedNode && this._selectedNode !== "{}") {
			var selJSON = jq.evalJSON(this._selectedNode);
			this._st.onClick(selJSON["id"]);
		} else {
			this._st.onClick(this._st.root);
		}

	},

	unbind_ : function() {
		this._st = null;

		this.$supers(addon.OrgChart, 'unbind_', arguments);
	},

	initSTOpts_ : function() {
		var component = this;
		var opts = {
			injectInto : component.uuid + '-infovis',
			transition : $jit.Trans.Quart.easeInOut,
			levelDistance : 50,
			duration : component._duration,
			orientation : component._orient,
			align : component._align,
			siblingOffset : 15,
			subtreeOffset : 15,
			Node : {
				'height' : 20,
				'width' : 60,
				'color' : '#aaa',
				'dim' : 40,
				'overridable' : true,
				'type' : component._nodetype,
				CanvasStyles : {
					shadowColor : '#ccc',
					shadowBlur : 10
				}
			},
			Navigation : {
				enable : true,
				panning : true
			},
			Edge : {
				type : 'line',
				overridable : true
			},
			Events : {
				enable : true,
				onClick : function(node, eventInfo, e) {

				}
			},
			onAfterPlotLine : function(adj) {

			},
			onAfterPlotNode : function(node) {

			},
			onBeforePlotNode : function(node) {
				/**
				 * add some color to the nodes in the path between the root node
				 * and the selected node.
				 */
				if (node.selected) {
					node.data.$color = "#ff7";
				} else {
					delete node.data.$color;
					/**
					 * if the node belongs to the last plotted level
					 */
					if (!node.anySubnode("exist")) {
						// count children number
						var count = 0;
						node.eachSubnode(function(n) {
							count++;
						});
						/**
						 * assign a node color based on how many children it has
						 */
						node.data.$color = [ '#aaa', '#baa', '#caa', '#daa',
								'#eaa', '#faa' ][count];
					}
				}
			},
			onCreateLabel : function(label, node) {
				label.id = node.id;
				label.innerHTML = node.name;
				label.onclick = function() {
					component._st.onClick(node.id, {
						onComplete : function() {
							if (node) {
								var selNode = component.getSubTree_(node.id);
								component.fire('onSelect', {
									selectedNode : selNode
								});
							}
						}
					});
				};
				// set label styles
				var style = label.style;
				style.width = 60 + 'px';
				style.height = 17 + 'px';
				style.cursor = 'pointer';
				style.color = '#333';
				style.fontSize = '0.8em';
				style.textAlign = 'center';
				style.paddingTop = '3px';
			},
			onBeforePlotLine : function(adj) {
				if (adj.nodeFrom.selected && adj.nodeTo.selected) {
					adj.data.$color = "#eed";
					adj.data.$lineWidth = 3;
				} else {
					delete adj.data.$color;
					delete adj.data.$lineWidth;
				}
			}
		};

		if (this._level <= -1) {
			opts['constrained'] = true;
		} else {
			opts['levelsToShow'] = this._level;
		}

		return opts;
	},

	getZclass : function() {
		return this._zclass != null ? this._zclass : "z-orgchart";
	}
});