package fem.model.output.nodalFieldState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.linalg.Vector;
import fem.interFace.INode;
import fem.model.FemModel;
import fem.model.output.Recorder;

public class NodalFieldStateRecorder implements Recorder, Serializable {

	private List<NodalFieldStateValue> m_list;
	
	public NodalFieldStateRecorder(){
		m_list = new ArrayList<NodalFieldStateValue>();
	}
	
	public Iterator<NodalFieldStateValue> iterate(){
		return m_list.iterator();
	}
	
	public void record(FemModel model) {
		Iterator<INode> nodes = model.iterator(INode.class);
		while(nodes.hasNext()){
			INode node = nodes.next();
			String name = node.getName();
			Vector crds = node.getCoordinates();
			double fieldstate = node.getPrimal()[0];
			m_list.add(new NodalFieldStateValue(name,crds,fieldstate));
			crds.release();
		}
	}
	
	public String toString() {
		final String LB = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder("Nodal Field States"+LB);
		Iterator iter = iterate();
		while(iter.hasNext())
			sb.append(iter.next()+LB);
		return sb.toString();
	}
	
}
