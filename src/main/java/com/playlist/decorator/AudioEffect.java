package com.playlist.decorator;

public abstract class AudioEffect implements AudioTrack {

  protected final AudioTrack wrapped;

  protected AudioEffect(AudioTrack wrapped) {
    if (wrapped == null) {
      throw new IllegalArgumentException("wrapped não pode ser null");
    }
    this.wrapped = wrapped;
  }

  protected abstract String describe();

  @Override
  public String getTitle() {
    return wrapped.getTitle();
  }

  @Override
  public String getEffectChain() {
    return wrapped.getEffectChain() + " -> " + describe();
  }
}