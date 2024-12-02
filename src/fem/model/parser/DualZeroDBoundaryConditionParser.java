package fem.model.parser;

import java.util.StringTokenizer;

import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;
import fem.components.DualZeroDBoundaryCondition;

public class DualZeroDBoundaryConditionParser implements IParser{

	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		String nodeName = st.nextToken().trim();
		double intensity = 0.0;
		double dof = 0.0;
		try{
			dof = Double.parseDouble(st.nextToken().trim());
			if(st.hasMoreTokens()) {
				intensity = Double.parseDouble(st.nextToken().trim());	
			}else {
				intensity = dof;
				dof = 1.0;
			}
			
		}
		catch(NumberFormatException nfe){
			throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
		}
		return new DualZeroDBoundaryCondition(name, nodeName, (int)(dof-1), intensity);
	}

}
