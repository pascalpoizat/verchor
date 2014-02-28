package cif;

import base.Message;
import base.MessageId;

/**
 * Created by pascalpoizat on 17/02/2014.
 */
public class CifMessage implements Message {

    // adapted
    private models.choreography.cif.generated.Message message;
    // own data
    private MessageId id;

    public CifMessage(models.choreography.cif.generated.Message message) {
        this.message = message;
        this.id = new MessageId(message.getMsgID());
    }

    public MessageId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CifMessage that = (CifMessage) o;

        if (!id.equals(that.id)) return false;

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
