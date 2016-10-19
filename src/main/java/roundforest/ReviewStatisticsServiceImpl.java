package roundforest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.aggregators.StatisticsAggregator;
import roundforest.model.SummaryStatistics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

public class ReviewStatisticsServiceImpl implements ReviewStatisticsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewStatisticsServiceImpl.class);
    private final ReviewsParser parser;
    private final ThreadPoolExecutor executorService;
    private final List<StatisticsAggregator> aggregators;

    public ReviewStatisticsServiceImpl(ReviewsParser parser, ThreadPoolExecutor executorService, List<StatisticsAggregator> aggregators) {
        this.parser = parser;
        this.executorService = executorService;
        this.aggregators = aggregators;
    }

    @Override
    public List<SummaryStatistics> calculateStats(InputStream in) throws IOException {
        LOGGER.info("Reading reviews from input stream");
        AtomicLong badLines = new AtomicLong();
        parser.parseStream(in,
                xs -> aggregators.forEach(a -> executorService.submit(() -> xs.forEach(a))),
                cells -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Can't parse data {} to review", Arrays.toString(cells));
                    }
                    badLines.incrementAndGet();
                });
        try {
            LOGGER.info("Stream reading completed, number of bad lines = {} waiting for processing", badLines.get());
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Waiting for completion interrupted");
        }

        List<SummaryStatistics> stats = aggregators
                .stream()
                .map(StatisticsAggregator::getStats).collect(toList());

        LOGGER.debug("Completed, statistics: {}", stats);
        return stats;
    }
}
