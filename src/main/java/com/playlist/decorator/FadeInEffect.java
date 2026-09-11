package com.playlist.decorator;

public final class FadeInEffect extends AudioEffect {

  private final int sampleCount;

  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    this.sampleCount = sampleCount;
  }

  @Override
  protected String describe() {
    return "fadeIn(" + sampleCount + ")";
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] result = new double[original.length];
    for (int i = 0; i < original.length; i++) {
      result[i] = (sampleCount > 0 && i < sampleCount)
              ? original[i] * ((double) i / sampleCount)
              : original[i];
    }
    return result;
  }
}