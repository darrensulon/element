package fem.analysis;

import java.io.File;
import fem.model.FemModel;

/**
 * The super class for all types of FEM analysis. 
 * 
 *
 */
public interface Analysis {
	
	public static String HEAT_TRANSFER = "Stationary Heat Flow Analysis";
	public static String SEEPAGE = "Seepage Analysis";
	public static String ELASTICITY = "Elastic Analysis";
	public static String POTENTIAL_FLOW = "Potential Flow Analysis";
	public static String ELECTROSTATIC = "Electrostatic Analysis";

	/**
	 * This is the algorithm required by all finite element analysis'.
	 * This method performs an analysis on the model by setting the system vector, primal vector, 
	 * dual vector and status vector for the system. Following, the system matrix is composed
	 * which is specific to the order of the analysis (i.e. first or second order). 
	 * The system matrix is then decomposed using Cholesky decomposition and the unknowns in the primal and 
	 * dual vectors are solved.
	 * 
	 * @param model 
	 * the model on which the analysis is performed
	 * 
	 * @param inputFile
	 * the input file from which the model is developed
	 */
	public void perform(FemModel model, File inputFile);
	
}
