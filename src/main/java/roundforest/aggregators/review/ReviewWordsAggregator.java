package roundforest.aggregators.review;

import roundforest.aggregators.StatisticsAggregator;
import roundforest.aggregators.element.TopKElementsAggregator;
import roundforest.model.Review;
import roundforest.model.SummaryStatistics;

import java.util.List;
import java.util.function.Function;

public class ReviewWordsAggregator implements StatisticsAggregator {
    private final TopKElementsAggregator aggregator;

    private final Function<String, List<String>> tokenizer;

    public ReviewWordsAggregator(TopKElementsAggregator aggregator, Function<String, List<String>> tokenizer) {
        this.aggregator = aggregator;
        this.tokenizer = tokenizer;
    }

    @Override
    public SummaryStatistics getStats() {
        return aggregator.getStats();
    }

    @Override
    public void accept(Review review) {
        tokenizer
                .apply(review.review)
                .forEach(aggregator::accept);
    }
}
