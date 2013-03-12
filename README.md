# Orgchart: a tree browser offers a convenient way to display data

## Introduction

Orgchart is a tree browser (or the so called SpaceTree) that offers a convenient way to display data, inspired by [JavaScript InfoVis Toolkit](http://philogb.github.com/jit/demos.html). The Orgchart consists of nodes and paths while path represents the links connecting the nodes. You can expand or collapse individual nodes in the tree to show or hide its children. You can also change the attributes to affect how the tree is displayed. For example, you can use *level* to adjust the number of levels that are shown in the tree.

## Example Scenario

Suppose we want to make an orgchart to display a company's staffs' information and their relations.

### SpaceTree Layout

To show the SpaceTree layout visualization, we need an orgchart component. The template tag refers the staff's name as the node label. We have also added a button for adding or removing a node.

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


Now, we can put the staffs' information into a Java bean (or the data of node) which is called *UserDataBean*, then we use the *UserDataBean* as the data of *SpaceTreeNode* and create their relations in *SpaceTreeNode*.

### Insert staff information into Java Bean

*UserDataBean* represents the data of *SpaceTreeNode* and *SpaceTreeNode* makes up the *SpaceTree*. You can use template tag in zul or customize the item renderer in a composer to generate the value which shows on the node label. By default the value will be *object#toString()*.

**JavaBean**
  
    // customize your java bean
    public class UserDataBean {

		private String name;
		private int age;
		
		// constructor, getter and setter
	
	}
    
### Initialize Spacetree

Create some SpaceTree nodes and combine all nodes together. The *ItemRenderer* is used to customize the value that shows on the node label. You can customize the item renderer if you don't want to use template tag in zul or override *object#toString()*.

**Composer**
  	
	public class SpaceTreeComposer extends SelectorComposer<Window> {
	
		@Wire("#myComp")
		private Orgchart myComp;
	
		public void doAfterCompose(Window comp) throws Exception {
			super.doAfterCompose(comp);
	
			// set true in last argument, that means is root
			SpaceTreeNode<UserDataBean> root = new SpaceTreeNode(null, null, true);
			SpaceTreeNode<UserDataBean> spacetreeRoot = new SpaceTreeNode(new UserDataBean("Peter", 23), null);
			root.add(spacetreeRoot);
			SpaceTreeNode<UserDataBean> first = new SpaceTreeNode(new UserDataBean("Jason", 1), null);
			SpaceTreeNode<UserDataBean> second = new SpaceTreeNode(new UserDataBean("Partick", 23), null);
			spacetreeRoot.add(first);
			spacetreeRoot.add(second);
			SpaceTreeNode jack = new SpaceTreeNode(new UserDataBean("Jack", 11),
					null);
			SpaceTreeNode mary = new SpaceTreeNode(new UserDataBean("Mary", 12),
					null);
			first.add(jack);
			first.add(mary);
			first.add(new SpaceTreeNode(new UserDataBean("Jean", 13), null));
			second.add(new SpaceTreeNode(new UserDataBean("Sam", 21), null));
			second.add(new SpaceTreeNode(new UserDataBean("Tom", 22), null));
			second.add(new SpaceTreeNode(new UserDataBean("Tim", 23), null));
			
			
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
