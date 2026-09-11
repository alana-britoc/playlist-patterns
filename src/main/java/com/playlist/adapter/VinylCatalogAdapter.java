package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyCatalog;

  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null) {
      throw new IllegalArgumentException("legacyCatalog não pode ser null");
    }
    this.legacyCatalog = legacyCatalog;
  }

  @Override
  public List<Track> findAll() {
    List<Track> tracks = new ArrayList<>();
    for (String record : legacyCatalog.fetchAllRecords()) {
      parseRecord(record).ifPresent(tracks::add);
    }
    return tracks;
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.isBlank()) {
      return Optional.empty();
    }
    String record = legacyCatalog.findRecordByCatalogNumber(id);
    return record == null ? Optional.empty() : parseRecord(record);
  }

  private Optional<Track> parseRecord(String record) {
    if (record == null) {
      return Optional.empty();
    }
    String[] fields = record.split("\\|", -1);
    if (fields.length != 5) {
      return Optional.empty();
    }

    String id = fields[0].trim();
    String rawTitle = fields[1].trim().replaceAll("\\s+", " ");
    String rawArtist = fields[2].trim();
    String durationField = fields[3].trim();
    String premiumField = fields[4].trim();

    if (id.isEmpty() || rawTitle.isEmpty()) {
      return Optional.empty();
    }

    int durationMs;
    try {
      durationMs = Integer.parseInt(durationField);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
    if (durationMs < 0) {
      return Optional.empty();
    }

    String title = toTitleCase(rawTitle);
    String artist = parseArtist(rawArtist);
    int durationSeconds = durationMs / 1000;
    boolean premium = premiumField.equalsIgnoreCase("Y");

    return Optional.of(new Track(id, title, artist, durationSeconds, premium));
  }

  private String toTitleCase(String text) {
    String[] words = text.toLowerCase(Locale.ROOT).split(" ");
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
      if (word.isEmpty()) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
    }
    return sb.toString();
  }

  private String parseArtist(String rawArtist) {
    String[] parts = rawArtist.split(",", 2);
    if (parts.length != 2) {
      return toTitleCase(rawArtist.trim());
    }
    String surname = toTitleCase(parts[0].trim());
    String firstName = toTitleCase(parts[1].trim());
    return firstName + " " + surname;
  }
}