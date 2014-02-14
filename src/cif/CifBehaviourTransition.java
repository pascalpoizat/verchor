package cif;

import base.Transition;

/**
 * Created by pascalpoizat on 13/02/2014.
 */
public class CifBehaviourTransition implements Transition {

    private CifBehaviourState source;
    private CifBehaviourState target;

    public CifBehaviourTransition(CifBehaviourState source, CifBehaviourState target) {
        this.source = source;
        this.target = target;
    }
}
