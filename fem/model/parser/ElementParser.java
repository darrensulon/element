package fem.model.parser;

import java.util.StringTokenizer;

import util.model.INamedObject;
import util.parser.IParser;
import util.parser.ParseException;
import fem.analysis.Analysis;
import fem.interFace.IElement;

public class ElementParser implements IParser{

	private Class m_elementType;
	
	public ElementParser(Class elemType){
		this.m_elementType = elemType;
	}
	
	public INamedObject parse(int lnr, String text, String chosenAnalysis) throws ParseException {
		StringTokenizer st = new StringTokenizer(text,",");
		String name = st.nextToken().trim();
		String[] nodeNames;
		if(chosenAnalysis.equals(Analysis.POTENTIAL_FLOW)) {
			nodeNames = new String[st.countTokens()];
		}else {
			nodeNames = new String[st.countTokens()-1];
		}
		for(int i = 0; i < nodeNames.length; i++){
			nodeNames[i] = st.nextToken().trim();
		}
		
		if (st.hasMoreTokens()) {
			String matName = st.nextToken().trim();
			try {
				IElement element = (IElement)m_elementType.newInstance();
				element.setName(name);
				element.setNodeNames(nodeNames);
				element.setMaterialName(matName);
				return element;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
		}else {
			try {
				IElement element = (IElement)m_elementType.newInstance();
				element.setName(name);
				element.setNodeNames(nodeNames);
				return element;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
			
		throw new RuntimeException();		
	}

}
