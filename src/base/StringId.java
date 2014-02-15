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

    public int hashCode() {
        return id.hashCode();
    }

    public int compareTo(StringId other) {
        return id.compareTo(other.getId());
    }
}
