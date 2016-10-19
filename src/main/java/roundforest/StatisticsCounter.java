package roundforest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.config.ApplicationConfiguration;
import roundforest.model.SummaryStatistics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class StatisticsCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsCounter.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("Started with args {}", Arrays.toString(args));
        ReviewStatisticsService statisticsService = ApplicationConfiguration.reviewStatisticsService();
        List<SummaryStatistics> xs = statisticsService.calculateStats(new FileInputStream("/Users/pilgrim/Downloads/chrome/amazon-fine-foods/Reviews.csv"));
        LOGGER.info("Statistics calculation completed");
        xs.forEach(s -> print(s, System.out));

        System.exit(0);
    }

    private static void print(SummaryStatistics s, PrintStream out) {
        out.print("\n");
        out.println("---------- " + s.getName() + " -------------");
        s
                .getTopKElements()
                .forEach(e -> out.println(e.element + "\t" + e.count));

        out.print("\n----------------------------------\n");
    }
}
