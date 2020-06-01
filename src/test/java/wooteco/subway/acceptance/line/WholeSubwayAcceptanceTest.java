package wooteco.subway.acceptance.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.service.line.dto.LineDetailResponse;
import wooteco.subway.service.line.dto.LineResponse;
import wooteco.subway.service.station.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WholeSubwayAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선도 전체 정보 조회")
    @Test
    public void wholeSubway() {
        // given : 노선이 존재한다.
        LineResponse lineResponse1 = createLine("2호선");
		// and : 역이 존재한다.
		StationResponse stationResponse1 = createStation("강남역");
		StationResponse stationResponse2 = createStation("역삼역");
		StationResponse stationResponse3 = createStation("삼성역");
		// and : 구간이 존재한다.
		addLineStation(lineResponse1.getId(), null, stationResponse1.getId());
		addLineStation(lineResponse1.getId(), stationResponse1.getId(), stationResponse2.getId());
		addLineStation(lineResponse1.getId(), stationResponse2.getId(), stationResponse3.getId());

		// and : 노선이 존재한다.
		LineResponse lineResponse2 = createLine("신분당선");

		// and : 역이 존재한다.
		StationResponse stationResponse5 = createStation("양재역");
		StationResponse stationResponse6 = createStation("양재시민의숲역");

		// and : 구간이 존재한다.
		addLineStation(lineResponse2.getId(), null, stationResponse1.getId());
		addLineStation(lineResponse2.getId(), stationResponse1.getId(), stationResponse5.getId());
		addLineStation(lineResponse2.getId(), stationResponse5.getId(), stationResponse6.getId());

		// then : 전체 노선을 조회한다.
		List<LineDetailResponse> response = retrieveWholeSubway().getLineDetailResponse();
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0).getStations().size()).isEqualTo(3);
		assertThat(response.get(1).getStations().size()).isEqualTo(3);
	}
}
