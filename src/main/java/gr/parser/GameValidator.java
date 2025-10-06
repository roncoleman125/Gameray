/*
 * Copyright (c) 2025 Hexant, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package gr.parser;

import java.util.*;

public class GameValidator {

    /**
     * Validates a parsed Game according to the rules:
     * 1. number of bets == number of outcomes, except where YOU splits → outcomes = bets + 1
     * 2. only YOU can split (P!)
     * 3. only YOU, HUEY, and DEWEY can hit (H!) or double-down (D!)
     * 4. each game must have at least two players: YOU and DEALER
     * 5. each player, including DEALER, may appear only once
     * Additionally:
     *   - a split (P!) must contain exactly two subhands
     *   - each split subhand must contain at least two cards
     *
     * @param game Parsed Game object from GameParser
     * @return list of validation error messages; empty if all rules pass
     */
    public static List<String> validate(GameParser.Game game) {
        List<String> errors = new ArrayList<>();

        if (game == null) {
            errors.add("Game is null.");
            return errors;
        }

        // --- Rule 1: Bets vs Outcomes ---
        boolean youSplit = hasYouSplit(game.playerHand);
        int bets = game.bets.size();
        int outcomes = game.outcomes.size();

        if (youSplit) {
            if (outcomes != bets + 1) {
                errors.add(String.format(
                        "Rule 1 violation: YOU split, expected outcomes=%d but found %d.",
                        bets + 1, outcomes));
            }
        } else {
            if (outcomes != bets) {
                errors.add(String.format(
                        "Rule 1 violation: Expected outcomes=%d but found %d.",
                        bets, outcomes));
            }
        }

        // --- Rule 2: Only YOU can split ---
        validateSplits(game, errors);

        // --- Rule 3: Only YOU, HUEY, DEWEY can hit or double ---
        validateDirectiveAccess(game.playerHand, errors);
        validateDirectiveAccess(game.dealerHand, errors);

        // --- Rule 4: Must have YOU and DEALER ---
        validatePlayersPresent(game, errors);

        // --- Rule 5: Each player only once ---
        validateUniquePlayers(game, errors);

        return errors;
    }

    /** Checks if YOU hand has a split. */
    private static boolean hasYouSplit(GameParser.Hand hand) {
        return hand != null && "YOU".equals(hand.who)
                && hand.directive != null && hand.directive.type == 'P';
    }

    /** Checks if a hand has a split directive. */
    private static boolean hasSplit(GameParser.Hand hand) {
        return hand != null && hand.directive != null && hand.directive.type == 'P';
    }

    /** Validates who can use H! or D!, and also calls split validation. */
    private static void validateDirectiveAccess(GameParser.Hand hand, List<String> errors) {
        if (hand == null || hand.directive == null)
            return;

        char type = hand.directive.type;
        String who = hand.who;

        // Only YOU, HUEY, and DEWEY can hit or double
        if ((type == 'H' || type == 'D')
                && !(who.equals("YOU") || who.equals("HUEY") || who.equals("DEWEY"))) {
            errors.add(String.format(
                    "Rule 3 violation: %s cannot use directive %c!.", who, type));
        }

        // Only YOU can split
        if (type == 'P' && !who.equals("YOU")) {
            errors.add(String.format(
                    "Rule 2 violation: %s cannot split (P!).", who));
        }
    }

    /** Validates split details: exactly two subhands, each with ≥2 cards. */
    private static void validateSplits(GameParser.Game game, List<String> errors) {
        GameParser.Hand[] hands = {game.playerHand, game.dealerHand};
        for (GameParser.Hand h : hands) {
            if (h == null || h.directive == null || h.directive.type != 'P')
                continue;

            GameParser.Directive dir = h.directive;

            // Rule 2 already checked who may split; now check the structure.
            if (dir.splitHands.size() != 2) {
                errors.add(String.format(
                        "Split error: %s has %d subhands, expected 2.",
                        h.who, dir.splitHands.size()));
            }

            for (int i = 0; i < dir.splitHands.size(); i++) {
                List<String> sub = dir.splitHands.get(i);
                if (sub.size() < 2) {
                    errors.add(String.format(
                            "Split error: %s subhand #%d has only %d card(s), expected at least 2.",
                            h.who, i + 1, sub.size()));
                }
            }
        }
    }

    /** Ensures that both YOU and DEALER are present. */
    private static void validatePlayersPresent(GameParser.Game game, List<String> errors) {
        Set<String> players = new HashSet<>();
        if (game.playerHand != null)
            players.add(game.playerHand.who);
        if (game.dealerHand != null)
            players.add(game.dealerHand.who);

        if (!players.contains("YOU"))
            errors.add("Rule 4 violation: Missing YOU player.");

        if (!players.contains("DEALER"))
            errors.add("Rule 4 violation: Missing DEALER player.");

        if (players.size() < 2)
            errors.add("Rule 4 violation: Each game must have at least YOU and DEALER.");
    }

    /** Ensures each player (including DEALER) is given only once. */
    private static void validateUniquePlayers(GameParser.Game game, List<String> errors) {
        List<String> participants = new ArrayList<>();
        if (game.playerHand != null)
            participants.add(game.playerHand.who);
        if (game.dealerHand != null)
            participants.add(game.dealerHand.who);

        Set<String> seen = new HashSet<>();
        for (String who : participants) {
            if (!seen.add(who))
                errors.add(String.format("Rule 5 violation: Player %s is duplicated.", who));
        }
    }

    // === Example Usage ===
    public static void main(String[] args) {
        // Valid example
        GameParser.Game g1 = new GameParser().parse("T1 {5,10}: YOU 7+7+P!{2+4,5+9} | DEALER 10+6 >> WIN{5}, PUSH{5}, LOSE{10}");
        System.out.println("Validating g1...");
        printValidation(g1);

        // Invalid: dealer splits
        GameParser.Game g2 = new GameParser().parse("T2 {5}: DEALER 10+10+P!{3+8,9+2} | YOU 9+8 >> WIN{5}");
        System.out.println("\nValidating g2...");
        printValidation(g2);

        // Invalid: dealer hits
        GameParser.Game g3 = new GameParser().parse("T3 {5}: DEALER 9+8+H!5 | YOU 10+6 >> WIN{5}");
        System.out.println("\nValidating g3...");
        printValidation(g3);

        // Invalid: split with wrong structure
        GameParser.Game g4 = new GameParser().parse("T4 {5}: YOU 8+8+P!{2+4+9} | DEALER 10+7 >> WIN{5}, PUSH{5}");
        System.out.println("\nValidating g4...");
        printValidation(g4);

        // Invalid: missing YOU
        GameParser.Game g5 = new GameParser().parse("T5 {5}: HUEY 10+2+D!10 | DEALER 9+8 >> LOSE{5}");
        System.out.println("\nValidating g5...");
        printValidation(g5);
    }

    private static void printValidation(GameParser.Game game) {
        List<String> errors = validate(game);
        if (errors.isEmpty())
            System.out.println("✓ Game is valid: " + game);
        else {
            System.out.println("✗ Validation errors for " + game.label + ":");
            for (String e : errors)
                System.out.println("  - " + e);
        }
    }
}
