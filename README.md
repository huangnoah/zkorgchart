# Orgchart: a tree browser that offers a convenient way to display data

## Introduct

  Orgchart is a tree browser (or call **SpaceTree**) that offers a convenient way to display data. It consist of nodes and paths, and the paths represent the links connecting these nodes. You can expand or collapse individual nodes in the tree to show or hide its children. And you can change the attributes to affect how the tree is displayed. For Example, use the **level** to adjust the number of levels that are shown in the tree. 

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


Now, we can put the staffs information into Java bean (or the data of node) which is called **UserDataBean** and implements **SpaceTreeData** interface, then we can put the **UserDataBean** into **SpaceTreeNode** and define the relations in **SpaceTreeNode**.

### Put the staffs information inot Java Bean (or the data of node)

The **UserDataBean** represents the data of **SpaceTree** node.

**JavaBean**
  
    // customize your java bean
    public class UserDataBean implements SpaceTreeData {

		private String id;
		private String name;
		private int age;

        // constructor, getter and setter

    }

Implemented **JSONAware** interface for the **SpaceTreeNode** allows us to easily transform tree node structure into JSON string that can be sent to client for evaluation and further processing.
Calling **toJSONString()** on the root node can convert the whole structure into JSON string.

**SpaceTreeNode**

	public class SpaceTreeNode<E extends SpaceTreeData> extends
		DefaultTreeNode<E> implements JSONAware {

		public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
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
    
### Define the relation and Initialize SpaceTree

Create some SpaceTree node and combine all nodes together.

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
			
			List<SpaceTreeNode<E>> sapcetreeRootChildren = new ArrayList<SpaceTreeNode<E>>();
			sapcetreeRootChildren.add(first);
			sapcetreeRootChildren.add(second);
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
			seld.setData(new UserDataBean(seld.getId(), "Augustin", conut++));
		}
	
	}

you can see the example in Github [https://github.com/huangnoah/zkorgchart](https://github.com/huangnoah/zkorgchart) 

  

    

  
