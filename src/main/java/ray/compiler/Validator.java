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

package ray.compiler;

import ray.model.Directive;
import ray.model.Game;
import ray.model.Hand;
import ray.type.Player;
import java.util.*;

import static ray.type.Player.*;

public class Validator {

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
    public static List<String> validate(Game game) {
        List<String> errors = new ArrayList<>();

        if (game == null) {
            errors.add("game is null.");
            return errors;
        }

        // --- Rule 1: Bets vs Outcomes ---
        boolean youSplit = hasYouSplit(game.you());
        int bets = game.bets.size();
        int outcomes = game.outcomes.size();

        if (youSplit) {
            if (outcomes != bets + 1) {
                errors.add(String.format(
                        "You split, expected outcomes=%d but found %d.",bets + 1, outcomes));
            }
        } else {
            if (outcomes != bets) {
                errors.add(String.format(
                        "expected outcomes=%d but found %d.",bets, outcomes));
            }
        }

        // --- Rule 2: Only YOU can split ---
        validateSplits(game, errors);

        // --- Rule 3: Only YOU, HUEY, DEWEY can hit or double ---
        for(Hand hand: game.hands) {
            validateDirectiveAccess(hand, errors);
        }

        // --- Rule 4: Must have YOU and DEALER ---
        validatePlayersPresent(game, errors);

        // --- Rule 5: Each player only once ---
        validateUniquePlayers(game, errors);

        return errors;
    }

    /** Checks if YOU hand has a split. */
    private static boolean hasYouSplit(Hand hand) {
        return hand != null && "YOU".equals(hand.who)
                && hand.directive != null && hand.directive.type == 'P';
    }

    /** Checks if a hand has a split directive. */
    private static boolean hasSplit(Hand hand) {
        return hand != null && hand.directive != null && hand.directive.type == 'P';
    }

    /** Validates who can use H! or D!, and also calls split validation. */
    private static void validateDirectiveAccess(Hand hand, List<String> errors) {
        if (hand == null || hand.directive == null)
            return;

        char type = hand.directive.type;
        Player who = hand.who;

        // Only YOU, HUEY, and DEWEY can hit or double
        if ((type == 'H' || type == 'D')
                && !(who.equals("You") || who.equals("Huey") || who.equals("Dewey"))) {
            errors.add(String.format(
                    "%s cannot use directive %c!.", who, type));
        }

        // Only YOU can split
        if (type == 'P' && !who.equals("You")) {
            errors.add(String.format(
                    "%s cannot split (P!).", who));
        }
    }

    /** Validates split details: exactly two subhands, each with ≥2 cards. */
    private static void validateSplits(Game game, List<String> errors) {
        Hand[] hands = {game.you(), game.dealer()};
        for (Hand h : hands) {
            if (h == null || h.directive == null || h.directive.type != 'P')
                continue;

            Directive dir = h.directive;

            // Rule 2 already checked who may split; now check the structure.
            if (dir.splitHands.size() != 2) {
                errors.add(String.format(
                        "split error: %s has %d subhands, expected 2.",
                        h.who, dir.splitHands.size()));
            }

            for (int i = 0; i < dir.splitHands.size(); i++) {
                List<String> sub = dir.splitHands.get(i);
                if (sub.size() < 2) {
                    errors.add(String.format(
                            "split error: %s subhand #%d has only %d card(s), expected at least 2.",
                            h.who, i + 1, sub.size()));
                }
            }
        }
    }

    /** Ensures that both YOU and DEALER are present. */
    private static void validatePlayersPresent(Game game, List<String> errors) {
        Player[] seats = { Huey, You, Dewey, Dealer };

        Set<Player> players = new HashSet<>();
        for(Player player: seats) {
            if(game.whodat(player) != null)
                players.add(player);
        }

        if (!players.contains(You))
            errors.add("missing You player.");

        if (!players.contains(Player.Dealer))
            errors.add("missing Dealer player.");
    }

    /** Ensures each player (including DEALER) is given only once. */
    private static void validateUniquePlayers(Game game, List<String> errors) {
        List<Player> participants = new ArrayList<>();
        if (game.you() != null)
            participants.add(game.you().who);
        if (game.dealer() != null)
            participants.add(game.dealer().who);

        Set<Player> seen = new HashSet<>();
        for (Player who : participants) {
            if (!seen.add(who))
                errors.add(String.format("player %s is duplicated.", who));
        }
    }

    // === Example Usage ===
    public static void main(String[] args) {
        // Valid example
        Game g1 = new Parser().parse("T1 {5,10}: You 7+7+P!{2+4,5+9} | Dealer 10+6 >> WIN{5}, PUSH{5}, LOSE{10}");
        System.out.println("Validating g1...");
        printValidation(g1);

        // Invalid: dealer splits
        Game g2 = new Parser().parse("T2 {5}: Dealer 10+10+P!{3+8,9+2} | You 9+8 >> Win{5}");
        System.out.println("\nValidating g2...");
        printValidation(g2);

        // Invalid: dealer hits
        Game g3 = new Parser().parse("T3 {5}: Dealer 9+8+H!5 | You 10+6 >> Win{5}");
        System.out.println("\nValidating g3...");
        printValidation(g3);

        // Invalid: split with wrong structure
        Game g4 = new Parser().parse("T4 {5}: You 8+8+P!{2+4+9} | Dealer 10+7 >> Win{5}, Push{5}");
        System.out.println("\nValidating g4...");
        printValidation(g4);

        // Invalid: missing YOU
        Game g5 = new Parser().parse("T5 {5}: Huey 10+2+D!10 | Dealer 9+8 >> Lose{5}");
        System.out.println("\nValidating g5...");
        printValidation(g5);
    }

    private static void printValidation(Game game) {
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
