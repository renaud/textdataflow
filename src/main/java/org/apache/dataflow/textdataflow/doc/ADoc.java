package org.apache.dataflow.textdataflow.doc;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.dataflow.sdk.coders.AvroCoder;
import com.google.cloud.dataflow.sdk.coders.DefaultCoder;

/**
 * 
 * Annotated document. Similar to UIMA CAS (common annotation structure)
 * 
 * @author renaud@apache.org
 */
@DefaultCoder(AvroCoder.class)
public class ADoc {

    private String text;
    private List<TAnnotation> annotations = new LinkedList<>();

    public ADoc() {
    }

    public ADoc(String text) {
        this.text = text;
    }

    // TEXT

    public String getText() {
        return text;
    }

    /**
     * WARNING, this will have a SERIOUS impact on all existing annotations (you
     * NEED to update them)
     * 
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    // ANNOTATIONS

    public ADoc add(TAnnotation a) {
        annotations.add(a);
        return this;
    }

    public List<TAnnotation> getAnnotations() {
        return annotations;
    }

    public List<TAnnotation> getAnnotations(String type) {
        return annotations.stream()//
                .filter(a -> a.getType().equals(type))
                .collect(Collectors.toList());
    }
}
