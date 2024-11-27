package fem.model.output.nodeDisplacement;

import java.util.Iterator;

import math.linalg.Vector;
import math.numericalIntegration.GaussPoint;
import math.numericalIntegration.IntegrationScheme;
import fem.interFace.IElement;
import fem.interFace.INode;
import fem.model.FemModel;
import fem.model.view.ElasticGraphicalOutputOptions;

public class StandardNodeDisplacementRecorder extends AbstractNodeDisplacementRecorder {	
	private int mode;
	
	public StandardNodeDisplacementRecorder(){
		super();
		mode = 0; // Default mode: Gauss if available, otherwise Mid.
	}
	
	public StandardNodeDisplacementRecorder(int mode){
		super();
		this.mode = mode;
	}
	
	public void record(FemModel model) {
		Iterator<INode> nodes = model.iterator(INode.class);
		while(nodes.hasNext()){
			INode node = nodes.next();
			String name = node.getName();
			int elementMode;
			Vector crds = node.getCoordinates();
			Vector displacements = Vector.getVector(2);
			displacements.set(node.getPrimal());
			if(mode == ElasticGraphicalOutputOptions.U_11) {
				displacements.set(0, 1);
			}else if (mode == ElasticGraphicalOutputOptions.U_22) {
				displacements.set(0, 0);
			}
			m_points.add(new NodeDisplacementPoint(name,crds,displacements));		
			displacements.release();
			crds.release();
		}
	}
	
}
