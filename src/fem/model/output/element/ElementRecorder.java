package fem.model.output.element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import fem.components.Triangle6Iso;
import fem.interFace.IElement;
import fem.model.FemModel;
import fem.model.output.Recorder;
import math.linalg.Matrix;

public class ElementRecorder implements Recorder, Serializable {

	private List<Element2D> m_list;
	
	public ElementRecorder(){
		m_list = new ArrayList<Element2D>();
	}
	
	public Iterator<Element2D> iterate(){
		return m_list.iterator();
	}
	
	public void record(FemModel model) {
		Iterator<IElement> elements = model.iterator(IElement.class);
		while(elements.hasNext()){
			IElement element = elements.next();
			String name = element.getName();
			Matrix x = element.computeX();
			double[] geom = new double[element.numNodes()*2];
//			if(element instanceof Triangle6Iso){
//				int[] _idx = new int[] {0, 5, 1, 3, 2, 4}; 
//				for(int i = 0; i < 2; i++){
//					for(int j = 0; j < 6; j++){
//						geom[2*j+i] = x.get(i,_idx[j]);
//					}
//				}
//			}
//			else {
				for(int i = 0; i < 2; i++){
					for(int j = 0; j < element.numNodes(); j++){
						geom[2*j+i] = x.get(i,j);
					}
				}
//			}
			x.release();
			m_list.add(new Element2D(name,geom));
		}
	}
	
}
