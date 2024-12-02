package fem.components;

import util.model.NamedObject;


/**
 * This class is the material type specific to plane elastic finite element analysis. The material properties required to
 * perform an plane elastic analysis are provided in the class. Analysis can be performed for the plane stress or plane strain case.
 * In the case of plane stress the elastic modulus can be provided as normal. In the case of plane strain, the elastic modulus or 'E'
 * value must be converted to represent the plane strain case using the following E = E/[(1-v)(1+v)]
 * 
 */
public class ElasticMaterial extends NamedObject{
	
	/**
	 * The elastic modulus of the material.
	 */
	private double eMod;
	
	/**
	 * The poisson's ratio of the material.
	 */
	private double posRatio;

	/**
	 * When instantiated the name of the material, elastic modulus and poisson's ratio is provided.
	 * 
	 * @param name
	 * name of material
	 * 
	 * @param eMod
	 * elastic modulus
	 * 
	 * @param posRatio
	 * poisson's ratio
	 */
	public ElasticMaterial(String name, double eMod, double posRatio) {
		super(name);
		this.eMod = eMod;
		this.posRatio = posRatio;
	}

	/**
	 * Gets the elastic modulas of the material.
	 * 
	 * @return
	 * elastic modulus
	 */
	public double geteMod() {
		return eMod;
	}

	/**
	 * Gets the poisson's ratio of the material.
	 * 
	 * @return
	 * poisson's ratio
	 */
	public double getPosRatio() {
		return posRatio;
	}
	
	

}
