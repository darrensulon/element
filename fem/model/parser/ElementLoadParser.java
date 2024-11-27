package fem.model.parser;

import java.util.StringTokenizer;

import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;
import fem.components.ElementLoad;

public class ElementLoadParser implements IParser{

	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		String elementName = st.nextToken().trim();
		double[] values = new double[st.countTokens()];
		try{
			for(int i = 0; i < values.length; i++){
				values[i] = Double.parseDouble(st.nextToken().trim());
			}	
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		return new ElementLoad(name, elementName, values);
	}

}
