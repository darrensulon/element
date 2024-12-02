package fem.model.output.stresses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fem.model.FemModel;
import fem.model.output.Recorder;

public abstract class AbstractStressRecorder implements Recorder {
	
	protected List<StressPoint> m_points;
	
	public AbstractStressRecorder(){
		m_points = new ArrayList<StressPoint>();
	}
	
	public Iterator<StressPoint> iterate(){
		return m_points.iterator();
	}
	
	public abstract void record(FemModel model);
	
	public double maxAbsHeatState(){
		Iterator<StressPoint> iter =iterate();
		double max = 0.0;
		while(iter.hasNext()){
			max = Math.max(iter.next().getStress().abs(), max);
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
