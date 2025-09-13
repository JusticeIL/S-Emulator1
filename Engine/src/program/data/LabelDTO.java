package program.data;

import java.util.Objects;

public class LabelDTO implements Searchable {
    private final String name;

    public LabelDTO(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LabelDTO labelDTO = (LabelDTO) o;
        return Objects.equals(name, labelDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
