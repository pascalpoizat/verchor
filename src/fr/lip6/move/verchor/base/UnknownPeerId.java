package fr.lip6.move.verchor.base;

/**
 * Created by pascalpoizat on 05/02/2014.
 */
public class UnknownPeerId extends VerchorException {
    public static final String MESSAGE = "Unknown peer";

    public UnknownPeerId(String message) {
        super(message);
    }
}
