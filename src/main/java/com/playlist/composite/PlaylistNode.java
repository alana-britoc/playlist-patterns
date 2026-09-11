package com.playlist.composite;

import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistNode implements MediaItem {

  private final String name;
  private final List<MediaItem> children = new ArrayList<>();

  public PlaylistNode(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("nome não pode ser null ou em branco");
    }
    this.name = name;
  }

  public PlaylistNode add(MediaItem item) {
    if (item == null) {
      throw new IllegalArgumentException("item não pode ser null");
    }
    if (item == this) {
      throw new IllegalArgumentException("não é possível adicionar a própria playlist");
    }
    if (item instanceof PlaylistNode && ((PlaylistNode) item).contains(this)) {
      throw new IllegalArgumentException("adicionar esse item criaria um ciclo");
    }
    children.add(item);
    return this;
  }

  public boolean remove(MediaItem item) {
    return children.remove(item);
  }

  public List<MediaItem> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public boolean contains(MediaItem item) {
    for (MediaItem child : children) {
      if (child == item) {
        return true;
      }
      if (child instanceof PlaylistNode && ((PlaylistNode) child).contains(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDurationSeconds() {
    int total = 0;
    for (MediaItem child : children) {
      total += child.getDurationSeconds();
    }
    return total;
  }

  @Override
  public int getTrackCount() {
    int total = 0;
    for (MediaItem child : children) {
      total += child.getTrackCount();
    }
    return total;
  }

  @Override
  public List<Track> flatten() {
    List<Track> result = new ArrayList<>();
    for (MediaItem child : children) {
      result.addAll(child.flatten());
    }
    return result;
  }
}