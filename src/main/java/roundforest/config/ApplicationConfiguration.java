package roundforest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.ReviewStatisticsService;
import roundforest.ReviewStatisticsServiceImpl;
import roundforest.ReviewsParser;
import roundforest.aggregators.StatisticsAggregator;
import roundforest.aggregators.element.ReviewElementsAggregator;
import roundforest.aggregators.element.TopKElementsAggregator;
import roundforest.aggregators.review.EnglishStopWordsFilter;
import roundforest.aggregators.review.ReviewWordsAggregator;
import roundforest.aggregators.review.TextPreprocessor;
import roundforest.aggregators.review.TextTokenizer;
import roundforest.model.Review;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ApplicationConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);
    private static final int topNElements = 1000;
    private static final int MAX_JOBS_IN_QUEUE = 1000;
    private static final AtomicLong RESUBMITTED_JOBS_COUNTER = new AtomicLong();
    private static final int BATCH_SIZE = 100;
    private static int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static ReviewStatisticsService reviewStatisticsService() {
        ThreadPoolExecutor executorService = executorService();

        logStats(executorService);

        return new ReviewStatisticsServiceImpl(reviewsParser(), executorService, aggregators());
    }

    private static void logStats(ThreadPoolExecutor executorService) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            LOGGER.info("[job status] comleted={}\tjobsInQueue={}\tresubmittedJobsCounter={}", executorService.getCompletedTaskCount(), executorService.getQueue().size(), RESUBMITTED_JOBS_COUNTER.get());
        }, 10, 10, TimeUnit.SECONDS);
    }

    private static List<StatisticsAggregator> aggregators() {
        return newArrayList(
                elementsAggregator(r -> r.profileName, "profileName"),
                elementsAggregator(r -> r.productId, "productId"),
                reviewWordsAggregator()
        );
    }

    private static ReviewWordsAggregator reviewWordsAggregator() {
        return new ReviewWordsAggregator(topKElementsAggregator("review"), textTokenizer());
    }

    private static ReviewElementsAggregator elementsAggregator(Function<Review, String> extractor, String name) {
        return new ReviewElementsAggregator(extractor, topKElementsAggregator(name));
    }

    private static TopKElementsAggregator topKElementsAggregator(String name) {
        return new TopKElementsAggregator(name, topNElements, 10, NUMBER_OF_PROCESSORS);
    }

    private static TextTokenizer textTokenizer() {
        TextPreprocessor p = new TextPreprocessor();
        EnglishStopWordsFilter f = new EnglishStopWordsFilter();
        return new TextTokenizer(p, f);
    }

    private static ThreadPoolExecutor executorService() {
        return new ThreadPoolExecutor(NUMBER_OF_PROCESSORS, NUMBER_OF_PROCESSORS * 2,
                100L, MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_JOBS_IN_QUEUE), (r, executor) -> {
            try {
                LOGGER.debug("Job queue is full, waiting to push");
                RESUBMITTED_JOBS_COUNTER.incrementAndGet();
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private static ReviewsParser reviewsParser() {
        return new ReviewsParser(BATCH_SIZE, ',', '"', '\\', 1);
    }
}
