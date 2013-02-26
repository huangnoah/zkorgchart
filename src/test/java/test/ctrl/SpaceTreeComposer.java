package test.ctrl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.addon.OrgChart;
import org.zkoss.addon.SpaceTreeModel;
import org.zkoss.addon.SpaceTreeNode;
import org.zkoss.model.UserDataBean;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

public class SpaceTreeComposer<E> extends SelectorComposer<Window> {

	@Wire("#myComp")
	private OrgChart<E> myComp;
	
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		List<SpaceTreeNode<E>> firstChildren = new ArrayList<SpaceTreeNode<E>>();
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Jack", 11), null));
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Mary", 12), null));
		firstChildren.add(new SpaceTreeNode(new UserDataBean("Jean", 13), null));
		SpaceTreeNode<E> first = new SpaceTreeNode(new UserDataBean("Jason", 1), firstChildren);
		
		List<SpaceTreeNode<E>> secondChildren = new ArrayList<SpaceTreeNode<E>>();
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Sam", 21), null));
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Tom", 22), null));
		secondChildren.add(new SpaceTreeNode(new UserDataBean("Tim", 23), null));
		SpaceTreeNode<E> second = new SpaceTreeNode(new UserDataBean("Partick", 23), secondChildren);
		
		List<SpaceTreeNode<E>> spacetreeRootChildren = new ArrayList<SpaceTreeNode<E>>();
		spacetreeRootChildren.add(first);
		spacetreeRootChildren.add(second);
		SpaceTreeNode<E> spacetreeRoot = new SpaceTreeNode(new UserDataBean("Peter", 0), spacetreeRootChildren);
		
		List<SpaceTreeNode<E>> rootChild = new ArrayList<SpaceTreeNode<E>>();
		rootChild.add(spacetreeRoot);
		SpaceTreeNode<E> root = new SpaceTreeNode(null, rootChild);
		
		SpaceTreeModel<E> model = new SpaceTreeModel<E>(root);
		myComp.setModel(model);
	}
	
	@Listen("onClick= #add")
	public void addNode() {
	    SpaceTreeNode<E> childNode = new SpaceTreeNode(new UserDataBean("Allen", 27), null);
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
		UserDataBean data =  (UserDataBean) seld.getData();
		data.setName("alan");
		seld.setData(data);
	}

}