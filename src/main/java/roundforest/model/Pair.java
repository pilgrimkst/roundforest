package roundforest.model;

import com.google.common.base.Objects;

public class Pair {
    public final String element;
    public final long count;

    public Pair(String element, long count) {
        this.element = element;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return count == pair.count &&
                Objects.equal(element, pair.element);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(element, count);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "element='" + element + '\'' +
                ", count=" + count +
                '}';
    }
}
