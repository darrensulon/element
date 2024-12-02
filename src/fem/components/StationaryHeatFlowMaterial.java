package fem.components;

import util.model.NamedObject;

/**
* This class is the material type specific to stationary heat flow finite element analysis. The material properties required to
* perform a stationary heat flow analysis are provided in the class.
* 
* @author Darren Sulon
*
*/
public class StationaryHeatFlowMaterial extends NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * The heat conductivity of the material.
	 */
	private double conductivity;

	/**
	 * When instantiated the name of the material, and heat conductivity is provided.
	 * 
	 * @param name
	 * name of material
	 * 
	 * @param conductivity
	 * heat conductivity
	 */
	public StationaryHeatFlowMaterial(String name, double conductivity){
		super(name);
		this.conductivity = conductivity;
	}
	
	/**
	 * Gets the heat conductivity of the material.
	 * 
	 * @return
	 * heat conductivity
	 */
	public double getConductivity() {
		return conductivity;
	}
	
}
