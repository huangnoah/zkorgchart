# Orgchart: a tree browser offers a convenient way to display data

## Introduct

  Orgchart is a tree browser (or call **SpaceTree**) that offers a convenient way to display data. It consists of nodes and paths, and the paths represent the links connecting these nodes. You can expand or collapse individual nodes in the tree to show or hide its children. And you can change the attributes to affect how the tree is displayed. For example, use the **level** to adjust the number of levels that are shown in the tree. 

## Example

Suppose we want to make a orgchart to display the staffs information and the relations connecting those staffs.

### SpaceTree Layout

To show the SpaceTree layout visualization we need a orgchart component. 
The template tag regards the staff's name as the node label. 
We have also added button for adding, removing a new node feature.

**ZUL**
  
	<zk>
		<window id="mainWin" apply="test.ctrl.SpaceTreeComposer">
			<orgchart id="myComp" level="3" nodetype="circle"
				duration="250" orient="right" align="right">
				<template name="model">
					<label value="${each.getName()}"></label>
				</template>
			</orgchart>
			<button id="add" label="add" />
			<button id="remove" label="remove" />
		</window>
	</zk>


Now, we can put the staffs information into Java bean (or the data of node) which is called **UserDataBean**, then we use the **UserDataBean** as the data of **SpaceTreeNode** and create the relations in **SpaceTreeNode**.

### Put the staffs information into Java Bean

The **UserDataBean** represents the data of **SpaceTreeNode**.
The result of toString() will be the node label if you don't use template tag in zul and customize the item renderer in composer.  

**JavaBean**
  
    // customize your java bean
    public class UserDataBean {

		private String name;
		private int age;
	
		@Override
		public String toString() {
			return name + " (" + age + ")";
		}
		
		// constructor, getter and setter
	
	}

**SpaceTreeNode**

	public class SpaceTreeNode<E> extends DefaultTreeNode<E> {

		private static int genId = 0;
		private int id;
		public SpaceTreeNode(E data, Collection<SpaceTreeNode<E>> children) {
			super(data, children);
			id = genId++;
		}
	
		public int getId() {
			return id;
		}
	
		public boolean hasChildren() {
			return getChildren() != null && getChildren().size() != 0;
		}
	
	}
    
### Create the relations and initialize SpaceTree

Create some SpaceTree nodes and combine all nodes together.
The ItemRenderer is used to customize the value which shows on the node label.
The component will call the default renderer which regards **org.zkoss.lang.Objects.toString(yourBean)** as the node label if you don't customize the renderer.  

**Composer**
  
    public class SpaceTreeComposer extends SelectorComposer<Window> {

		@Wire("#myComp")
		private OrgChart myComp;
	
		public void doAfterCompose(Window comp) throws Exception {
			super.doAfterCompose(comp);
	
			SpaceTreeNode jack = new SpaceTreeNode(new UserDataBean("Jack", 11),
					null);
			SpaceTreeNode mary = new SpaceTreeNode(new UserDataBean("Mary", 12),
					null);
	
			List<SpaceTreeNode<UserDataBean>> firstChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
			firstChildren.add(jack);
			firstChildren.add(mary);
			firstChildren
					.add(new SpaceTreeNode(new UserDataBean("Jean", 13), null));
			SpaceTreeNode<UserDataBean> first = new SpaceTreeNode(new UserDataBean(
					"Jason", 1), firstChildren);
	
			List<SpaceTreeNode<UserDataBean>> secondChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
			secondChildren
					.add(new SpaceTreeNode(new UserDataBean("Sam", 21), null));
			secondChildren
					.add(new SpaceTreeNode(new UserDataBean("Tom", 22), null));
			secondChildren
					.add(new SpaceTreeNode(new UserDataBean("Tim", 23), null));
			SpaceTreeNode<UserDataBean> second = new SpaceTreeNode(
					new UserDataBean("Partick", 23), secondChildren);
	
			List<SpaceTreeNode<UserDataBean>> spacetreeRootChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
			spacetreeRootChildren.add(first);
			spacetreeRootChildren.add(second);
			SpaceTreeNode<UserDataBean> spacetreeRoot = new SpaceTreeNode(
					new UserDataBean("Peter", 0), spacetreeRootChildren);
	
			List<SpaceTreeNode<UserDataBean>> rootChild = new ArrayList<SpaceTreeNode<UserDataBean>>();
			rootChild.add(spacetreeRoot);
			SpaceTreeNode<UserDataBean> root = new SpaceTreeNode(null, rootChild);
	
			// customize your renderer
			// myComp.setItemRenderer(new ItemRenderer() {
			// @Override
			// public String render(Component owner, Object data, int index) {
			// return ((UserDataBean) data).getName();
			// }
			// });
	
			SpaceTreeModel model = new SpaceTreeModel(root);
			model.addToSelection(mary);
			
			myComp.setModel(model);
		}
	
		@Listen("onClick= #add")
		public void addNode() {
			SpaceTreeNode<UserDataBean> childNode = new SpaceTreeNode(
					new UserDataBean("Allen", 27), null);
			SpaceTreeNode seldNode = myComp.getSelectedNode();
			if (seldNode != null) {
				seldNode.add(childNode);
			}
		}
	
		@Listen("onClick= #remove")
		public void removeNode() {
			SpaceTreeNode seldNode = myComp.getSelectedNode();
			if (seldNode != null) {
				seldNode.removeFromParent();
			}
		}
	
		@Listen("onSelect= #myComp")
		public void editNode() {
			SpaceTreeNode seld = myComp.getSelectedNode();
			UserDataBean data = (UserDataBean) seld.getData();
			data.setName("Augustin");
			seld.setData(data);
		}
	
	}

You can download the complete source code for this example from its github repository [here](https://github.com/huangnoah/zkorgchart) 

Reference: [ZK Small Talk - Integrate 3rd Party Javascript Libraries In ZK](http://books.zkoss.org/wiki/Small_Talks/2012/November/Integrate_3rd_Party_Javascript_Libraries_In_ZK)