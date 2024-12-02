/*
 * Created on Jul 2, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package util.coll;

/**
 * @author bertie
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassFilter implements IteratorFilter{
	private static final long serialVersionUID = 1L;
	private Class m_filterClass; 
		
	public ClassFilter(Class filter){
		m_filterClass = filter;
	}
	
	public boolean matches(Object obj) {
		return m_filterClass.isInstance(obj);
	}

}
