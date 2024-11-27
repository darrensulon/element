package fem.model.output.gradientVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fem.model.FemModel;
import fem.model.output.Recorder;

public abstract class AbstractGradientVectorRecorder implements Recorder {

	public static final int MID_POINT = 1;
	public static final int GAUSS_POINT = 2;
	public static final int BOTH = 3;
	
	protected List<GradientVectorPoint> m_points;
	
	public AbstractGradientVectorRecorder(){
		m_points = new ArrayList<GradientVectorPoint>();
	}
	
	public Iterator<GradientVectorPoint> iterate(){
		return m_points.iterator();
	}
	
	public abstract void record(FemModel model);
	
	public double maxAbsHeatState(){
		Iterator<GradientVectorPoint> iter =iterate();
		double max = 0.0;
		while(iter.hasNext()){
			max = Math.max(iter.next().getHeatState().abs(), max);
		}
		return max;
	}
	
	public String toString() {
		final String LB = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder("Heat State Recorder"+LB);
		Iterator iter = iterate();
		while(iter.hasNext())
			sb.append(iter.next()+LB);
		return sb.toString();
	}
}
