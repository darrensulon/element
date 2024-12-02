package fem.model.parser;

import java.util.StringTokenizer;

import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;
import fem.components.PrimalBoundaryCondition;

public class PrimalBoundaryConditionParser implements IParser{

	// s1, n1
	// s2, n2, 10
	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		String nodeName = st.nextToken().trim();
		if(st.countTokens() == 0)
			return new PrimalBoundaryCondition(name, nodeName);
		double dof = 0.0;
		double _primal = 0.0;
		if(st.hasMoreTokens()){
			try {
				dof = Double.parseDouble(st.nextToken().trim());
				if(st.hasMoreTokens()) {
					_primal = Double.parseDouble(st.nextToken().trim());
				}else {
					_primal = dof;
					dof = 1.0;
				}
				
			}
			catch(NumberFormatException nfe){
				throw new ParseException(lnr,nfe.getClass().getName()+" - '"+nfe.getLocalizedMessage()+"'",text);
			}
		}
		if(st.hasMoreTokens())
			throw new ParseException(lnr,st.countTokens()+" token(s) to many",text);
		return new PrimalBoundaryCondition(name, nodeName, (int)(dof-1), _primal);
	}

}
