package wooteco.subway.domain.line;

import wooteco.subway.exception.NoLineStationExistsException;

import java.util.*;

public class LineStations {
    private final Set<LineStation> stations;

    public LineStations(Set<LineStation> stations) {
        this.stations = stations;
    }

    public static LineStations of(List<LineStation> lines) {
        return new LineStations(new HashSet<>(lines));
    }

    public static LineStations empty() {
        return new LineStations(new HashSet<>());
    }

    public LineStation findLineStation(Long preStationId, Long stationId) {
        return stations.stream()
                .filter(lineStation -> lineStation.isLineStationOf(preStationId, stationId))
                .findFirst()
                .orElseThrow(NoLineStationExistsException::new);
    }

    public Set<LineStation> getStations() {
        return stations;
    }

    public void add(LineStation targetLineStation) {
        updatePreStationOfNextLineStation(targetLineStation.getPreStationId(), targetLineStation.getStationId());
        stations.add(targetLineStation);
    }

    private void remove(LineStation targetLineStation) {
        updatePreStationOfNextLineStation(targetLineStation.getStationId(), targetLineStation.getPreStationId());
        stations.remove(targetLineStation);
    }

    public void removeById(Long targetStationId) {
        extractByStationId(targetStationId)
                .ifPresent(this::remove);
    }

    public List<Long> getStationIds() {
        List<Long> result = new ArrayList<>();
        extractNext(null, result);
        return result;
    }

    private void extractNext(Long preStationId, List<Long> ids) {
        stations.stream()
                .filter(lineStation -> Objects.equals(lineStation.getPreStationId(), preStationId))
                .findFirst()
                .ifPresent(lineStation -> {
                    Long nextStationId = lineStation.getStationId();
                    ids.add(nextStationId);
                    extractNext(nextStationId, ids);
                });
    }

    private void updatePreStationOfNextLineStation(Long targetStationId, Long newPreStationId) {
        extractByPreStationId(targetStationId)
                .ifPresent(lineStation -> lineStation.updatePreLineStation(newPreStationId));
    }

    private Optional<LineStation> extractByStationId(Long stationId) {
        return stations.stream()
                .filter(lineStation -> Objects.equals(lineStation.getStationId(), stationId))
                .findFirst();
    }

    private Optional<LineStation> extractByPreStationId(Long preStationId) {
        return stations.stream()
                .filter(lineStation -> Objects.equals(lineStation.getPreStationId(), preStationId))
                .findFirst();
    }

    public int extractShortestDistance() {
        return stations.stream()
                .mapToInt(LineStation::getDistance)
                .sum();
    }

    public int extractShortestDuration() {
        return stations.stream()
                .mapToInt(LineStation::getDuration)
                .sum();
    }
}
