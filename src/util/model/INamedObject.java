
package util.model;

public interface INamedObject extends Comparable {
	
    public String getName();
    
    public void setName(String name);
    
    public NamedModel getModel();
    
    public void setModel(NamedModel model);
    
}