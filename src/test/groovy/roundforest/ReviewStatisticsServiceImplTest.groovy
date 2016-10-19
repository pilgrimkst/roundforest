package roundforest

import roundforest.model.SummaryStatistics
import spock.lang.Specification

import java.util.concurrent.Executors

class ReviewStatisticsServiceImplTest extends Specification {

    public static final int NUMBER_OF_REVIEWS_IN_SAMPLE = 100

    def "CalculateStats: should parse data from stream, and process reviews via aggreagtors"() {
        given:
        def dataStream = getClass().getResourceAsStream("../sample-reviews-100.csv")
        def executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        def parser = new ReviewsParser(10, ',' as char, '"' as char, '\\' as char, 1)
        def aggregator = Mock(StatisticsAggregator)
        def service = new ReviewStatisticsServiceImpl(parser, executor, [aggregator])

        def stats = Mock(SummaryStatistics)
        when:
        def result = service.calculateStats(dataStream)

        then:
        NUMBER_OF_REVIEWS_IN_SAMPLE * aggregator.accept(_)

        then:
        1 * aggregator.getStats() >> stats

        then:
        result == [stats]

    }
}
