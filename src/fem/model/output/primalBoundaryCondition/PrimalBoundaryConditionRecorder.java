package fem.model.output.primalBoundaryCondition;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import math.linalg.Vector;
import fem.interFace.INode;
import fem.interFace.IPrimalBoundaryCondition;
import fem.model.FemModel;
import fem.model.output.Recorder;

public class PrimalBoundaryConditionRecorder implements Recorder{

	private List<Point2D> m_crds;
	private Map<Point2D,String> m_names;
	
	public PrimalBoundaryConditionRecorder(){
		m_crds = new ArrayList<Point2D>();
		m_names = new HashMap<Point2D, String>();
	}
	
	public Iterator<Point2D> iterate(){
		return m_crds.iterator();
	}
	
	public String getName(Point2D pnt){
		return m_names.get(pnt);
	}
	
	public void record(FemModel model) {
		Iterator<IPrimalBoundaryCondition> pbcs = model.iterator(IPrimalBoundaryCondition.class);
		while(pbcs.hasNext()){
			IPrimalBoundaryCondition p = pbcs.next();
			String name_value = p.node().getName()+" = "+p.getPrimalValue();
			Vector crds = p.node().getCoordinates();
			Point2D pnt = new Point2D.Double(crds.get(0),crds.get(1));
			m_crds.add(pnt);
			m_names.put(pnt, name_value);
			crds.release();
		}
	}
	
}
