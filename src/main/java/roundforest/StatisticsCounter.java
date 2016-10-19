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

        if (args.length == 0) {
            System.err.print("You need to provide path to the data as first argument");
            System.exit(-1);
        }

        String path = args[0];

        ReviewStatisticsService statisticsService = ApplicationConfiguration.reviewStatisticsService();
        List<SummaryStatistics> xs = statisticsService.calculateStats(new FileInputStream(path));
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
