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

    public int compareTo(StringId other) {
        return id.compareTo(other.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringId stringId = (StringId) o;

        if (!id.equals(stringId.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
