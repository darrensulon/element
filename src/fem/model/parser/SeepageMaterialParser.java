package fem.model.parser;

import java.util.StringTokenizer;

import fem.components.SeepageMaterial;
import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;

public class SeepageMaterialParser implements IParser {

	@Override
	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		double k = 0.0;
		try {
			k = Double.parseDouble(st.nextToken().trim());
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		if(st.hasMoreTokens())
			throw new ParseException(lnr,st.countTokens()+" tokens to many",text);
		
		return new SeepageMaterial(name, k);
	}

}
