package roundforest;

import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roundforest.model.Review;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public class ReviewsParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewsParser.class);

    /**
     * Id,ProductId,UserId,ProfileName,HelpfulnessNumerator,HelpfulnessDenominator,Score,Time,Summary,Text
     * 1,B001E4KFG0,A3SGXH7AUHU8GW,delmartian,1,1,5,1303862400,Good Quality Dog Food,I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.
     */
    public void parseStream(InputStream in, Consumer<Optional<Review>> reviewsConsumer, Consumer<String[]> unparsed) throws IOException {
        LOGGER.info("Working with input stream...");
        CSVReader reader = new CSVReader(new InputStreamReader(in));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            Optional<Review> review = parse(nextLine);
            if (review.isPresent()) {
                LOGGER.trace("[review parsed]\t{}", review);
                reviewsConsumer.accept(review);
            } else {
                LOGGER.debug("[review unparsed]\t{}", Arrays.toString(nextLine));
                unparsed.accept(nextLine);
            }
        }

        LOGGER.info("Stream processed successfully");
    }

    static Optional<Review> parse(String[] data) {
        return data.length == 10 ?
                Optional.of(new Review(data[3], data[1], data[9])) :
                Optional.empty();
    }
}
