package util.model;

public interface ModelListener {

	public void wasAdded(ModelEvent me);
	public void wasChanged(ModelEvent me);
	public void wasRemoved(ModelEvent me);
	
}
