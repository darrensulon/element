package fem.model.output.strain;

import java.util.Iterator;

import math.linalg.Matrix;
import math.linalg.Vector;
import math.numericalIntegration.GaussPoint;
import math.numericalIntegration.IntegrationScheme;
import fem.components.ConstantStrainTriangle;
import fem.interFace.IElement;
import fem.interFace.INode;
import fem.model.FemModel;
import fem.model.output.gradientVector.AbstractGradientVectorRecorder;
import fem.model.output.gradientVector.GradientVectorPoint;
import fem.model.output.stresses.StressPoint;
import fem.model.view.ElasticGraphicalOutputOptions;

public class StandardStrainRecorder extends AbstractStrainRecorder {	
	private int mode;
	private int whereToDisp;
	
	public StandardStrainRecorder(){
		super();
		mode = 0; // Default mode: Gauss if available, otherwise Mid.
	}
	
	public StandardStrainRecorder(int mode, int whereToDisp){
		super();
		this.mode = mode;
		this.whereToDisp = whereToDisp;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void record(FemModel model) {
		Iterator<IElement> elements = model.iterator(IElement.class);
		while(elements.hasNext()){
			IElement element = elements.next();
			String name = element.getName();
			
			if(element instanceof ConstantStrainTriangle) whereToDisp = ElasticGraphicalOutputOptions.DISP_AT_CENTRE;
			
			if(whereToDisp ==  ElasticGraphicalOutputOptions.DISP_AT_CENTRE) {
				Vector crds = element.centroidZcoordinates();
				Vector x = element.zTox(crds);
				Vector hs = element.getStrains(crds);
				if(mode == ElasticGraphicalOutputOptions.E_11) {
					hs = hs.getSubVector(new int[] {0, 1});
					hs.set(0, 1);
				}else if(mode == ElasticGraphicalOutputOptions.E_22) {
					hs = hs.getSubVector(new int[] {0, 1});
					hs.set(0, 0);
				}else if (mode == ElasticGraphicalOutputOptions.E_12) {
					hs = hs.getSubVector(new int[] {2});
				
				}
				m_points.add(new StrainPoint(name,x,hs));
				hs.release();
				x.release();
				crds.release();
				
			}else if (whereToDisp == ElasticGraphicalOutputOptions.DISP_AT_GP) {
				for (int i = 0; i < element.getIntegrationScheme().numberOfGaussPoints(); i++) {
					GaussPoint gp = element.getIntegrationScheme().getGaussPoint(i);
					Vector gp_crds = gp.coordinates();
					Vector x = element.zTox(gp_crds);
					Vector hs = element.getStrains(gp_crds);
					if(mode == ElasticGraphicalOutputOptions.E_11) {
						hs = hs.getSubVector(new int[] {0, 1});
						hs.set(0, 1);
					}else if(mode == ElasticGraphicalOutputOptions.E_22) {
						hs = hs.getSubVector(new int[] {0, 1});
						hs.set(0, 0);
					}else if (mode == ElasticGraphicalOutputOptions.E_12) {
						hs = hs.getSubVector(new int[] {2});
					}
					m_points.add(new StrainPoint(name,x,hs));
					hs.release();
					gp_crds.release();
					x.release();
				}
			}
		}
	}
}


