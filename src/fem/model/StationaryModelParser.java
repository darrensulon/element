package fem.model;

import util.parser.Parser;
import fem.components.PlaneQuad4Iso;
import fem.components.ConstantStrainTriangle;
import fem.components.DualZeroDBoundaryCondition;
import fem.components.ElasticMaterial;
import fem.components.ElementLoad;
import fem.components.Quad4Iso;
import fem.components.SeepageMaterial;
//import fem.components.Triangle6Iso;
import fem.components.StationaryHeatFlowMaterial;
import fem.components.Node;
import fem.components.PrimalBoundaryCondition;
import fem.components.Triangle;
import fem.model.parser.DualZeroDBoundaryConditionParser;
import fem.model.parser.ElasticMaterialParser;
import fem.model.parser.ElementLoadParser;
import fem.model.parser.ElementParser;
import fem.model.parser.StationaryHeatFlowMaterialParser;
import fem.model.parser.NodeParser;
import fem.model.parser.PrimalBoundaryConditionParser;
import fem.model.parser.SeepageMaterialParser;

/**
 * In this class, classes which are required to create a FemModel and perform the analysis are registered.
 * Classes need to be registered  in order to be read from an input file.
 * 
 * @author Darren Sulon
 *
 */
public class StationaryModelParser extends Parser{

	/**
	 * All components required to be read from an input file must be registered within this class.
	 */
	public StationaryModelParser() {
		register(Node.class, new NodeParser());
		register(Triangle.class, new ElementParser(Triangle.class));
		register(Quad4Iso.class, new ElementParser(Quad4Iso.class));		
		register(PrimalBoundaryCondition.class, new PrimalBoundaryConditionParser());
		register(DualZeroDBoundaryCondition.class, new DualZeroDBoundaryConditionParser());
		register(StationaryHeatFlowMaterial.class, new StationaryHeatFlowMaterialParser());
		register(ConstantStrainTriangle.class, new ElementParser(ConstantStrainTriangle.class));
		register(PlaneQuad4Iso.class, new ElementParser(PlaneQuad4Iso.class));
		register(ElementLoad.class, new ElementLoadParser());	
		register(SeepageMaterial.class, new SeepageMaterialParser());
		register(ElasticMaterial.class, new ElasticMaterialParser());
		
	}
	
}