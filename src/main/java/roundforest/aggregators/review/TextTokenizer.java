package roundforest.aggregators.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextTokenizer implements Function<String, List<String>> {

    private final Function<String, String> textPreprocessor;
    private final Predicate<String> tokenFilter;

    public TextTokenizer(Function<String, String> textPreprocessor, Predicate<String> tokenFilter) {
        this.textPreprocessor = textPreprocessor;
        this.tokenFilter = tokenFilter;
    }

    @Override
    public List<String> apply(String s) {
        return Stream
                .of(s)
                .map(textPreprocessor)
                .map(x -> new ArrayList<>(Arrays.asList(x.split("[^\\w]"))))
                .flatMap(Collection::stream)
                .filter(tokenFilter)
                .collect(Collectors.toList());
    }

}
