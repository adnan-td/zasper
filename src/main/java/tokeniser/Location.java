package tokeniser;

public class Location {
  public int start;
  public int end;

  public Location(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public Location() {
  }

  public void set_location(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public void set_location(Location location) {
    this.start = location.start;
    this.end = location.end;
  }

  public void set_start(int start) {
    this.start = start;
  }

  public void set_end(int end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return String.format("Location: { start: \"%d\", end: \"%d\" }", start, end);
  }
}
