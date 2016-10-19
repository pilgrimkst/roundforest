package roundforest.aggregators;

import com.clearspring.analytics.stream.Counter;
import com.clearspring.analytics.stream.StreamSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.model.Pair;
import roundforest.model.Review;
import roundforest.model.SummaryStatistics;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

@ThreadSafe
public class TopKElementsAggregator implements StatisticsAggregator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopKElementsAggregator.class);
    private final String name;
    private final Function<Review, String> elementExtractor;
    private final List<StreamSummary<String>> topk;
    private final int topNElements;
    private final int concurrencyFactor;
    private final List<ReadWriteLock> locks;

    public TopKElementsAggregator(String name, Function<Review, String> elementExtractor, int topNElements, int capacityFactor, int concurrencyFactor) {
        this.name = name;
        this.elementExtractor = elementExtractor;
        this.concurrencyFactor = concurrencyFactor;
        this.topNElements = topNElements;

        locks = new ArrayList<>(concurrencyFactor);
        topk = new ArrayList<>(concurrencyFactor);
        for (int i = 0; i < concurrencyFactor; i++) {
            locks.add(new ReentrantReadWriteLock());
            topk.add(new StreamSummary<>(topNElements * capacityFactor));
        }
    }

    @Override
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

    @Override
    public void accept(Review review) {
        String s = elementExtractor.apply(review);
        int hash = Math.abs(s.hashCode() % concurrencyFactor);
        LOGGER.trace("[accept review] review={}\tstring={}\thash={}", review, s, hash);
        ReadWriteLock l = locks.get(hash);
        try {
            l.writeLock().lock();
            topk.get(hash).offer(s);
        } finally {
            l.writeLock().unlock();
        }
    }

}
