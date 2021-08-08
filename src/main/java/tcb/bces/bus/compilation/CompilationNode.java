package tcb.bces.bus.compilation;

import java.util.ArrayList;
import java.util.List;

public class CompilationNode {
	private String data;
	private List<CompilationNode> children = new ArrayList<CompilationNode>();
	
	/**
	 * Creates a new node
	 * @param data
	 */
	public CompilationNode(String data) {
		this.data = data;
	}
	
	/**
	 * Adds a child node to this node
	 * @param node
	 * @return
	 */
	public CompilationNode addChild(CompilationNode node) {
		this.children.add(node);
		return this;
	}
	
	/**
	 * Returns all child nodes of this node
	 * @return
	 */
	public List<CompilationNode> getChildren() {
		return this.children;
	}
	
	/**
	 * Returns the data of this node
	 * @return
	 */
	public String getData() {
		return this.data;
	}
	
	/**
	 * Clears this node from child nodes
	 */
	public void clear() {
		this.children.clear();
	}
	
	private String printStr = "";
	@Override
	public String toString() {
		this.printStr = "";
		this.getStrRec(this, "", true);
		return this.printStr;
	}
	private void getStrRec(CompilationNode prntNode, String prefix, boolean isTail) {
		prntNode.printStr = prntNode.printStr + (prefix + (isTail ? "'-- " : "|-- ") + this.data) + "\n";
        for (int i = 0; i < this.children.size() - 1; i++) {
        	this.children.get(i).getStrRec(prntNode, prefix + (isTail ? "    " : "|   "), false);
        }
        if (this.children.size() > 0) {
        	this.children.get(this.children.size() - 1).getStrRec(prntNode, prefix + (isTail ?"    " : "|   "), true);
        }
    }
}
