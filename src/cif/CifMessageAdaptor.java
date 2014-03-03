package cif;

import base.Message;
import base.MessageId;

/**
 * Created by pascalpoizat on 17/02/2014.
 */
public class CifMessageAdaptor implements Message {

    // adapted
    private models.choreography.cif.generated.Message message;
    // own data
    private MessageId id;

    public CifMessageAdaptor(models.choreography.cif.generated.Message message) {
        this.message = message;
        this.id = new MessageId(message.getMsgID());
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
