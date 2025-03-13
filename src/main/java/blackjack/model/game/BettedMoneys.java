package blackjack.model.game;

import blackjack.model.player.Participant;

import java.util.HashMap;
import java.util.Map;

import static blackjack.model.game.ParticipantResult.LOSE;
import static blackjack.model.game.ParticipantResult.WIN;

public class BettedMoneys {

    public static final int DRAW_MONEY = 0;
    public static final double WINNING_MONEY_RATE = 1.5;

    private final Map<Participant, BettedMoney> bettedMoneys;

    public BettedMoneys(Map<Participant, BettedMoney> bettedMoneys) {
        this.bettedMoneys = bettedMoneys;
    }

    public Map<Participant, Integer> calculateWinningMoney(final boolean isDealerBlackJack, final Map<Participant, ParticipantResult> participantResults) {
        Map<Participant, Integer> winningMoneys = new HashMap<>();
        for (Map.Entry<Participant, ParticipantResult> resultEntry : participantResults.entrySet()) {
            Participant participant = resultEntry.getKey();
            if (isDealerBlackJack) {
                winningMoneys.put(participant, calculateWinningMoneyIfDealerBlackJack(participant));
                continue;
            }
            winningMoneys.put(participant, calculateWinningMoneyNotBlackJack(bettedMoneys.get(participant).getMoney(), participant.isBlackJack(), resultEntry.getValue()));
        }
        return winningMoneys;
    }

    private int calculateWinningMoneyIfDealerBlackJack(final Participant participant) {
        if (participant.isBlackJack()) {
            return DRAW_MONEY;
        }
        return 0 - bettedMoneys.get(participant).getMoney();
    }

    private int calculateWinningMoneyNotBlackJack(final int bettedMoney, final boolean isBlackJack, final ParticipantResult participantResult) {
        if (isBlackJack) {
            return (int)(bettedMoney * WINNING_MONEY_RATE);
        }
        if (participantResult == WIN) {
            return bettedMoney;
        }
        if (participantResult == LOSE) {
            return 0 - bettedMoney;
        }
        return DRAW_MONEY;
    }
}
