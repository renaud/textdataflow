package org.apache.dataflow.textdataflow.examples;

import org.apache.dataflow.textdataflow.doc.ADoc;
import org.apache.dataflow.textdataflow.transforms.Tokenize;
import org.apache.dataflow.textdataflow.transforms.Tokenize.Text2ADoc;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.MapElements;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.transforms.SimpleFunction;

public class LocalFlow {

    public static void main(String[] args) {

        PipelineOptions options = PipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);

        p.apply(TextIO.Read.from("src/test/resources/alice.txt"))//

                .apply(ParDo.named("ExtractWords").of(new Text2ADoc()))//

                .apply(Tokenize.NAIVE_SENTENCE_TOKENIZER)//
               // .apply(Tokenize.NAIVE_WORD_TOKENIZER)//

                .apply("FormatResults",
                        MapElements.via(new SimpleFunction<ADoc, String>() {
                            @Override
                            public String apply(ADoc input) {
//                                return input.getAnnotations(Tokenize.TOKEN)
//                                        .get(2).toString();
                                return input.getText();
                            }
                        }))
                .apply(TextIO.Write.to("src/test/resources/LocalFlow.txt"));
        ;

        p.run();
    }
}
