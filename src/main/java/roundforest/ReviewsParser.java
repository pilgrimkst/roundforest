package roundforest;

import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.model.Review;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ReviewsParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewsParser.class);

    private final int batchSize;
    private final char separator;
    private final char quotechar;
    private final char escape;
    private final int line;

    public ReviewsParser(int batchSize, char separator, char quotechar, char escape, int line) {
        this.batchSize = batchSize;
        this.separator = separator;
        this.quotechar = quotechar;
        this.escape = escape;
        this.line = line;
    }

    /**
     * Id,ProductId,UserId,ProfileName,HelpfulnessNumerator,HelpfulnessDenominator,Score,Time,Summary,Text
     * 1,B001E4KFG0,A3SGXH7AUHU8GW,delmartian,1,1,5,1303862400,Good Quality Dog Food,I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.
     */
    public void parseStream(InputStream in, Consumer<List<Review>> reviewsConsumer, Consumer<String[]> unparsed) throws IOException {
        LOGGER.info("Working with input stream...");
        CSVReader reader = new CSVReader(new InputStreamReader(in), separator, quotechar, escape, line);
        String[] nextLine;
        List<Review> reviews = new ArrayList<>(batchSize);
        while ((nextLine = reader.readNext()) != null) {
            Optional<Review> review = parse(nextLine);
            if (review.isPresent()) {
                LOGGER.trace("[review parsed]\t{}", review);
                reviews.add(review.get());
            } else {
                LOGGER.debug("[review unparsed]\t{}", Arrays.toString(nextLine));
                unparsed.accept(nextLine);
            }

            if (reviews.size() >= batchSize) {
                LOGGER.debug("[flush reviews to consumer]");
                reviewsConsumer.accept(reviews);
                reviews = new ArrayList<>(batchSize);
            }
        }

        if (reviews.size() > 0) {
            LOGGER.debug("[flush remaining reviews to consumer]");
            reviewsConsumer.accept(reviews);
        }

        LOGGER.info("Stream processed successfully");
    }

    static Optional<Review> parse(String[] data) {
        return data.length == 10 ?
                Optional.of(new Review(data[3], data[1], data[9])) :
                Optional.empty();
    }


}
