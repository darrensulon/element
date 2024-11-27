package util.model;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;


public abstract class NamedObject implements Externalizable, INamedObject, IStreamable{
	private static final long serialVersionUID = 1L;
	private String name;
	private NamedModel model;
	
    //.......Constructors.......................
    public NamedObject(String name){
        this.name = name;
    }
    public NamedObject() {
    }
    //.......Methods............................
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;  
    }
    public NamedModel getModel(){
    	return model;
    }
    public void setModel(NamedModel model){
    	this.model = model;
    }
    public String toString() {
        return name; // +"("+getClass().getName()+")";
    }
    
    protected void _notifyWasChanged(){
    	if(model == null)
    		return;
    	model.fireChangeEvent(this);
    }
    
    //.......Comparable.........................
    public int compareTo(Object obj) {
        if(NamedObject.class.isInstance(obj)) {
            return getName().compareTo(((NamedObject)obj).getName());
        }
        return 0;
    }

    //.......Externalizable.....................
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
    }
    public void writeExternal(ObjectOutput out) throws IOException {
    	out.writeUTF(name);
    }
    
    public void fromStream(BufferedReader br) throws Exception {
    	name = br.readLine();
    }
    
    public void toStream(PrintStream ps) {
    	ps.println(name);
    }
    
}
