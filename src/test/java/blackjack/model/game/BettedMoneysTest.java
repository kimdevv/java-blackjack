package blackjack.model.game;

import blackjack.model.card.Card;
import blackjack.model.card.CardShape;
import blackjack.model.card.CardType;
import blackjack.model.player.Dealer;
import blackjack.model.player.Participant;
import blackjack.model.player.Participants;
import blackjack.model.player.PlayerName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BettedMoneysTest {

    @Test
    void 딜러가_블랙잭이_아닌_경우에_참가자_별_승무패_결과를_가지고_배당금을_계산한다() {
        // Given
        Dealer dealer = new Dealer();
        dealer.putCard(new Card(CardShape.SPADE, CardType.NORMAL_5));
        Participant participant = new Participant(new PlayerName("프리")); // 블랙잭
        participant.putCard(new Card(CardShape.SPADE, CardType.ACE));
        participant.putCard(new Card(CardShape.SPADE, CardType.KING));
        Participant participant2 = new Participant(new PlayerName("포비")); // 승
        participant2.putCard(new Card(CardShape.SPADE, CardType.KING));
        Participant participant3 = new Participant(new PlayerName("제이슨")); // 무
        participant3.putCard(new Card(CardShape.HEART, CardType.NORMAL_5));
        Participant participant4 = new Participant(new PlayerName("리사")); // 패
        participant4.putCard(new Card(CardShape.SPADE, CardType.NORMAL_2));
        BettedMoneys bettedMoneys = new BettedMoneys(Map.of(participant, new BettedMoney(10_000),
                participant2, new BettedMoney(10_000),
                participant3, new BettedMoney(10_000),
                participant4, new BettedMoney(10_000)));
        Participants participants = new Participants(List.of(participant, participant2, participant3, participant4));
        Map<Participant, ParticipantResult> participantResults = ParticipantResult.calculateParticipantResults(dealer, participants);

        // When
        Map<Participant, Integer> winningMoneys = bettedMoneys.calculateWinningMoneys(dealer.isBlackJack(), participantResults);

        // Then
        assertThat(winningMoneys).isEqualTo(Map.of(participant, 15_000, participant2, 10_000, participant3, 0, participant4, -10_000));
    }

    @Test
    void 딜러가_블랙잭인_경우에_참가자_별_승무패_결과를_가지고_배당금을_계산한다() {
        // Given
        Dealer dealer = new Dealer();
        dealer.putCard(new Card(CardShape.SPADE, CardType.ACE));
        dealer.putCard(new Card(CardShape.SPADE, CardType.KING));
        Participant participant = new Participant(new PlayerName("프리")); // 블랙잭(무)
        participant.putCard(new Card(CardShape.SPADE, CardType.ACE));
        participant.putCard(new Card(CardShape.SPADE, CardType.KING));
        Participant participant2 = new Participant(new PlayerName("제이슨")); // 무
        participant2.putCard(new Card(CardShape.HEART, CardType.NORMAL_5));
        Participant participant3 = new Participant(new PlayerName("리사")); // 패
        participant3.putCard(new Card(CardShape.SPADE, CardType.NORMAL_2));
        BettedMoneys bettedMoneys = new BettedMoneys(Map.of(participant, new BettedMoney(10_000),
                participant2, new BettedMoney(10_000),
                participant3, new BettedMoney(10_000)));
        Participants participants = new Participants(List.of(participant, participant2, participant3));
        Map<Participant, ParticipantResult> participantResults = ParticipantResult.calculateParticipantResults(dealer, participants);

        // When
        Map<Participant, Integer> winningMoneys = bettedMoneys.calculateWinningMoneys(dealer.isBlackJack(), participantResults);

        // Then
        assertThat(winningMoneys).isEqualTo(Map.of(participant, 0, participant2, -10_000, participant3, -10_000));
    }
}
