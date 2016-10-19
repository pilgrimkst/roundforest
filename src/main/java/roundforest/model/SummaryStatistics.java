package roundforest.model;

import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

public class SummaryStatistics {
    private String name;
    private List<Pair> topKElements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair> getTopKElements() {
        return topKElements;
    }

    public void setTopKElements(List<Pair> topKElements) {
        this.topKElements = topKElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryStatistics that = (SummaryStatistics) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(topKElements, that.topKElements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, topKElements);
    }

    @Override
    public String toString() {
        return "SummaryStatistics{" +
                "name='" + name + '\'' +
                ", topKElements=" + topKElements +
                '}';
    }
}
