package roundforest.aggregators.review;

import java.util.function.Function;

public class TextPreprocessor implements Function<String, String> {
    @Override
    public String apply(String s) {
        return s.replaceAll("<.*?>", "").toLowerCase();
    }
}
