package util.model;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fem.analysis.Analysis;
import fem.model.FemException;
import util.coll.ClassFilter;
import util.coll.FilterableCollection;

public class NamedModel extends NamedObject implements Externalizable {
	
	private static final long serialVersionUID = 1;

	private Set<INamedObject> components;
	private Map<String,INamedObject> registry;
	private List<ModelListener> m_listeners;
	protected boolean fireEvents = false;
	private String chosenAnalysis = Analysis.HEAT_TRANSFER;
	
	public String getChosenAnalysis() {
		return chosenAnalysis;
	}
	
	public void setChosenAnalysis(String chosenAnalysis) {
		this.chosenAnalysis = chosenAnalysis;
	}
	
	public NamedModel(){
		components = new LinkedHashSet<INamedObject>();
		registry = new HashMap<String, INamedObject>();
		m_listeners = new ArrayList<ModelListener>();
	}	
	
	public NamedModel(String name){
		super(name);
		components = new LinkedHashSet<INamedObject>();
		registry = new HashMap<String, INamedObject>();
		m_listeners = new ArrayList<ModelListener>();		
	}
	
	public boolean contains(INamedObject nobj){
		return components.contains(nobj);
	}
	
	public boolean add(INamedObject nobj){
		boolean _added = components.add(nobj);
		if(_added){
			if(nobj.getModel()!=null) // object only allowed in one model at a time.
				nobj.getModel().remove(nobj);
			nobj.setModel(this);
			registry.put(nobj.getName(), nobj);
			fireAddEvent(nobj);
			return true;
		}
		return false;
	}

	public boolean remove(INamedObject nobj){
		boolean _contains = components.contains(nobj);
		if(_contains){
			nobj.setModel(null);
			components.remove(nobj);
			registry.remove(nobj.getName());
			fireRemoveEvent(nobj);
			return true;
		}
		return false;
	}
	
	public INamedObject get(String name){
		if(!registry.containsKey(name))
			throw new NamedObjectNotFoundException(name);
		return registry.get(name);		
	}
	
	//.......Component administration .....................
	public Iterator iterator(Class<?> type){
		return FilterableCollection.filterableIterator(components.iterator(), new ClassFilter(type));
	}
		
	public Iterator<INamedObject> iterator(){
		return components.iterator();
	}

	public int size(){
		return components.size();
	}
	
	public int size(Class<?> type){
		Iterator<Object> iter = FilterableCollection.filterableIterator(components.iterator(), new ClassFilter(type));
		int _size = 0;
		while(iter.hasNext()){
			iter.next();
			_size++;
		}
		return _size;
	}
	
	//.......Model listener administration.....................		
	
	public void addModelListener(ModelListener ml){
		m_listeners.add(ml);
	}
	
	public void removeModelListener(ModelListener ml){
		m_listeners.remove(ml);
	}
	public void clearModelListeners(){
		m_listeners.clear();
	}
	
	protected Iterator<ModelListener> listeners(){
		return m_listeners.iterator();
	}
	
	private void fireAddEvent(INamedObject nobj){
		if(!fireEvents)
			return;
		ModelEvent me = new ModelEvent(nobj,this);
		for(ModelListener ml: m_listeners)
			ml.wasAdded(me);
	}
	
	private void fireRemoveEvent(INamedObject nobj){
		if(!fireEvents)
			return;
		ModelEvent me = new ModelEvent(nobj,this);
		for(ModelListener ml: m_listeners)
			ml.wasRemoved(me);
	}
	
	protected void fireChangeEvent(INamedObject nobj){
		if(!fireEvents)
			return;
		ModelEvent me = new ModelEvent(nobj,this);
		for(ModelListener ml: m_listeners)
			ml.wasChanged(me);
	}
	
    //.......Externalizable.....................	
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    	super.readExternal(in);
    	components = new HashSet<INamedObject>();
    	registry = new HashMap<String, INamedObject>();
		m_listeners = new ArrayList<ModelListener>();
    	int size = in.readInt();
    	for(int i = 0; i < size; i++)
    		add((INamedObject)in.readObject());
    	fireEvents = true;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
    	super.writeExternal(out);
    	out.writeInt(components.size());
    	for(INamedObject nobj: components)
    		out.writeObject(nobj);
    }
    
    public void readCapture(ObjectInput in) throws IOException, ClassNotFoundException {
    	setName(in.readUTF());
    	registry = (Map<String, INamedObject>)in.readObject();
    	components = (Set<INamedObject>)in.readObject();
    	// restore component to model reference
    	Iterator<INamedObject> iter =  components.iterator();
    	while(iter.hasNext())
    		iter.next().setModel(this);
    	fireEvents = true;
    }
    
    public void writeCapture(ObjectOutput out) throws IOException {
    	out.writeUTF(getName());
    	out.writeObject(registry);
    	out.writeObject(components);    	
    }
    
    public void fromStream(BufferedReader br) throws Exception {
    	super.fromStream(br);
    	int numComp = Integer.parseInt(br.readLine());
    	for(int i = 0; i < numComp; i++){
    		Class _class = Class.forName(br.readLine());
    		IStreamable streamable = (IStreamable)_class.newInstance();
    		streamable.fromStream(br);
    		add(streamable);
    	}
    }
    
    public void toStream(PrintStream ps) {
    	super.toStream(ps);
    	Iterator iter = iterator(IStreamable.class);
    	int size = 0;
    	while(iter.hasNext()){
    		size++;
    		iter.next();
    	}
    	ps.println(size);
    	iter = components.iterator();
    	while(iter.hasNext()){
    		IStreamable streamable = (IStreamable)iter.next();
    		ps.println(streamable.getClass().getName());
    		streamable.toStream(ps);
    	}
    }
	
}
