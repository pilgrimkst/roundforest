package roundforest.aggregators

import roundforest.ReviewsParser
import spock.lang.Specification

class TopKElementsAggregatorTest extends Specification {
    def "should count reviews stats for given string extractor"() {
        given:
        def dataStream = getClass().getResourceAsStream("../../sample-reviews-100.csv")
        def parser = new ReviewsParser(10, ',' as char, '"' as char, '\\' as char, 1)
        def reviews = []

        def aggregator = new TopKElementsAggregator("productId", { r -> r.productId }, 3, 10, 2)

        when:
        parser.parseStream(dataStream, { l -> reviews.addAll(l) }, { x -> })

        reviews.forEach({ r -> aggregator.accept(r) })

        def stats = aggregator.stats
        print(stats)

        then:
        stats != null
        stats.topKElements.collect { s -> s.element }.containsAll(["B001EO5QW8", "B0019CW0HE", "B001GVISJM"])
    }
}
