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
		SpaceTreeNode<E> spacetreeRoot = new SpaceTreeNode(new UserDataBean("0", "Peter", 0), spacetreeRootChildren);
		
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