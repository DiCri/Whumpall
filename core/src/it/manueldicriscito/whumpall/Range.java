package it.manueldicriscito.whumpall;


import java.util.Random;

public class Range {
    Random random = new Random();
    private float low;
    private float high;
    public Range(float low, float high) {
        this.low = low;
        this.high = high;
    }
    public boolean contains(int number) {
        return (number>=low && number<=high);
    }
    public float getRandom() {
        return low + random.nextFloat() * (high-low);
    }
    public static Range single(float number) {
        return new Range(number, number);
    }
    public static Range range(float low, float high) {
        return new Range(low, high);
    }

}
