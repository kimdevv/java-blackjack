package blackjack.model.player;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import blackjack.model.game.BettedMoney;
import org.junit.jupiter.api.Test;

public class ParticipantsTest {

    @Test
    void 참가자가_최소_2명_최대_8명이_아닌_경우_예외가_발생한다() {
        // given

        // when & then
        assertThatThrownBy(() -> new Participants(List.of(
                new Participant(new PlayerName("한스1")),
                new Participant(new PlayerName("한스2")),
                new Participant(new PlayerName("한스3")),
                new Participant(new PlayerName("한스4")),
                new Participant(new PlayerName("한스5")),
                new Participant(new PlayerName("한스6")),
                new Participant(new PlayerName("한스7")),
                new Participant(new PlayerName("한스8")),
                new Participant(new PlayerName("한스9"))
        ))).hasMessage("참가자는 2~8명 이여야 합니다.");
    }

    @Test
    void 중복된_이름이_있으면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> new Participants(List.of(
                new Participant(new PlayerName("프리")),
                new Participant(new PlayerName("프리"))
        ))).hasMessage("중복된 이름은 사용할 수 없습니다.");
    }
}
