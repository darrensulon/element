package fem.model;

import java.util.Iterator;

import math.linalg.Vector;
import util.model.NamedModel;
import fem.analysis.Analysis;
import fem.interFace.IDualBoundaryCondition;
import fem.interFace.IElement;
import fem.interFace.IElementLoad;
import fem.interFace.INode;
import fem.interFace.IPrimalBoundaryCondition;
import fem.model.output.Recorder;

public class FemModel extends NamedModel {

	private static final long serialVersionUID = 1L;
	
	private int numEq;
	
	public void setupEquationNumbers(){
		Iterator<INode> nodes = iterator(INode.class);
		numEq = 0;
		while(nodes.hasNext()){
			INode node = nodes.next();
			int temp = node.setSystemIndex(numEq++);
			numEq = temp;
		}
	}
	
//	public void setupEquationNumbers(){
//		setupBoundaryConditions();
//		Iterator<INode> nodes = iterator(INode.class);
//		numEq = 0;
//		while(nodes.hasNext()){
//			INode node = nodes.next();
//			if(!node.hasPrimalBoundaryCondition())
//				node.setSystemIndex(numEq++);
//		}
//		nodes = iterator(INode.class);
//		while(nodes.hasNext()){
//			INode node = nodes.next();
//			if(node.hasPrimalBoundaryCondition())
//				node.setSystemIndex(numEq++);
//		}
//	}
	
	public void resetPrimalValues(){
		Iterator<INode> nodes = iterator(INode.class);
		while(nodes.hasNext()){
			INode node = nodes.next();
			for(int i = 0; i < node.getPrimal().length; i++) {
				node.getPrimal()[i] = 0;
			}
			
		}
	}
	
	public void resetDualValues(){
		Iterator<IPrimalBoundaryCondition> _primalBoundaryConditions = 
			iterator(IPrimalBoundaryCondition.class);
		while(_primalBoundaryConditions.hasNext()){
			IPrimalBoundaryCondition _primalBC = _primalBoundaryConditions.next();
			_primalBC.setDualValue(0);
		}
	}
	
	public void setupBoundaryConditions(){
		Iterator<IPrimalBoundaryCondition> primalBCs = iterator(IPrimalBoundaryCondition.class);
		while(primalBCs.hasNext()){
			primalBCs.next().node().setPrimalBoundaryCondition(true);
		}
	}
	
	public int numDofs(){
		return numEq;
	}
	
	public boolean[] getStatusVector(){
		boolean[] status = new boolean[numEq];
		Iterator<IPrimalBoundaryCondition> supports = iterator(IPrimalBoundaryCondition.class);
		while(supports.hasNext()){
			IPrimalBoundaryCondition pbc = supports.next();
			status[pbc.getSystemIndex()] = true;
		}		
		return status;
	}
	
	public Vector getSystemVector(){
		Vector _system = Vector.getVector(numEq);
		Iterator<IElementLoad> element_loads = iterator(IElementLoad.class);
		while(element_loads.hasNext()){
			IElementLoad eload = element_loads.next();
			IElement element = eload.element();
			Vector _load = element.getElementVector(eload.getNodalIntensities());
			_system.add(_load,element.getSystemIndices());
			_load.release();
		}		
		return _system;
	}
	
	public Vector getPrimalVector(){
		Vector _primal = Vector.getVector(numEq);
		Iterator<INode> nodes = iterator(INode.class);
		int count = 0;
		while(nodes.hasNext()){
			INode node = nodes.next();
			double[] nodePrimal = node.getPrimal();
			for(int i = 0; i < nodePrimal.length; i++) {
				_primal.set(nodePrimal[i], count);
				count++;
			}
		}
		return _primal;
	}
	
	public Vector getDualVector(){
		Vector _dual = Vector.getVector(numEq);
		Iterator<IPrimalBoundaryCondition> _primalBoundaryConditions = 
			iterator(IPrimalBoundaryCondition.class);
		while(_primalBoundaryConditions.hasNext()){
			IPrimalBoundaryCondition _primalBC = _primalBoundaryConditions.next();
			_dual.set(_primalBC.getDualValue(), _primalBC.getSystemIndex());
		}
		Iterator<IDualBoundaryCondition> loads = iterator(IDualBoundaryCondition.class);
		while(loads.hasNext()){
			IDualBoundaryCondition load = loads.next();
			Vector _load = load.getDualValue();
			_dual.add(_load, load.getSystemIndex());
			_load.release();
		}
		return _dual;
	}

	public void setPrimalResults(Vector primal){
		Iterator<INode> nodes = iterator(INode.class);
		while(nodes.hasNext()){
			INode node = nodes.next();
			int[] indices = node.getSystemIndex();
			Vector u = primal.getSubVector(indices);
			double[] nodePrimal = u.get();
			for(int i = 0; i < indices.length; i++) {
				node.setPrimal(nodePrimal[i], i);
			}
		}
	}

	public void setDualResults(Vector dual){
		Iterator<IPrimalBoundaryCondition> _primalBoundaryConditions = 
			iterator(IPrimalBoundaryCondition.class);
		while(_primalBoundaryConditions.hasNext()){
			IPrimalBoundaryCondition _primalBC = _primalBoundaryConditions.next();
			_primalBC.setDualValue(dual.get(_primalBC.getSystemIndex()));
		}
	}

	public Recorder recordState(Recorder rec){
		rec.record(this);
		return rec;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator iter = iterator();
		while(iter.hasNext())
			sb.append(iter.next()+"\n");
		return sb.toString();
	}
		
}
