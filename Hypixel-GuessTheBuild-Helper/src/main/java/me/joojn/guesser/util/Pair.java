package me.joojn.guesser.util;

import java.util.Objects;

public class Pair<K,V> {

    private final K key;
    private final V value;

    public K getKey()
    {
        return this.key;
    }

    public V getValue()
    {
        return this.value;
    }

    public Pair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + " = " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof Pair)
        {
            Pair pair = (Pair) o;
            return Objects.equals(pair.key, this.key) && Objects.equals(pair.value, this.value);
        }

        return false;
    }
}
