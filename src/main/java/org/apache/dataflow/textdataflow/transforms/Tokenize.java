package org.apache.dataflow.textdataflow.transforms;

import static java.lang.Math.min;

import java.util.regex.Pattern;

import org.apache.dataflow.textdataflow.doc.ADoc;
import org.apache.dataflow.textdataflow.doc.TAnnotation;

import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.transforms.ParDo.Bound;

public class Tokenize {

    public static final String SENTENCE = "Sentence";
    public static final String TOKEN = "Token";

    public static class Text2ADoc extends DoFn<String, ADoc> {

        @Override
        public void processElement(ProcessContext c) {
            c.output(new ADoc(c.element()));
        }
    }

    public final static Bound<String, ADoc> TEXT2DOC = ParDo.named("Text2ADoc")
            .of(new Text2ADoc());

    public static class NaiveSentenceTokenizerFn extends DoFn<ADoc, ADoc> {

        @Override
        public void processElement(ProcessContext c) {

            ADoc doc = c.element();
            final String text = doc.getText();

            if (text.indexOf('.') == -1) {// no dots, return whole sentence
                doc.add(new TAnnotation(doc, SENTENCE, 0, text.length()));

            } else {
                int i = 0;
                for (String dotSpaceSplit : text.split("\\. ")) {
                    for (String sentenceText : dotSpaceSplit.split("\\.")) {
                        doc.add(new TAnnotation(doc, SENTENCE, i, //
                                // to account for last sentence not having a dot
                                min(text.length(),
                                        i + sentenceText.length() + 1)));
                        i += sentenceText.length() + 1;
                    }
                    i++;// to account for the space after the dot
                }
            }
            c.output(doc);
        }
    }

    public final static Bound<ADoc, ADoc> NAIVE_SENTENCE_TOKENIZER = ParDo
            .named("NaiveSentenceTokenizer").of(new NaiveSentenceTokenizerFn());

    public static class NaiveWordTokenizerFn extends DoFn<ADoc, ADoc> {

        /** Splits on any punctuation character, digits and case change. */
        public static final String patterPunctDigitsCamelcase = "(?<=\\w)(?=\\W)|(?<=\\W)(?=\\w)|(?<=[A-Z])(?=[a-z])|(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)";

        /** Splits on any punctuation character, @see tests */
        public static final String patterPunctuation = "(?<=\\w)(?=\\W)|(?<=\\W)(?=\\w)";

        /** Splits on any punctuation character, except dashes, @see tests */
        public static final String patterPunctuationNoDash = "(?<=[(a-zA-Z_0-9\\-)])(?=[^(a-zA-Z_0-9\\-)])|(?<=[^(a-zA-Z_0-9\\-)])(?=[(a-zA-Z_0-9\\-)])";

        // TODO add config
        private static Pattern tokenizationPattern = Pattern
                .compile(patterPunctuationNoDash);

        @Override
        public void processElement(ProcessContext c) {

            ADoc doc = c.element();

            for (TAnnotation sentence : doc.getAnnotations(SENTENCE)) {

                int start = sentence.getBegin();
                String text = sentence.getText();
                if (text.endsWith("."))// remove trailing dot
                    text = text.substring(0, text.length() - 1);

                for (String word : tokenizationPattern.split(text)) {
                    if (!word.equals(" ")) {
                        doc.add(new TAnnotation(doc, TOKEN, start,
                                start + word.length()));
                    }
                    start += word.length();
                }
            }
            c.output(doc);
        }
    }

    public final static Bound<ADoc, ADoc> NAIVE_WORD_TOKENIZER = ParDo
            .named("NaiveWordTokenizer").of(new NaiveWordTokenizerFn());
}
