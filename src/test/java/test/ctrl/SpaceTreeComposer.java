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

public class SpaceTreeComposer extends SelectorComposer<Window> {

	@Wire("#myComp")
	private OrgChart<?> myComp;
	
	public static int conut = 50;
	
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
		
		List<SpaceTreeNode<?>> sapcetreeRootChildren = new ArrayList<SpaceTreeNode<?>>();
		sapcetreeRootChildren.add(first);
		sapcetreeRootChildren.add(second);
		SpaceTreeNode<?> spacetreeRoot = new SpaceTreeNode(new UserDataBean("0", "Peter", 0), sapcetreeRootChildren);
		List<SpaceTreeNode<?>> rootChild = new ArrayList<SpaceTreeNode<?>>();
		rootChild.add(spacetreeRoot);
		SpaceTreeNode<?> root = new SpaceTreeNode(null, rootChild);
		SpaceTreeModel model = new SpaceTreeModel(root);
		myComp.setModel(model);
	}
	
	@Listen("onClick= #add")
	public void addNode() {
	    SpaceTreeNode childNode = new SpaceTreeNode(new UserDataBean(conut++ +"", "Allen", conut++), null);
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
		seld.setData(new UserDataBean(seld.getId(), "Augustin", conut++));
	}

}