package base;

/**
 * Created by pascalpoizat on 15/02/2014.
 */
public interface Message {

    public MessageId getId();

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();

}
