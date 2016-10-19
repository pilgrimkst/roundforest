package roundforest.aggregators;

import roundforest.model.Review;
import roundforest.model.SummaryStatistics;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReviewWordsAggregator implements StatisticsAggregator {
    private final TopKElementsAggregator aggregator;
    private final Function<String, String> textPreprocessor;
    private final Function<String, List<String>> tokenizer;
    private final Predicate<String> tokenFilter;

    public ReviewWordsAggregator(TopKElementsAggregator aggregator, Function<String, String> textPreprocessor, Function<String, List<String>> tokenizer, Predicate<String> tokenFilter) {
        this.aggregator = aggregator;
        this.textPreprocessor = textPreprocessor;
        this.tokenizer = tokenizer;
        this.tokenFilter = tokenFilter;
    }

    @Override
    public SummaryStatistics getStats() {
        return aggregator.getStats();
    }

    @Override
    public void accept(Review review) {
        Stream
                .of(review.review)
                .map(textPreprocessor)
                .map(tokenizer)
                .flatMap(Collection::stream)
                .filter(tokenFilter)
                .forEach(aggregator::accept);
    }
}
