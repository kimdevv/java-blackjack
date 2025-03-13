package blackjack.controller;

import blackjack.model.game.*;
import blackjack.model.player.Dealer;
import blackjack.model.player.Participant;
import blackjack.model.player.Participants;
import blackjack.view.InputView;
import blackjack.view.OutputView;

import java.util.HashMap;
import java.util.Map;

public class BlackJackController {

    private final InputView inputView;
    private final OutputView outputView;

    public BlackJackController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        Participants participants = generateParticipants();
        BettedMoneys bettedMoneys = generateBettedMoneys(participants);
        Dealer dealer = new Dealer();
        BlackJackGame blackJackGame = new BlackJackGame(new DeckInitializer(), dealer, participants);
        blackJackGame.initializeGame();
        outputView.outputFirstCardDistributionResult(participants, dealer);
        progressTurns(blackJackGame, dealer, participants);
        calculateFinalWinningMoney(dealer, participants, bettedMoneys);
    }

    private Participants generateParticipants() {
        String namesText = inputView.inputParticipantName();
        return new Participants(Parser.parseNames(namesText).stream()
                .map(Participant::new)
                .toList());
    }

    private BettedMoneys generateBettedMoneys(final Participants participants) {
        Map<Participant, BettedMoney> bettedMoneys = new HashMap<>();
        for (Participant participant : participants.getParticipants()) {
            bettedMoneys.put(participant, new BettedMoney(inputView.inputParticipantMoney(participant.getName())));
        }
        return new BettedMoneys(bettedMoneys);
    }

    private void progressTurns(final BlackJackGame blackJackGame, final Dealer dealer, final Participants participants) {
        giveMoreCardToWantingParticipants(blackJackGame);
        giveMoreDealerCard(blackJackGame, dealer);
        outputView.outputFinalCardStatus(dealer, participants);
    }

    private void giveMoreCardToWantingParticipants(final BlackJackGame blackJackGame) {
        while (blackJackGame.canGiveCardToParticipant()) {
            Participant participant = blackJackGame.getCurrentTurnParticipant();
            boolean isPlayerWantCard = Parser.parseCommand(inputView.inputCallOrStay(participant.getName()));
            blackJackGame.giveCardToCurrentTurnParticipant(isPlayerWantCard);
            outputView.outputPlayerCardStatus(participant);
            checkBust(blackJackGame, participant);
        }
    }

    private void checkBust(final BlackJackGame blackJackGame, final Participant participant) {
        if (participant.isBust()) {
            outputView.outputParticipantBust(participant.getName());
            blackJackGame.skipTurn();
        }
    }

    private void giveMoreDealerCard(final BlackJackGame blackJackGame, final Dealer dealer) {
        while (blackJackGame.isDealerCardDrawable()) {
            blackJackGame.drawDealerCard();
            outputView.outputDealerGetCard();
            outputView.outputPlayerCardStatus(dealer);
        }
        outputView.outputDealerCardFinish();
    }

    /*private void calculateFinalResults(final Dealer dealer, final Participants participants) {
        Map<Participant, ParticipantResult> participantResults = ParticipantResult.calculateParticipantResults(dealer, participants);
        Map<ParticipantResult, Integer> participantResultCounts = ParticipantResult.countResults(participantResults);
        outputView.outputFinalResult(participantResults, participantResultCounts);
    }*/

    private void calculateFinalWinningMoney(final Dealer dealer, final Participants participants, final BettedMoneys bettedMoneys) {
        Map<Participant, ParticipantResult> participantResults = ParticipantResult.calculateParticipantResults(dealer, participants);
        Map<Participant, Integer> winningMoney = bettedMoneys.calculateWinningMoney(dealer.isBlackJack(), participantResults);
        int dealerMoney = -winningMoney.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        outputView.outputFinalWinningMoney(dealerMoney, winningMoney);
    }
}
