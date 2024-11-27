package fem.model.output.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.linalg.Vector;
import fem.interFace.INode;
import fem.model.FemModel;
import fem.model.output.Recorder;

public class NodeRecorder implements Recorder, Serializable {

	private List<NodeValue> m_list;
	
	public NodeRecorder(){
		m_list = new ArrayList<NodeValue>();
	}
	
	public Iterator<NodeValue> iterate(){
		return m_list.iterator();
	}
	
	public void record(FemModel model) {
		Iterator<INode> nodes = model.iterator(INode.class);
		while(nodes.hasNext()){
			INode node = nodes.next();
			String name = node.getName();
			Vector crds = node.getCoordinates();
			m_list.add(new NodeValue(name,crds));
			crds.release();
		}
	}
	
	public String toString() {
		final String LB = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder("Node"+LB);
		Iterator iter = iterate();
		while(iter.hasNext())
			sb.append(iter.next()+LB);
		return sb.toString();
	}
	
}
