package com.example.mymnist;

public interface Classifier {
    String name();

    Classification recognize(final float[] pixels);

}
