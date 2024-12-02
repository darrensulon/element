package fem.model.view;

public class ElasticGraphicalOutputOptions {
	
	public static final String STRESS11 = "Normal Stresses in X1 plane";
	public static final String STRESS22 = "Normal Stresses in X2 plane";
	public static final String STRESS12 = "Shear Stresses in X1-X2 plane";
	public static final String STRESS_INPLANE1 = "Max in plane stresses in X1-X2 plane";
	public static final String STRESS_INPLANE2 = "Min in plane stresses in X1-X2 plane";
	
	public static final String DISP11 = "Displacement in X1 plane";
	public static final String DISP22 = "Displacement in X2 plane";
	public static final String DISP_RES = "Displacement in X1-X2 plane";
	public static final String DISP_SHAPE = "Displaced Shape";
	
	public static final String STRAIN11 = "Normal Strains in X1 plane";
	public static final String STRAIN22 = "Normal Strains in X2 plane";
	public static final String STRAIN12 = "Shear Strains in X1-X2 plane";
	
	public static final int U_11 = 0;
	public static final int U_22 = 1;
	public static final int U_RES = 2;
	public static final int U_SHAPE = 3;
	
	public static final int S_11 = 4;
	public static final int S_22 = 5;
	public static final int S_12 = 6;
	public static final int S_INPLANE1 = 7;
	public static final int S_INPLANE2 = 8;
	
	public static final int E_11 = 9;
	public static final int E_22 = 10;
	public static final int E_12 = 11; 
	
	public static final int DISP_AT_GP = 0;
	public static final String AT_GP = "Gauss Points";
	public static final int DISP_AT_CENTRE = 1;
	public static final String AT_CENTRE = "Centre of Element";

}
