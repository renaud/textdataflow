# TextDataflow


An experiment in performing UIMA-like text processing with Dataflow

Simplifications over UIMA

* no typesystem
* only Maven for packaging
* no support for C++ or other runtimes

Example

        Pipeline p = Pipeline.create(PipelineOptionsFactory.create());

        p.apply(TextIO.Read.from("src/test/resources/alice_oneline.txt"))//

                .apply(Tokenize.TEXT2DOC)//

                .apply(Tokenize.NAIVE_SENTENCE_TOKENIZER)//
                .apply(Tokenize.NAIVE_WORD_TOKENIZER)//

                .apply(TextIO.Write.to("src/test/resources/LocalFlow.txt"));
        ;
        p.run();



Future plans

* support Ruta-like rules
* evaluate performance at scale (now copying ADoc over every time :-( )


