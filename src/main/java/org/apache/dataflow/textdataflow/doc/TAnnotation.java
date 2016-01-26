package org.apache.dataflow.textdataflow.doc;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.dataflow.sdk.coders.AvroCoder;
import com.google.cloud.dataflow.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
public class TAnnotation {
    public static final String DEFAULT_TYPE = "annotation";

    private int begin, end;
    private String type;
    private Map<String, Object> meta = new HashMap<>();

    public TAnnotation() {
        setType(DEFAULT_TYPE);
    }

    public TAnnotation(String type, int begin, int end) {
        this.type = type;
        this.begin = begin;
        this.end = end;
    }

    // BEGIN / END

    public int getBegin() {
        return begin;
    }

    public TAnnotation setBegin(int begin) {
        this.begin = begin;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public TAnnotation setEnd(int end) {
        this.end = end;
        return this;
    }

    // META

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Object getMeta(String field) {
        return meta.get(field);
    }

    public TAnnotation setMeta(String field, Object value) {
        meta.put(field, value);
        return this;
    }

    // TEXT (get only)

    public String getText(ADoc doc) {
        return doc.getText().substring(begin, end);
    }

    // TYPE

    public String getType() {
        return type;
    }

    public TAnnotation setType(String type) {
        this.type = type;
        return this;
    }

    // UTILS

    @Override
    public String toString() {
        return type + "[" + begin + ":" + end + "]";
    }
}
