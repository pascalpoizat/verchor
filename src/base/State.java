package base;

import java.util.List;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface State {

    public List<Transition> getIncomingTransitions();

    public List<Transition> getOutgoingTransitions();
}
