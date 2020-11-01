package com.example.mymnist;

public class Classification {

    private float conf; //confidence; output of Softmax

    private String label; //predicted label (i.e. 1, 2, 3...)

    Classification() {
        this.label = null;
        this.conf = -1.0F;  //indicates not predicted;
    }

    void update(float conf, String label) {
        this.label = label;
        this.conf = conf;
    }

    public float getConf() {
        return conf;
    }


    public String getLabel() {
        return label;
    }
}
