package roundforest.aggregators;

import roundforest.model.Review;
import roundforest.model.SummaryStatistics;

import java.util.function.Consumer;

public interface StatisticsAggregator extends Consumer<Review> {
    SummaryStatistics getStats();
}
