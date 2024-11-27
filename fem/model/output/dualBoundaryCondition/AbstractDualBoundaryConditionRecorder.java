package fem.model.output.dualBoundaryCondition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fem.model.FemModel;
import fem.model.output.Recorder;

public abstract class AbstractDualBoundaryConditionRecorder implements Recorder {
	
	protected List<DualBoundaryConditionPoint> m_points;
	
	public AbstractDualBoundaryConditionRecorder(){
		m_points = new ArrayList<DualBoundaryConditionPoint>();
	}
	
	public Iterator<DualBoundaryConditionPoint> iterate(){
		return m_points.iterator();
	}
	
	public abstract void record(FemModel model);
	
	public double maxAbsHeatState(){
		Iterator<DualBoundaryConditionPoint> iter =iterate();
		double max = 0.0;
		while(iter.hasNext()){
			max = Math.max(iter.next().getHeatState().abs(), max);
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
