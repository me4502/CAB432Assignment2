package com.me4502.cab432.sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.PropertiesUtils;
import twitter4j.Status;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class SentimentConnector {

    private StanfordCoreNLP coreNLP;

    public SentimentConnector() {
        coreNLP = new StanfordCoreNLP(PropertiesUtils.asProperties("annotators", "tokenize, ssplit, parse, sentiment"));
    }

    public OptionalInt getSentiment(String message) {
        return coreNLP.process(message).get(CoreAnnotations.SentencesAnnotation.class).parallelStream()
                .max(Comparator.comparing(Object::toString)).stream()
                .map(sent -> sent.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class))
                .map(RNNCoreAnnotations::getPredictedClass)
                .mapToInt(i -> i)
                .findFirst();
    }

    public List<Map<String, String>> getAllSentiment(List<Status> messages) {
        return messages.parallelStream()
                .map(status -> Map.of(
                        "time", String.valueOf(status.getCreatedAt().toInstant().getEpochSecond()),
                        "sentiment", String.valueOf(getSentiment(status.getText()).orElse(0))
                ))
                .collect(Collectors.toList());
    }
}
