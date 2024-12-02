package fem.model.output.nodeDisplacement;

import math.linalg.Vector;

public class NodeDisplacementPoint {

	private String m_elementName;
	private Vector m_crds;
	private Vector m_disp;	
	
	public NodeDisplacementPoint(String elementName, Vector crds, Vector disp){
		this.m_elementName = elementName;
		this.m_crds = crds.clone(true);
		this.m_disp = disp.clone(true);
	}
	
	public String getName(){
		return m_elementName;
	}
	
	public Vector getCrds(){
		return m_crds;
	}
	
	public Vector getDisplacements(){
		return m_disp;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_elementName+"\t");
		for(int i = 0; i < m_crds.size(); i++){			
			sb.append(m_crds.get(i)+"\t");
		}
		for(int i = 0; i < m_disp.size(); i++)
			sb.append(m_disp.get(i)+"\t");
		return sb.toString();
	}

}
