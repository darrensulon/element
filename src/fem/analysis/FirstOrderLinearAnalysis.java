package fem.analysis;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

import math.linalg.Cholesky;
import math.linalg.Matrix;
import math.linalg.Profile;
import math.linalg.ProfileMatrix;
import math.linalg.Vector;
import fem.interFace.IElement;
import fem.model.FemModel;

/**
 * In first-order analysis the System matrix is assumed to be constant and unaffected by changes
 * in the geometry of the model. This is the standard assumption of stationary heat flow, plane elastic problems, groundwater seepage, 
 * potential flow and electrostatic first-order analysis.
 *
 */
public class FirstOrderLinearAnalysis implements Analysis {

	/**
	 * The output stream to which the calculations of the analysis is written.
	 */
	private PrintStream out;
	
	/**
	 * Determines whether the analysis performed must be detailed or not.
	 */
	private boolean detailed;
	
	/**
	 * When instantiated, the stream to which the calculations and whether the calculations should 
	 * be detailed or not are set.
	 * 
	 * @param ps
	 * print stream to which the analysis is written
	 * 
	 * @param detail
	 * whether the analysis shall be detailed or not
	 */
	public FirstOrderLinearAnalysis(PrintStream ps, boolean detail){
		this.out = ps;
		this.detailed = detail;
	}
	
	/**
	 * Assembles the system matrix for the model. The element matrix for each element is selected 
	 * depending on which type of analysis is chosen.
	 * 
	 * 
	 * @param model
	 * model on which analysis is performed
	 * 
	 * @param mat
	 * profile matrix of the system
	 */
	public void assembleSystemMatrix(FemModel model, ProfileMatrix mat){
		Iterator<IElement> elements = model.iterator(IElement.class);
		if(out != null && detailed){
			out.println();
			out.println("Element matrices...\t");
		}
		while(elements.hasNext()){
			IElement element = elements.next();
			Matrix ke = null;
			if(model.getChosenAnalysis().equals(Analysis.HEAT_TRANSFER)) {
				ke = element.getElementConductivityMatrix();
			}else if (model.getChosenAnalysis().equals(Analysis.ELASTICITY)) {
				ke = element.getElementStiffnessMatrix();
			}else if (model.getChosenAnalysis().equals(Analysis.POTENTIAL_FLOW)) {
				ke = element.getElementPotentialMatrix();
			}else if (model.getChosenAnalysis().equals(Analysis.SEEPAGE)) {
				ke = element.getElementPermeabilityMatrix();
			}
			
			
			if(out != null && detailed)
				ke.printf("ke for "+element.getName(), out);
			
			mat.addMatrix(ke, element.getSystemIndices());
			ke.release();
		}
	}
	
	
	public void perform(FemModel model, File inputFile){		
		model.setupEquationNumbers();
		model.setupBoundaryConditions();
		int numEq = model.numDofs();
		
		Vector sysVec = model.getSystemVector();
		Vector primalVec = model.getPrimalVector();
		Vector dualVec = model.getDualVector();
		boolean[] statusVec = model.getStatusVector();

		if(out != null){
			out.println();
			out.println("+----------------------------------------------------------+");
			out.println("|  FirstOrderLinearAnalysis                                |");
			out.println("+----------------------------------------------------------+");
			out.println();
			out.println("Analysis Type: "+model.getChosenAnalysis());
			out.println("Analysis performed on: " + inputFile.getName() );
			out.println();
			if(detailed){
				sysVec.print("sysvec\t", out);
				dualVec.print("dualVec\t", out);
				primalVec.print("primalVec", out);
				Vector.print("status", statusVec, out);
			}
		}
		
		// creating profile 
		Profile profile = new Profile(numEq);
		Iterator<IElement> elements = model.iterator(IElement.class);
		while(elements.hasNext()){
			profile.add(elements.next().getSystemIndices());
		}		
		ProfileMatrix _Ks = new ProfileMatrix(profile.profile());
		
		if(out != null)
			out.print("Assemble [Ks]...\t");
		long start = System.currentTimeMillis();
		
		assembleSystemMatrix(model,_Ks);
		
		long time = System.currentTimeMillis() - start;				
		if(out != null){
			out.println(time+" [ms]");
			if(detailed)
				_Ks.printf("Ks", out);
			out.print("Decomposing [Ks]...\t");
		}
		
		start = System.currentTimeMillis();
		
		Cholesky.decompose(_Ks,statusVec);
		
		time = System.currentTimeMillis() - start;
		if(out != null){
			out.println(time+" [ms]");
			if(detailed)
				_Ks.printf("Ks decomposed", out);
			out.print("Solve primal vector...\t");
		}		
		start = System.currentTimeMillis();
		
		_Ks.solvePrimal(primalVec, dualVec, sysVec, statusVec);
		
		time = System.currentTimeMillis() - start;	
		if(out != null){
			out.println(time+" [ms]");
			out.print("Solve dual vector...\t");
		}				
		start = System.currentTimeMillis();
		
		_Ks.solveDual(primalVec, dualVec, sysVec, statusVec);
		
		time = System.currentTimeMillis() - start;						
		if(out != null){
			out.println(time+" [ms]");
			if(detailed){
				primalVec.printf("primal", out);
				dualVec.printf("dual", out);
			}
			out.println("SUM dual   = "+dualVec.sum());
			out.println("SUM system = "+sysVec.sum());			
		}
		
		model.setPrimalResults(primalVec);
		model.setDualResults(dualVec);
		
		_Ks = _Ks.release();
		primalVec = primalVec.release();
		dualVec = dualVec.release();
		sysVec = sysVec.release();
		
		if(out != null)
			out.println("+----------------------------------------------------------+");
	}
	
}
