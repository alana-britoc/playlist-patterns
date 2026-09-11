package com.playlist.composite;

import com.playlist.core.Track;
import java.util.Collections;
import java.util.List;

public class TrackItem implements MediaItem {

  private final Track track;

  public TrackItem(Track track) {
    if (track == null) {
      throw new IllegalArgumentException("track não pode ser null");
    }
    this.track = track;
  }

  public Track getTrack() {
    return track;
  }

  @Override
  public String getName() {
    return track.title();
  }

  @Override
  public int getDurationSeconds() {
    return track.durationSeconds();
  }

  @Override
  public int getTrackCount() {
    return 1;
  }

  @Override
  public List<Track> flatten() {
    return Collections.singletonList(track);
  }
}