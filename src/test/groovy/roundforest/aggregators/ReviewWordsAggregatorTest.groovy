package roundforest.aggregators

import roundforest.aggregators.element.TopKElementsAggregator
import roundforest.aggregators.review.ReviewWordsAggregator
import roundforest.model.Review
import spock.lang.Specification

class ReviewWordsAggregatorTest extends Specification {
    def "should collect statistics on most used words in review"() {
        given:
        def review = new Review("P", "P_ID", "A b, B - b. a!")
        def agg = Mock(TopKElementsAggregator)
        def aggregator = new ReviewWordsAggregator(agg, { t -> t.split("[^\\w]").toList() })

        when:
        aggregator.accept(review)

        then:
        1 * agg.accept("A")
        1 * agg.accept("b")
        1 * agg.accept("B")
        1 * agg.accept("b")
        1 * agg.accept("a")

    }
}
