package roundforest.aggregators.element;

import com.clearspring.analytics.stream.Counter;
import com.clearspring.analytics.stream.StreamSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.model.Pair;
import roundforest.model.SummaryStatistics;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public class TopKElementsAggregator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopKElementsAggregator.class);
    private final String name;
    private final List<StreamSummary<String>> topk;
    private final int topNElements;
    private final int concurrencyFactor;
    private final List<ReadWriteLock> locks;

    public TopKElementsAggregator(String name, int topNElements, int capacityFactor, int concurrencyFactor) {
        this.name = name;
        this.concurrencyFactor = concurrencyFactor;
        this.topNElements = topNElements;

        locks = new ArrayList<>(concurrencyFactor);
        topk = new ArrayList<>(concurrencyFactor);
        for (int i = 0; i < concurrencyFactor; i++) {
            locks.add(new ReentrantReadWriteLock());
            topk.add(new StreamSummary<>(topNElements * capacityFactor));
        }
    }

    public SummaryStatistics getStats() {
        PriorityQueue<Pair> data = new PriorityQueue<>(topNElements * concurrencyFactor, Comparator.comparingLong(x -> -1 * x.count));
        for (int i = 0; i < concurrencyFactor; i++) {
            try {
                locks.get(i).readLock().lock();
                List<Counter<String>> chunk = topk.get(i).topK(topNElements);
                chunk.forEach((e) -> data.offer(new Pair(e.getItem(), e.getCount())));
            } finally {
                locks.get(i).readLock().unlock();
            }
        }

        List<Pair> stats = new ArrayList<>(topNElements);
        for (int i = 0; i < topNElements && data.peek() != null; i++) {
            Pair p = data.poll();
            stats.add(p);
        }

        SummaryStatistics s = new SummaryStatistics();
        s.setName(name);
        s.setTopKElements(stats);
        return s;
    }

    public void accept(String element) {
        int hash = Math.abs(element.hashCode() % concurrencyFactor);
        LOGGER.trace("[accept review]\tstring={}\thash={}", element, hash);
        ReadWriteLock l = locks.get(hash);
        try {
            l.writeLock().lock();
            topk.get(hash).offer(element);
        } finally {
            l.writeLock().unlock();
        }
    }

}
