package roundforest;

import roundforest.model.SummaryStatistics;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ReviewStatisticsService {
    List<SummaryStatistics> calculateStats(InputStream in) throws IOException;
}
