package fem.model.output.stresses;

import math.linalg.Vector;

public class StressPoint {

	private String m_elementName;
	private Vector m_crds;
	private Vector m_inPlaneStress;	
	
	public StressPoint(String elementName, Vector crds, Vector inPlaneStress){
		this.m_elementName = elementName;
		this.m_crds = crds.clone(true);
		this.m_inPlaneStress = inPlaneStress.clone(true);
	}
	
	public String getName(){
		return m_elementName;
	}
	
	public Vector getCrds(){
		return m_crds;
	}
	
	public Vector getStress(){
		return m_inPlaneStress;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_elementName+"\t");
		for(int i = 0; i < m_crds.size(); i++){			
			sb.append(m_crds.get(i)+"\t");
		}
		for(int i = 0; i < m_inPlaneStress.size(); i++)
			sb.append(m_inPlaneStress.get(i)+"\t");
		return sb.toString();
	}

}
