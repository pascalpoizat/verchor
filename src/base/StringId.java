package base;

/**
 * Created by pascalpoizat on 13/02/2014.
 */
public class StringId {
    private String id;

    public StringId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean equals(StringId other) {
        return id.equals(other.getId());
    }
}
