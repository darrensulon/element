package fem.model.output;

import fem.model.FemModel;

/**
 * Interface which contains specific methods required to record components in the model. 
 * Components are recorded so that they can later be drawn in the graphical display output.
 * 
 * @author Darren Sulon
 *
 */
public interface Recorder {

	public void record(FemModel model);
	
}
