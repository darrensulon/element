package util.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import util.model.NamedModel;

public class Parser {

	private static final String START_TAG = "[<][.a-z:A-Z:0-9]*[>]";
	private static final String END_TAG = "[<][/][.a-z:A-Z:0-9]*[>]";
	
	protected Map<Class, IParser> m_registry = new HashMap<Class, IParser>();
	
	private static IParser s_current_type;
		
	private Class getClass(String tag) throws ClassNotFoundException{
		if(tag.startsWith("</")){
			return Class.forName(tag.substring(2, tag.length()-1));
		}
		if(tag.startsWith("<")){
			return Class.forName(tag.substring(1, tag.length()-1));
		}	
		throw new ClassNotFoundException("Invalid tag");
	}
	
	public void register(Class _class, IParser parser){
		m_registry.put(_class, parser);
	}

	public void unregister(Class _class){
		m_registry.remove(_class);
	}
	
	public NamedModel fromStream(NamedModel model, InputStream is) throws IOException, 
			ClassNotFoundException{
		LineNumberReader br = new LineNumberReader(new InputStreamReader(is));
		String line;
		while((line = br.readLine()) != null){
			line = line.trim();
			if(line.startsWith("#") || line.length() == 0)
				continue;
			if(line.matches(START_TAG)){
				s_current_type = m_registry.get(getClass(line));
			}
			else if(line.matches(END_TAG)){
				s_current_type = null;
			}
			else {
				if(s_current_type != null)
					try{
						model.add(s_current_type.parse(br.getLineNumber(),line, model.getChosenAnalysis()));
					}
					catch (NoSuchElementException nsee){
						new ParseException(br.getLineNumber(),"missing token(s)",line).printStackTrace();
					}
					catch (ParseException pe) {
						pe.printStackTrace();
					}
			}
		}		
		return model;
	}
	
}