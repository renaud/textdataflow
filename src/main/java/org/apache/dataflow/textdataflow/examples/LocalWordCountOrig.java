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

public class LocalWordCountOrig {

    public static void main(String[] args) {
        
     // Create and set our Pipeline Options.
        PipelineOptions options = PipelineOptionsFactory.create();

        // For local mode, you do not need to set the runner
        // since the DirectPipelineRunner is already the default.

        // Create the Pipeline with the specified options.
        Pipeline p = Pipeline.create(options);


        // Apply the pipeline's transforms.

        // Concept #1: Apply a root transform to the pipeline; in this case, TextIO.Read to read a set
        // of input text files. TextIO.Read returns a PCollection where each element is one line from
        // the input text (a set of Shakespeare's texts).
        p.apply(TextIO.Read.from("src/test/resources/alice.txt"))
         // Concept #2: Apply a ParDo transform to our PCollection of text lines. This ParDo invokes a
         // DoFn (defined in-line) on each element that tokenizes the text line into individual words.
         // The ParDo returns a PCollection<String>, where each element is an individual word in
         // Shakespeare's collected texts.
         .apply(ParDo.named("ExtractWords").of(new DoFn<String, String>() {
                           @Override
                           public void processElement(ProcessContext c) {
                             for (String word : c.element().split("[^a-zA-Z']+")) {
                               if (!word.isEmpty()) {
                                 c.output(word);
                               }
                             }
                           }
                         }))
         // Concept #3: Apply the Count transform to our PCollection of individual words. The Count
         // transform returns a new PCollection of key/value pairs, where each key represents a unique
         // word in the text. The associated value is the occurrence count for that word.
         .apply(Count.<String>perElement())
         // Apply a MapElements transform that formats our PCollection of word counts into a printable
         // string, suitable for writing to an output file.
         .apply("FormatResults", MapElements.via(new SimpleFunction<KV<String, Long>, String>() {
                           @Override
                           public String apply(KV<String, Long> input) {
                             return input.getKey() + ": " + input.getValue();
                           }
                         }))
         // Concept #4: Apply a write transform, TextIO.Write, at the end of the pipeline.
         // TextIO.Write writes the contents of a PCollection (in this case, our PCollection of
         // formatted strings) to a series of text files in Google Cloud Storage.
         // CHANGE 3/3: The Google Cloud Storage path is required for outputting the results to.
         .apply(TextIO.Write.to("src/test/resources/alice.txt"));

        // Run the pipeline.
        p.run();
    }
}
