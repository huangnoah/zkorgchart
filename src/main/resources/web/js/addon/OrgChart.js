/**
 * 
 * Base naming rule: The stuff start with "_" means private , end with "_" means
 * protect , others mean public.
 * 
 * All the member field should be private.
 * 
 * Life cycle: (It's very important to know when we bind the event) A widget
 * will do this by order : 1. $init 2. set attributes (setters) 3. rendering
 * mold (@see mold/comp.js ) 4. call bind_ to bind the event to dom .
 * 
 * this.deskop will be assigned after super bind_ is called, so we use it to
 * determine whether we need to update view manually in setter or not. If
 * this.desktop exist , means it's after mold rendering.
 * 
 */

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
	_addNodeJson : '{"id": 2, "name": 2}',
	/**
	 * Don't use array/object as a member field, it's a restriction for ZK
	 * object, it will work like a static , share with all the same Widget class
	 * instance.
	 * 
	 * if you really need this , assign it in bind_ method to prevent any
	 * trouble.
	 * 
	 * TODO:check array or object , must be one of them ...I forgot. -_- by Tony
	 */

	$define : {
		/**
		 * The member in $define means that it has its own setter/getter. (It's
		 * a coding sugar.)
		 * 
		 * If you don't get this , you could see the comment below for another
		 * way to do this.
		 * 
		 * It's more clear.
		 * 
		 */
		selectedNode : _zkf = function() {
			if (this.desktop) {
			}
		},
		addNodeJson : _zkf,
		adding : _zkf,
		removing : _zkf,
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
				component._st.switchPosition(this._orient, "animate");
			}
		},
		align : function() {
			if (this.desktop) {
				var component = this;
				component._st.switchAlignment(this._align, "replot");
			}
		},

	},
	/**
	 * If you don't like the way in $define , you could do the setter/getter by
	 * yourself here.
	 * 
	 * Like the example below, they are the same as we mentioned in $define
	 * section.
	 */
	/*
	 * getText:function(){ return this._text; }, setText:function(val){
	 * this._text = val; if(this.desktop){ //update the UI here. } },
	 */
	getJson : function() {
		return this._json;
	},

	setJson : function(val) {
		this._oriJson = this._json;
		this._json = val;
		if (this.desktop) {
		}
	},

	getCmd : function() {
		return this._cmd;
	},

	setCmd : function(val) {
		var component = this;
		if (val === 'add' || val === 'remove' || val === 'refresh')
			this._cmd = val;
		else {
			this._cmd = '';
		}
		if (this.desktop) { // update the UI here.
			if (this._cmd === 'remove') {
				this.remove(jq.evalJSON(this._selectedNode));
			} else if (this._cmd === 'add') {
				this.add(this._addNodeJson, jq.evalJSON(this._selectedNode));
			} else if (this._cmd === 'refresh') {
				component._st.loadJSON(jq.evalJSON(this._json));
				component._st.refresh();
			}
		}
	},

	remove : function(node) {
		var component = this;
		var nodeid = node["id"];
		var oriJson = jq.evalJSON(component._oriJson);
		var isRoot = (nodeid === oriJson["id"]);
		if (!isRoot) {
			if (!component._removing) {
				component._removing = true;
				var parent = $jit.json.getParent(oriJson, nodeid);
				component._st.removeSubtree(nodeid, true, 'animate', {
					hideLabels : false,
					onComplete : function() {
						var parentNode = component.getSubTree(parent["id"])
						var parentNodeStr = JSON.stringify(parentNode)
						component._removing = false;
						component._selectedNode = parentNodeStr;
						component.fire('onUser', {
							selectedNode : parentNodeStr
						});
						
					}
				});
			}
		}
	},

	add : function(nodeJson, parent) {
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
					var oriJson = jq.evalJSON(component._oriJson)
					if (parentid === oriJson["id"]) {
						component._st.select(parentid);
						component._st.refresh();
					}
					
				}
			});
		}
	},
	
	getSubTree : function(nodeid) {
		var component = this;
		return $jit.json.getSubtree(jq.evalJSON(component._json), nodeid);
	},

	bind_ : function() {

		/**
		 * For widget lifecycle , the super bind_ should be called as FIRST
		 * STATEMENT in the function. DONT'T forget to call supers in bind_ , or
		 * you will get error.
		 */
		this.$supers(addon.OrgChart, 'bind_', arguments);

		this._st = new $jit.ST(this.initSTOpts_());

		var treeJson = jq.evalJSON(this._json);
		// load json data
		this._st.loadJSON(treeJson);
		// compute node positions and layout
		this._st.compute();
		// emulate a click on the root node.
		this._st.onClick(this._st.root);

		// A example for domListen_ , REMEMBER to do domUnlisten
		// in unbind_.
		// this.domListen_(this.$n("cave"), "onClick",
		// "_doItemsClick");

	},
	/*
	 * A example for domListen_ listener.
	 */
	/*
	 * _doItemsClick: function (evt) { alert("item click event fired"); },
	 */
	unbind_ : function() {

		// A example for domUnlisten_ , should be paired with
		// bind_
		// this.domUnlisten_(this.$n("cave"), "onClick",
		// "_doItemsClick");
		this._st = null;

		/*
		 * For widget lifecycle , the super unbind_ should be called as LAST
		 * STATEMENT in the function.
		 */
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
				// add some color to the nodes in the path
				// between the
				// root node and the selected node.
				if (node.selected) {
					node.data.$color = "#ff7";
				} else {
					delete node.data.$color;
					// if the node belongs to the last plotted
					// level
					if (!node.anySubnode("exist")) {
						// count children number
						var count = 0;
						node.eachSubnode(function(n) {
							count++;
						});
						// assign a node color based on
						// how many children it has
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
								var selNode = component.getSubTree(node.id);
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
	/*
	 * widget event, more detail please refer to
	 * http://books.zkoss.org/wiki/ZK%20Client-side%20Reference/Notifications
	 */

	getZclass : function() {
		return this._zclass != null ? this._zclass : "z-orgchart";
	}
});