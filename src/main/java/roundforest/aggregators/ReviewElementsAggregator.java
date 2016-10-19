package roundforest.aggregators;

import roundforest.model.Review;
import roundforest.model.SummaryStatistics;

import java.util.function.Function;

public class ReviewElementsAggregator implements StatisticsAggregator {
    private final Function<Review, String> stringExtractor;
    private final TopKElementsAggregator topKElementsAggregator;

    public ReviewElementsAggregator(Function<Review, String> stringExtractor, TopKElementsAggregator topKElementsAggregator) {
        this.stringExtractor = stringExtractor;
        this.topKElementsAggregator = topKElementsAggregator;
    }

    @Override
    public SummaryStatistics getStats() {

        return topKElementsAggregator.getStats();
    }

    @Override
    public void accept(Review review) {
        topKElementsAggregator.accept(stringExtractor.apply(review));
    }
}
