package test.ctrl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.addon.OrgChart;
import org.zkoss.addon.SpaceTreeModel;
import org.zkoss.addon.SpaceTreeNode;
import org.zkoss.addon.SpaceTreeRenderer;
import org.zkoss.model.UserDataBean;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

public class SpaceTreeComposer extends SelectorComposer<Window> {

	@Wire("#myComp")
	private OrgChart myComp;
	
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		List<SpaceTreeNode<UserDataBean>> firstChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Jack", 11), null));
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Mary", 12), null));
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Jean", 13), null));
		SpaceTreeNode<UserDataBean> first = new SpaceTreeNode(new UserDataBean("Jason", 1), firstChildren);
		
		List<SpaceTreeNode<UserDataBean>> secondChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Sam", 21), null));
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Tom", 22), null));
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Tim", 23), null));
		SpaceTreeNode<UserDataBean> second = new SpaceTreeNode(new UserDataBean("Partick", 23), secondChildren);
		
		List<SpaceTreeNode<UserDataBean>> spacetreeRootChildren = new ArrayList<SpaceTreeNode<UserDataBean>>();
		spacetreeRootChildren.add(first);
		spacetreeRootChildren.add(second);
		SpaceTreeNode<UserDataBean> spacetreeRoot = new SpaceTreeNode(new UserDataBean("Peter", 0), spacetreeRootChildren);
		
		List<SpaceTreeNode<UserDataBean>> rootChild = new ArrayList<SpaceTreeNode<UserDataBean>>();
		rootChild.add(spacetreeRoot);
		SpaceTreeNode<UserDataBean> root = new SpaceTreeNode(null, rootChild);

		// customize your renderer
		myComp.setSpaceTreeRenderer(new SpaceTreeRenderer<UserDataBean>() {
			@Override
			public String render(UserDataBean data) {
				return data.getName();
			}
		});
		
		SpaceTreeModel model = new SpaceTreeModel(root);
		myComp.setModel(model);
	}
	
	@Listen("onClick= #add")
	public void addNode() {
	    SpaceTreeNode<UserDataBean> childNode = new SpaceTreeNode(new UserDataBean("Allen", 27), null);
	    SpaceTreeNode seldNode = myComp.getSelectedNode();
	    seldNode.add(childNode);
	}
	
	@Listen("onClick= #remove")
	public void removeNode() {
	    SpaceTreeNode seldNode = myComp.getSelectedNode();
	    seldNode.getParent().remove(seldNode);
	}
	
	@Listen("onSelect= #myComp")
	public void editNode() {
		SpaceTreeNode seld = myComp.getSelectedNode();
		UserDataBean data =  (UserDataBean) seld.getData();
		data.setName("Augustin");
		seld.setData(data);
	}

}