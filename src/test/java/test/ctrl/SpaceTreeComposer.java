package test.ctrl;

import org.zkoss.addon.Orgchart;
import org.zkoss.addon.SpaceTreeModel;
import org.zkoss.addon.SpaceTreeNode;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Window;

import test.model.UserDataBean;

public class SpaceTreeComposer extends SelectorComposer<Window> {

	@Wire("#myComp")
	private Orgchart myComp;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		SpaceTreeNode<UserDataBean> root = new SpaceTreeNode(null, null);
		SpaceTreeNode<UserDataBean> spacetreeRoot = new SpaceTreeNode(new UserDataBean("Partick", 23), null);
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