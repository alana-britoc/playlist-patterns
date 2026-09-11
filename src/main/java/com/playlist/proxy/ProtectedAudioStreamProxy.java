package com.playlist.proxy;

import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import java.util.Arrays;
import java.util.function.Supplier;

public class ProtectedAudioStreamProxy implements AudioStream {

  private final Track track;
  private final Subscription plan;
  private final Supplier<AudioStream> loader;

  private AudioStream realStream;
  private byte[] cachedBytes;

  public ProtectedAudioStreamProxy(Track track, Subscription plan, Supplier<AudioStream> loader) {
    if (track == null || plan == null || loader == null) {
      throw new IllegalArgumentException("argumentos não podem ser null");
    }
    this.track = track;
    this.plan = plan;
    this.loader = loader;
  }

  public ProtectedAudioStreamProxy(Track track, Subscription plan) {
    this(track, plan, () -> new RemoteAudioStream(track));
  }

  public boolean isLoaded() {
    return realStream != null;
  }

  @Override
  public String getTrackId() {
    return track.id();
  }

  @Override
  public byte[] readBytes() {
    if (cachedBytes != null) {
      return Arrays.copyOf(cachedBytes, cachedBytes.length);
    }
    if (track.premium() && plan == Subscription.FREE) {
      throw new AccessDeniedException("plano não permite ouvir esta faixa");
    }
    if (realStream == null) {
      realStream = loader.get();
    }
    cachedBytes = realStream.readBytes();
    return Arrays.copyOf(cachedBytes, cachedBytes.length);
  }
}