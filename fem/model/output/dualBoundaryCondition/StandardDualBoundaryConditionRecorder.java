package fem.model.output.dualBoundaryCondition;

import java.util.Iterator;

import math.linalg.Vector;
import math.numericalIntegration.GaussPoint;
import math.numericalIntegration.IntegrationScheme;
import fem.interFace.IDualBoundaryCondition;
import fem.interFace.IElement;
import fem.interFace.INode;
import fem.model.FemModel;

public class StandardDualBoundaryConditionRecorder extends AbstractDualBoundaryConditionRecorder {	
	private int mode;
	
	public StandardDualBoundaryConditionRecorder(){
		super();
		mode = 0; // Default mode: Gauss if available, otherwise Mid.
	}
	
	public StandardDualBoundaryConditionRecorder(int mode){
		super();
		this.mode = mode;
	}
	
	public void record(FemModel model) {
		Iterator<IDualBoundaryCondition> duals = model.iterator(IDualBoundaryCondition.class);
		while(duals.hasNext()){
			IDualBoundaryCondition d = duals.next();
			String name = d.getName();
			int elementMode;
			Vector crds = d.node().getCoordinates();
			Vector dualVec = Vector.getVector(2);
			dualVec.clear();
			dualVec.add(d.getDualValue().get(0), d.getDoF());
			m_points.add(new DualBoundaryConditionPoint(name,crds,dualVec));		
			dualVec.release();
			crds.release();
		}
	}
	
}
