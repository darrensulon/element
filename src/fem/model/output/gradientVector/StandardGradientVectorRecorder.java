package fem.model.output.gradientVector;

import java.util.Iterator;

import math.linalg.Vector;
import math.numericalIntegration.GaussPoint;
import math.numericalIntegration.IntegrationScheme;
import fem.interFace.IElement;
import fem.model.FemModel;

public class StandardGradientVectorRecorder extends AbstractGradientVectorRecorder {	
	private int mode;
	
	public StandardGradientVectorRecorder(){
		super();
		mode = 0; // Default mode: Gauss if available, otherwise Mid.
	}
	
	public StandardGradientVectorRecorder(int mode){
		super();
		this.mode = mode;
	}
	
	public void record(FemModel model) {
		Iterator<IElement> elements = model.iterator(IElement.class);
		while(elements.hasNext()){
			IElement element = elements.next();
			String name = element.getName();
			IntegrationScheme m_gauss = element.getIntegrationScheme();
			int elementMode;
			if(mode == 0){
				if(m_gauss != null){
					elementMode = AbstractGradientVectorRecorder.GAUSS_POINT;
				} else {
					elementMode = AbstractGradientVectorRecorder.MID_POINT;
				}
			} else {
				elementMode = mode;
				if(m_gauss == null)elementMode = AbstractGradientVectorRecorder.MID_POINT;
			}
			if((elementMode & GAUSS_POINT) == GAUSS_POINT){
				// Gauss loop
				for (int i = 0; i < m_gauss.numberOfGaussPoints(); i++) {
					GaussPoint gp = m_gauss.getGaussPoint(i);
					Vector crds = gp.coordinates();
					Vector x = element.zTox(crds);
					Vector hs = element.computeGradientVector(crds);			
					m_points.add(new GradientVectorPoint(name,x,hs));
					x.release();			
					hs.release();
					crds.release();
				}
			}
			if((elementMode & MID_POINT) == MID_POINT){ 
				Vector crds = element.centroidZcoordinates();
				Vector x = element.zTox(crds);
				Vector hs = element.computeGradientVector(crds);			
				m_points.add(new GradientVectorPoint(name,x,hs));
				x.release();			
				hs.release();
				crds.release();
			}
		}
	}
	
}
