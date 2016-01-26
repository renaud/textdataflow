package org.apache.dataflow.textdataflow.examples;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.Count;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.MapElements;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.transforms.SimpleFunction;
import com.google.cloud.dataflow.sdk.values.KV;

public class LocalWordCount {

    public static void main(String[] args) {

        PipelineOptions options = PipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(options);

        p.apply(TextIO.Read.from("src/test/resources/alice.txt"))//
                .apply(ParDo.named("ExtractWords")
                        .of(new DoFn<String, String>() {
                            @Override
                            public void processElement(ProcessContext c) {
                                for (String word : c.element()
                                        .split("[^a-zA-Z']+")) {
                                    if (!word.isEmpty()) {
                                        c.output(word);
                                    }
                                }
                            }
                        }))//
                .apply(Count.<String> perElement())
                .apply("FormatResults", MapElements
                        .via(new SimpleFunction<KV<String, Long>, String>() {
                            @Override
                            public String apply(KV<String, Long> input) {
                                return input.getKey() + ": " + input.getValue();
                            }
                        }))
                .apply(TextIO.Write.to("src/test/resources/alice_LocalWordCount.txt"));

        p.run();
    }
}
