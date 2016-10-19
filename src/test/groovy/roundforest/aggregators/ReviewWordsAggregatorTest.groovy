package roundforest.aggregators

import roundforest.model.Review
import spock.lang.Specification

class ReviewWordsAggregatorTest extends Specification {
    def "should collect statistics on most used words in review"() {
        given:
        def review = new Review("P", "P_ID", "A b, B - b. a!")
        def agg = Mock(TopKElementsAggregator)
        def aggregator = new ReviewWordsAggregator(agg, { f -> f.toLowerCase() }, { t -> t.split("[^\\w]").toList() }, { f -> true })

        when:
        aggregator.accept(review)

        then:
        1 * agg.accept("a")
        1 * agg.accept("b")
        1 * agg.accept("b")
        1 * agg.accept("b")
        1 * agg.accept("a")

    }
}
