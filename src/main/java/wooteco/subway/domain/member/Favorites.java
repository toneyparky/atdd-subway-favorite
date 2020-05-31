package wooteco.subway.domain.member;

import wooteco.subway.domain.station.Stations;
import wooteco.subway.exception.NoFavoriteExistException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Favorites {
	private final Set<Favorite> favorites;

	public Favorites(Set<Favorite> favorites) {
		this.favorites = favorites;
	}

	public static Favorites empty() {
		return new Favorites(new HashSet<>());
	}

	public void add(Favorite favorite) {
		favorites.add(favorite);
	}

	public void remove(Favorite favorite) {
		if (!favorites.remove(favorite)) {
			throw new NoFavoriteExistException();
		}
	}

	public Set<Long> extractStationIds() {
		return favorites.stream()
				.map(favorite -> Arrays.asList(favorite.getSourceId(), favorite.getTargetId()))
				.flatMap(List::stream)
				.collect(Collectors.toSet());
	}

	public List<FavoriteDetail> toFavoriteDetails(Stations stations, long memberId) {
		return favorites.stream()
				.map(favorite -> FavoriteDetail.of(
						memberId,
						favorite,
						stations.extractStationById(favorite.getSourceId()).getName(),
						stations.extractStationById(favorite.getTargetId()).getName()))
				.collect(Collectors.toList());
	}

	public boolean hasFavoriteOf(long sourceId, long targetId) {
		return favorites.stream()
				.anyMatch(favorite -> favorite.hasSourceId(sourceId) && favorite.hasTargetId(targetId));
	}

	public Set<Favorite> getFavorites() {
		return favorites;
	}
}
