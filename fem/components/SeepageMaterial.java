package fem.components;

import util.model.NamedObject;

/**
* This class is the material type specific to seepage finite element analysis. The material properties required to
* perform a seepage analysis are provided in the class.
* 
* @author Darren Sulon
*
*/
public class SeepageMaterial extends NamedObject{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The permeability of the material
	 */
	private double k;

	/**
	 * When instantiated the name of the material, and seepage permeability is provided.
	 * 
	 * @param name
	 * name of material
	 * 
	 * @param k
	 * permeability
	 */
	public SeepageMaterial(String name, double k) {
		super(name);
		this.k = k;
	}

	/**
	 * Gets the permeability of the seepage material.
	 * 
	 * @return
	 * permeability
	 */
	public double getK() {
		return k;
	}
	
	
}
