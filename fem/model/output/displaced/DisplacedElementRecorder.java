package fem.model.output.displaced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import fem.components.Triangle6Iso;
import fem.interFace.IElement;
import fem.model.FemModel;
import fem.model.output.Recorder;
import math.linalg.Matrix;
import math.linalg.Vector;

public class DisplacedElementRecorder implements Recorder, Serializable {

	private List<DisplacedElement2D> m_list;
	
	public DisplacedElementRecorder(){
		m_list = new ArrayList<DisplacedElement2D>();
	}
	
	public Iterator<DisplacedElement2D> iterate(){
		return m_list.iterator();
	}
	
	public double determineModelScale(FemModel model) {
		double scale = 0.0;
		Iterator<IElement> elements = model.iterator(IElement.class);
		while(elements.hasNext()){
			IElement element = elements.next();
			double x = element.computeX().maxEntry();
			if(scale*scale < x*x) {
				scale = Math.abs(x);
			}
		}
		return scale;		
	}
	
	public double determineDisplacementScale(FemModel model) {
		double scale = model.getPrimalVector().maxEntry();
		return scale;		
	}
	
	public void record(FemModel model) {
		Iterator<IElement> elements = model.iterator(IElement.class);
		double modScale = determineModelScale(model);
		double dispScale = determineDisplacementScale(model);
		double percentOfModel  = 0.01;
		while(elements.hasNext()){
			IElement element = elements.next();
			String name = element.getName();
			Matrix x = element.computeX();
			Vector u = element.computeU();
			u.scale(modScale/dispScale*percentOfModel);
			double[] geom = new double[element.numNodes()*2];

				for(int i = 0; i < 2; i++){
					for(int j = 0; j < element.numNodes(); j++){
						geom[2*j+i] = x.get(i,j) + u.get(i+2*j);
					}
				}
			x.release();
			m_list.add(new DisplacedElement2D(name,geom));
		}
	}
	
}
