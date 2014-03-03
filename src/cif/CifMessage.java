package cif;

import base.Message;
import base.MessageId;

/**
 * Created by pascalpoizat on 03/03/2014.
 */
public class CifMessage implements Message {

    // own data
    private MessageId id;

    public CifMessage(String id) {
        this.id = new MessageId(id);
    }

    public MessageId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(this instanceof Message)) return false;

        Message that = (Message) o;

        if (!id.equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
