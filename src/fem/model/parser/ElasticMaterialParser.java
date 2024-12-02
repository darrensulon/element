package fem.model.parser;

import java.util.StringTokenizer;

import fem.components.ElasticMaterial;
import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;

public class ElasticMaterialParser implements IParser {
	
	@Override
	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		double e_mod = 0.0;
		double posRatio = 0.0;
		try {
			e_mod = Double.parseDouble(st.nextToken().trim());
			posRatio = Double.parseDouble(st.nextToken().trim());
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		if(st.hasMoreTokens())
			throw new ParseException(lnr,st.countTokens()+" tokens to many",text);
		
		return new ElasticMaterial(name, e_mod, posRatio);
	}

}
