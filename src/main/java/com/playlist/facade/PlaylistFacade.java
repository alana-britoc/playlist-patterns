package com.playlist.facade;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import com.playlist.proxy.ProtectedAudioStreamProxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaylistFacade {

  private final TrackCatalog catalog;
  private final Subscription plan;
  private final Map<String, ProtectedAudioStreamProxy> streams = new HashMap<>();

  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {
    if (catalog == null || plan == null) {
      throw new IllegalArgumentException("catalog e plan não podem ser nulos");
    }
    this.catalog = catalog;
    this.plan = plan;
  }

  public PlaylistNode buildLibrary(String name) {
    PlaylistNode library = new PlaylistNode(name);
    for (Track track : catalog.findAll()) {
      library.add(new TrackItem(track));
    }
    return library;
  }

  public byte[] listen(String trackId) {
    Track track = findTrackOrThrow(trackId);
    ProtectedAudioStreamProxy proxy = streams.computeIfAbsent(trackId,
            id -> new ProtectedAudioStreamProxy(track, plan));
    return proxy.readBytes();
  }

  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    Track track = findTrackOrThrow(trackId);
    byte[] bytes = listen(trackId);
    double[] samples = new double[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      samples[i] = bytes[i] / 128.0;
    }
    AudioTrack raw = new RawAudioTrack(track.title(), samples);
    AudioTrack withVolume = new VolumeEffect(raw, volume);
    return new FadeInEffect(withVolume, fadeInSamples);
  }

  private Track findTrackOrThrow(String trackId) {
    Optional<Track> track = catalog.findById(trackId);
    if (track.isEmpty()) {
      throw new TrackNotFoundException("faixa não encontrada: " + trackId);
    }
    return track.get();
  }
}