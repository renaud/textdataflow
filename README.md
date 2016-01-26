# TextDataflow


An experiment in performing UIMA-like text processing with Dataflow

Simplifications over UIMA

* no typesystem
* only Maven for packaging
* no support for C++ or other runtimes

Example Pipeline (LocalFlow)

        Pipeline p = Pipeline.create(PipelineOptionsFactory.create());

        p.apply(TextIO.Read.from("src/test/resources/alice_oneline.txt"))//

                .apply(Tokenize.TEXT2DOC)//

                .apply(Tokenize.NAIVE_SENTENCE_TOKENIZER)//
                .apply(Tokenize.NAIVE_WORD_TOKENIZER)//

                .apply(TextIO.Write.to("src/test/resources/LocalFlow.txt"));
        ;
        p.run();

Example Transformer (Tokenize.NAIVE_WORD_TOKENIZER)

    public static class NaiveWordTokenizerFn extends DoFn<ADoc, ADoc> {

        private static Pattern tokenizationPattern = Pattern.compile("(?<=\\w)(?=\\W)|(?<=\\W)(?=\\w)");

        @Override
        public void processElement(ProcessContext c) {

            ADoc doc =  new ADoc(c.element());

            for (TAnnotation sentence : doc.getAnnotations(SENTENCE)) {

                int start = sentence.getBegin();
                String text = sentence.getText(doc);
                if (text.endsWith("."))// remove trailing dot
                    text = text.substring(0, text.length() - 1);

                for (String word : tokenizationPattern.split(text)) {
                    if (!word.equals(" ")) {
                        doc.add(new TAnnotation(TOKEN, start,
                                start + word.length()));
                    }
                    start += word.length();
                }
            }
            c.output(doc);
        }
    }

Future plans

* support Ruta-like rules
* evaluate performance at scale (now copying ADoc over every time :-( )


