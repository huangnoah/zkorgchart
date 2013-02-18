# Orgchart

## Introduct

  Orgchart is a tree browser (or call **SpaceTree**), the tree offers a convenient way to display data, it consist of nodes and paths, the path represent the relation between node and node. You can expand or collapse individual nodes in the tree to show or hide its children. And you can use the attributes to affect how the tree is displayed.  For Example, use the **level** to adjust the number of levels that are shown in the tree. 

## Example

Suppose we want to describe the staff information and the relation between staff in company or organization by orgchart.

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


In order to show the information and relation, we need to map staff information to java bean which called **UserDataBean** and implements **SpaceTreeData** interface,
then we can define the relation in **SpaceTreeNode**

### Map information into your Java Bean

the **UserDataBean** represent the data of **SpaceTree** node.
SpaceTree layout accepts **JSON** data, 
the only thing you need to do is create the JSON Object contains other properties but **exclude** **id** and **name** in **getJsonData()** method if **UserDataBean** already implements **SpaceTreeData** interface,
so we can get correct JSON format for SpaceTree usage when **SpaceTreeNode** call **toJSONString()** method

the java bean code would be as follows.

**JavaBean**
  
    // customize your java bean
    public class UserDataBean implements SpaceTreeData<JSONObject> {

		private String id;
		private String name;
		private int age;

        // put other data but exclude id and name into JSONObject 
		public JSONObject getJsonData() {
			JSONObject json = new JSONObject();
			json.put("age", age);
			return json;
		}

        // constructor, getter and setter

    }
    
    
### Define the relation and Initialize SpaceTree
    
Implemented **SpaceTreeData** interface for the **UserDataBean** allows we to easily transform tree node structure into JSON string that can be sent to client for evaluation and further processing.
Below is how we create some simple **SpaceTreeNode** and convert the whole structure into JSON string by calling **toJSONString()** on the root node.

**Composer**
  
    public class SpaceTreeComposer extends SelectorComposer<Window> {
    
    	@Wire("#myComp")
		private OrgChart<?> myComp;
		public static int conut = 50;
		
    	// init tree
        public void doAfterCompose(Window comp) throws Exception {
			super.doAfterCompose(comp);
		
			List<SpaceTreeNode<?>> firstChildren = new ArrayList<SpaceTreeNode<?>>();
			firstChildren.add(new SpaceTreeNode(new UserDataBean("11", "Jack", 11), null));
			firstChildren.add(new SpaceTreeNode(new UserDataBean("12", "Mary", 12), null));
			firstChildren.add(new SpaceTreeNode(new UserDataBean("13", "Jean", 13), null));
			SpaceTreeNode<?> first = new SpaceTreeNode(new UserDataBean("1", "Jason", 1), firstChildren);
		
			List<SpaceTreeNode<?>> secondChildren = new ArrayList<SpaceTreeNode<?>>();
			secondChildren.add(new SpaceTreeNode(new UserDataBean("21", "Sam", 21), null));
			secondChildren.add(new SpaceTreeNode(new UserDataBean("22", "Tom", 22), null));
			secondChildren.add(new SpaceTreeNode(new UserDataBean("23", "Tim", 23), null));
			SpaceTreeNode<?> second = new SpaceTreeNode(new UserDataBean("2", "Partick", 23), secondChildren);
		
			List<SpaceTreeNode<?>> rootChildren = new ArrayList<SpaceTreeNode<?>>();
			rootChildren.add(first);
			rootChildren.add(second);
			SpaceTreeNode<?> root = new SpaceTreeNode(new UserDataBean("0", "Peter", 0), rootChildren);
		
			DefaultTreeModel model = new DefaultTreeModel(root);
			myComp.initModel(model);
		}
		
		// add node
		@Listen("onClick= #add")
		public void addNode() {
		    SpaceTreeNode node = new SpaceTreeNode(new UserDataBean(conut++ +"", "Allen", conut++), null);
		    myComp.add(node, myComp.getSelectedNodeId());
		}
		
		// remove node
		@Listen("onClick= #remove")
		public void removeNode() {
		    myComp.remove(myComp.getSelectedNodeId());
		}
		
		// edit node
		@Listen("onSelect= #myComp")
		public void editNode() {
			SpaceTreeNode seld = myComp.getSelectedNode();
			seld.setData(new UserDataBean(seld.getId(), "Augustin", conut++));
		}
    }

you can see the example in Github [https://github.com/huangnoah/zkorgchart](https://github.com/huangnoah/zkorgchart) 

  

    

  
