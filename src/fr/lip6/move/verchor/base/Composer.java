package fr.lip6.move.verchor.base;

import java.util.List;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public interface Composer {

    public Behaviour compose(List<Behaviour> behaviourList);
}
