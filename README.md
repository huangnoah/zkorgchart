# Orgchart: a tree browser that offers a convenient way to display data

## Introduct

  Orgchart is a tree browser (or call **SpaceTree**) that offers a convenient way to display data. It consists of nodes and paths, and the paths represent the links connecting these nodes. You can expand or collapse individual nodes in the tree to show or hide its children. And you can change the attributes to affect how the tree is displayed. For example, use the **level** to adjust the number of levels that are shown in the tree. 

## Example

Suppose we want to make a orgchart to display the staffs information and the relations connecting those staffs.

### SpaceTree Layout

To show the SpaceTree layout visualization we need a orgchart component. I have also added button for adding, removing a new node feature.

**ZUL**
  
	<zk>
		<window id="mainWin" apply="test.ctrl.SpaceTreeComposer">
			<orgchart id="myComp" level="3" nodetype="circle"
				duration="250" orient="right" align="right">
			</orgchart>
			<button id="add" label="add" />
			<button id="remove" label="remove" />
		</window>
	</zk>


Now, we can put the staffs information into Java bean (or the data of node) which is called **UserDataBean**, then we use the **UserDataBean** as the data of **SpaceTreeNode** and create the relations in **SpaceTreeNode**.

### Put the staffs information inot Java Bean (or the data of node)

The **UserDataBean** represents the data of **SpaceTreeNode**.

**JavaBean**
  
    // customize your java bean
    public class UserDataBean {

		private String id;
		private String name;
		private int age;

        // constructor, getter and setter

    }

Implemented **JSONAware** interface for the **SpaceTreeNode** allows us to easily transform tree node structure into JSON string that can be sent to client for evaluation and further processing.
Calling **toJSONString()** on the root node can convert the whole structure into JSON string.

**SpaceTreeNode**

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
	
OrgChart.getRealRenderer() will check if the customize renderer exists, if not it uses the default renderer.

**Default Node Renderer**

	// default node renderer in OrgChart
    private static final SpaceTreeNodeRenderer _defRend = new SpaceTreeNodeRenderer() {
		@Override
		public String render(SpaceTreeNode node) {
			if (node.getData() == null) {
				return "{}";
			} else {
				return node.toJSONString();
			}
		}

		private String getDataValue(SpaceTreeNode node, String fieldName) {
			Object data = node.getData();
			String value = null;
			try {
				Field field = data.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				value = field.get(data).toString();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return value;
		}

		@Override
		public String getJSONId(SpaceTreeNode node) {
			return getDataValue(node, "id");
		}

		@Override
		public String getJSONName(SpaceTreeNode node) {
			return getDataValue(node, "name");
		}

		@Override
		public JSONObject getJSONData(SpaceTreeNode node) {
			Object data = node.getData();
			JSONObject props = new JSONObject();

			List<Field> fields = Arrays.asList(data.getClass()
					.getDeclaredFields());
			String fieldname = null;

			for (Field field : fields) {
				field.setAccessible(true);
				fieldname = field.getName();

				if ("id".equals(fieldname) || "name".equals(fieldname)) {
					continue;
				} else {
					try {
						props.put(fieldname, field.get(data));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

			return props;
		}

		@Override
		public String toJSONString(SpaceTreeNode node) {
			JSONObject json = new JSONObject();
			json.put("id", node.getJSONId());
			json.put("name", node.getJSONName());
			json.put("data", node.getJSONData());
			json.put("children", node.getChildren());
			return json.toJSONString();
		}

	};
    
### Define the relation and Initialize SpaceTree

Create some SpaceTree nodes and combine all nodes together.

**Composer**
  
    public class SpaceTreeComposer<E extends SpaceTreeData> extends SelectorComposer<Window> {

		@Wire("#myComp")
		private OrgChart<E> myComp;
		
		public static int conut = 50;
		
		public void doAfterCompose(Window comp) throws Exception {
			super.doAfterCompose(comp);
			
			List<SpaceTreeNode<E>> firstChildren = new ArrayList<SpaceTreeNode<E>>();
			firstChildren.add(new SpaceTreeNode(new UserDataBean("11", "Jack", 11), null));
			firstChildren.add(new SpaceTreeNode(new UserDataBean("12", "Mary", 12), null));
			firstChildren.add(new SpaceTreeNode(new UserDataBean("13", "Jean", 13), null));
			SpaceTreeNode<E> first = new SpaceTreeNode(new UserDataBean("1", "Jason", 1), firstChildren);
			
			List<SpaceTreeNode<E>> secondChildren = new ArrayList<SpaceTreeNode<E>>();
			secondChildren.add(new SpaceTreeNode(new UserDataBean("21", "Sam", 21), null));
			secondChildren.add(new SpaceTreeNode(new UserDataBean("22", "Tom", 22), null));
			secondChildren.add(new SpaceTreeNode(new UserDataBean("23", "Tim", 23), null));
			SpaceTreeNode<E> second = new SpaceTreeNode(new UserDataBean("2", "Partick", 23), secondChildren);
			
			List<SpaceTreeNode<E>> spacetreeRootChildren = new ArrayList<SpaceTreeNode<E>>();
			spacetreeRootChildren.add(first);
			spacetreeRootChildren.add(second);
			SpaceTreeNode<E> spacetreeRoot = new SpaceTreeNode(new UserDataBean("0", "Peter", 0), sapcetreeRootChildren);
			
			List<SpaceTreeNode<E>> rootChild = new ArrayList<SpaceTreeNode<E>>();
			rootChild.add(spacetreeRoot);
			SpaceTreeNode<E> root = new SpaceTreeNode(null, rootChild);
			
			SpaceTreeModel<E> model = new SpaceTreeModel<E>(root);
			myComp.setModel(model);
		}
		
		@Listen("onClick= #add")
		public void addNode() {
		    SpaceTreeNode<E> childNode = new SpaceTreeNode(new UserDataBean(conut++ +"", "Allen", conut++), null);
		    SpaceTreeNode<E> seldNode = myComp.getSelectedNode();
		    seldNode.add(childNode);
		}
		
		@Listen("onClick= #remove")
		public void removeNode() {
		    SpaceTreeNode<E> seldNode = myComp.getSelectedNode();
		    seldNode.getParent().remove(seldNode);
		}
		
		@Listen("onSelect= #myComp")
		public void editNode() {
			SpaceTreeNode seld = myComp.getSelectedNode();
			seld.setData(new UserDataBean(seld.getJSONId(), "Augustin", conut++));
		}
	
	}

You can download the complete source code for this example from its github repository [here](https://github.com/huangnoah/zkorgchart) 

Reference: [ZK Small Talk - Integrate 3rd Party Javascript Libraries In ZK](http://books.zkoss.org/wiki/Small_Talks/2012/November/Integrate_3rd_Party_Javascript_Libraries_In_ZK)