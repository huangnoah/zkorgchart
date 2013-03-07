# Orgchart: a tree browser offers a convenient way to display data

## Introduction

  The Orgchart is a tree browser (or call **SpaceTree**) that offers a convenient way to display data. (inspired by [JavaScript InfoVis Toolkit](http://philogb.github.com/jit/demos.html)) It consists of nodes and paths, and the paths represent the links connecting these nodes. You can expand or collapse individual nodes in the tree to show or hide its children. And you can change the attributes to affect how the tree is displayed. For example, use the **level** to adjust the number of levels that are shown in the tree. 

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
					<label value="${each.name}"></label>
				</template>
			</orgchart>
			<button id="add" label="add" />
			<button id="remove" label="remove" />
		</window>
	</zk>


Now, we can put the staffs information into Java bean (or the data of node) which is called **UserDataBean**, then we use the **UserDataBean** as the data of **SpaceTreeNode** and create the relations in **SpaceTreeNode**.

### Put the staffs information into Java Bean

The **UserDataBean** represents the data of **SpaceTreeNode** and the **SpaceTreeNode**s make up the **SpaceTree**.
You can use template tag in zul or customize the item renderer in composer to generate the value which shows on the node label, or by default the value will be object#toString().  

**JavaBean**
  
    // customize your java bean
    public class UserDataBean {

		private String name;
		private int age;
		
		// constructor, getter and setter
	
	}
    
### Initialize Spacetree

Create some SpaceTree nodes and combine all nodes together.
The ItemRenderer is used to customize the value which shows on the node label.
You can customize the item renderer if you don't want to use template tag in zul or override object#toString().  

**Composer**
  
    public class SpaceTreeComposer extends SelectorComposer<Window> {

		@Wire("#myComp")
		private Orgchart myComp;
	
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
	
			// customize your renderer, the data means your bean, and index means the node id
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

<pre>
   Copyright 2013 Potix Corporation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
</pre>
