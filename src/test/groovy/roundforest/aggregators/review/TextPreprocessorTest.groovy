package roundforest.aggregators.review

import spock.lang.Specification

class TextPreprocessorTest extends Specification {
    def "Lower cases and strpis html tags"() {
        given:
        def processor = new TextPreprocessor()

        expect:
        processor.apply("A bcde f") == "a bcde f"
        processor.apply("<a href=\"http://url\">A bcde f</a>") == "a bcde f"
        processor.apply("</br>A bcde f") == "a bcde f"
    }
}
