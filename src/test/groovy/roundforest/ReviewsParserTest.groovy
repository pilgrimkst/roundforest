package roundforest

import roundforest.model.Review
import spock.lang.Specification

import java.nio.charset.Charset
import java.util.function.Consumer

class ReviewsParserTest extends Specification {
    def "ParseStream: should parse stream of text lines to Reviews and pass it to reviews consumer"() {
        given:
        def stream = new ByteArrayInputStream("1,B001E4KFG0,A3SGXH7AUHU8GW,delmartian,1,1,5,1303862400,Good Quality Dog Food,I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.\n".getBytes(Charset.forName("UTF-8")))
        def parser = new ReviewsParser()
        def reviewConsumer = Mock(Consumer)
        def unparsedConsumer = Mock(Consumer)

        when:
        parser.parseStream(stream, reviewConsumer, unparsedConsumer)

        then:
        1 * reviewConsumer.accept(_)
        0 * unparsedConsumer.accept(_)

    }

    def "ParseStream: should pass all unparsed lines to unparsed consumer"() {
        given:
        def stream = new ByteArrayInputStream("1,1303862400,Bad line\n".getBytes(Charset.forName("UTF-8")))
        def parser = new ReviewsParser()
        def reviewConsumer = Mock(Consumer)
        def unparsedConsumer = Mock(Consumer)

        when:
        parser.parseStream(stream, reviewConsumer, unparsedConsumer)

        then:
        0 * reviewConsumer.accept(_)
        1 * unparsedConsumer.accept(_)

    }

    def "Parse: should extraxt Review from string[]"() {
        given:
        def cells = ["1", "B001E4KFG0", "A3SGXH7AUHU8GW", "delmartian", "1", "1", "5", "1303862400", "Good Quality Dog Food", "I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most."] as String[]

        when:
        def result = ReviewsParser.parse(cells)

        then:
        result.isPresent()
        result == Optional.of(new Review("delmartian", "B001E4KFG0", "I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most."))
    }

    def "Parse: should return empty if there inconsistent items in array"() {
        given:
        def cells = ["1", "B001E4KFG0", "A3SGXH7AUHU8GW", "delmartian", "1303862400", "Good Quality Dog Food", "y and she appreciates this product better than  most."] as String[]

        when:
        def result = ReviewsParser.parse(cells)

        then:
        !result.isPresent()
    }
}
