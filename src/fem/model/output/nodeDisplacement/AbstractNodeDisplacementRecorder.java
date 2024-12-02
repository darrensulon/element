package fem.model.output.nodeDisplacement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fem.model.FemModel;
import fem.model.output.Recorder;

public abstract class AbstractNodeDisplacementRecorder implements Recorder {
	
	protected List<NodeDisplacementPoint> m_points;
	
	public AbstractNodeDisplacementRecorder(){
		m_points = new ArrayList<NodeDisplacementPoint>();
	}
	
	public Iterator<NodeDisplacementPoint> iterate(){
		return m_points.iterator();
	}
	
	public abstract void record(FemModel model);
	
	public double maxAbsDisplacement(){
		Iterator<NodeDisplacementPoint> iter =iterate();
		double max = 0.0;
		while(iter.hasNext()){
			max = Math.max(iter.next().getDisplacements().abs(), max);
		}
		return max;
	}
	
	public String toString() {
		final String LB = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder("Node Displacement Recorder"+LB);
		Iterator iter = iterate();
		while(iter.hasNext())
			sb.append(iter.next()+LB);
		return sb.toString();
	}
}
