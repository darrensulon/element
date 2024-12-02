package fem.model.parser;

import java.util.StringTokenizer;
import fem.components.StationaryHeatFlowMaterial;
import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;

public class StationaryHeatFlowMaterialParser implements IParser{

	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		double conductivity = 0.0;
		try {
			conductivity = Double.parseDouble(st.nextToken().trim());
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		if(st.hasMoreTokens())
			throw new ParseException(lnr,st.countTokens()+" tokens to many",text);
		
		return new StationaryHeatFlowMaterial(name, conductivity);
	}

}
