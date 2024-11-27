package fem.model.output.strain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fem.model.FemModel;
import fem.model.output.Recorder;

public abstract class AbstractStrainRecorder implements Recorder {
	
	protected List<StrainPoint> m_points;
	
	public AbstractStrainRecorder(){
		m_points = new ArrayList<StrainPoint>();
	}
	
	public Iterator<StrainPoint> iterate(){
		return m_points.iterator();
	}
	
	public abstract void record(FemModel model);
	
	public double maxAbsHeatState(){
		Iterator<StrainPoint> iter =iterate();
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
