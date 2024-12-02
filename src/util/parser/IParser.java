package util.parser;

import util.model.INamedObject;

public interface IParser {

	public INamedObject parse(int lineNr, String text, String ChosenAnalysis) throws ParseException;
	
}
