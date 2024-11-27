package fem.model.output.nodalFieldState;

import java.io.Serializable;

import math.linalg.Vector;

public class NodalFieldStateValue implements Serializable {

	private String m_nodeName;
	private Vector m_crds;
	private double m_temp;	
	
	public NodalFieldStateValue(String elementName, Vector crds, double temp){
		this.m_nodeName = elementName;
		this.m_crds = crds.clone(true);
		this.m_temp = temp;
	}
	
	public String getName(){
		return m_nodeName;
	}
	
	public Vector getCrds(){
		return m_crds;
	}
	
	public double getFieldState(){
		return m_temp;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_nodeName+"\t");
		for(int i = 0; i < m_crds.size(); i++){			
			sb.append(m_crds.get(i)+"\t");
		}
		sb.append(m_temp);
		return sb.toString();
	}
		
}
