package fem.model.parser;

import java.util.StringTokenizer;

import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;
import fem.components.Node;

public class NodeParser implements IParser{

	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		double[] crds = new double[Math.min(st.countTokens(),3)];
		try{
			for(int i = 0; i < crds.length; i++){
				crds[i] = Double.parseDouble(st.nextToken().trim());
			}
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		if(st.hasMoreTokens())
			throw new ParseException(lnr,st.countTokens()+" tokens to many",text);
		return new Node(name,crds, chosenAnalysis);
	}

}
