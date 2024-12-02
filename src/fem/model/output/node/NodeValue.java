package fem.model.output.node;

import java.io.Serializable;

import math.linalg.Vector;

public class NodeValue implements Serializable {

	private String m_nodeName;
	private Vector m_crds;
	
	public NodeValue(String elementName, Vector crds){
		this.m_nodeName = elementName;
		this.m_crds = crds.clone(true);
	}
	
	public String getName(){
		return m_nodeName;
	}
	
	public Vector getCrds(){
		return m_crds;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_nodeName+"\t");
		for(int i = 0; i < m_crds.size(); i++){			
			sb.append(m_crds.get(i)+"\t");
		}
		return sb.toString();
	}
		
}
