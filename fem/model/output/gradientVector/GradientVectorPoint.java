package fem.model.output.gradientVector;

import math.linalg.Vector;

public class GradientVectorPoint {

	private String m_elementName;
	private Vector m_crds;
	private Vector m_heatState;	
	
	public GradientVectorPoint(String elementName, Vector crds, Vector heatState){
		this.m_elementName = elementName;
		this.m_crds = crds.clone(true);
		this.m_heatState = heatState.clone(true);
	}
	
	public String getName(){
		return m_elementName;
	}
	
	public Vector getCrds(){
		return m_crds;
	}
	
	public Vector getHeatState(){
		return m_heatState;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_elementName+"\t");
		for(int i = 0; i < m_crds.size(); i++){			
			sb.append(m_crds.get(i)+"\t");
		}
		for(int i = 0; i < m_heatState.size(); i++)
			sb.append(m_heatState.get(i)+"\t");
		return sb.toString();
	}

}
