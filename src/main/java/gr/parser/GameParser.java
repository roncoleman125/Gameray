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
import java.util.regex.*;

public class GameParser {

    // === Data Models ===
    static class Game {
        String label;
        List<Integer> bets = new ArrayList<>();      // supports one or two bets
        Hand playerHand;
        Hand dealerHand;
        List<Outcome> outcomes = new ArrayList<>();  // supports one or two outcomes

        @Override
        public String toString() {
            return String.format(
                    "Game[label=%s, bets=%s, player=%s, dealer=%s, outcomes=%s]",
                    label, bets, playerHand, dealerHand, outcomes);
        }
    }

    static class Hand {
        String who;
        List<String> cards = new ArrayList<>();
        Directive directive; // optional directive: P, D, H

        @Override
        public String toString() {
            if (directive != null)
                return who + cards + " " + directive;
            return who + cards;
        }
    }

    static class Directive {
        char type; // P, D, H
        List<List<String>> splitHands = new ArrayList<>(); // used for P!
        List<String> extraCards = new ArrayList<>();       // used for D! or H!

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(type).append("!");
            if (type == 'P') {
                sb.append("{");
                for (int i = 0; i < splitHands.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(String.join("+", splitHands.get(i)));
                }
                sb.append("}");
            } else if (!extraCards.isEmpty()) {
                sb.append(String.join("+", extraCards));
            }
            return sb.toString();
        }
    }

    static class Outcome {
        String result;
        int amount;

        @Override
        public String toString() {
            return result + "{" + amount + "}";
        }
    }

    // === Parsing Methods ===

    public static Game parse(String line) {
        line = line.trim();

        Game game = new Game();

        String[] parts = line.split(">>");
        if (parts.length != 2)
            throw new IllegalArgumentException("Missing '>>' outcome separator");

        String leftPart = parts[0].trim();
        String rightPart = parts[1].trim();

        // Left: T1 {5}: YOU 10+4 | DEALER 9+3+5
        // Right: LOSE{5} or WIN{5}, PUSH{10}

        // Step 1: parse label and bets
        int colonIdx = leftPart.indexOf(':');
        if (colonIdx < 0)
            throw new IllegalArgumentException("Missing ':' after bet section");

        String header = leftPart.substring(0, colonIdx).trim(); // T1 {5} or T1 {5,10}
        String body = leftPart.substring(colonIdx + 1).trim();  // YOU 10+4 | DEALER 9+3+5

        parseLabelAndBet(header, game);

        // Step 2: split player and dealer
        String[] hands = body.split("\\|");
        if (hands.length != 2)
            throw new IllegalArgumentException("Expected player and dealer hands separated by '|'");

        game.playerHand = parseHand(hands[0].trim());
        game.dealerHand = parseHand(hands[1].trim());

        // Step 3: parse one or two outcomes
        game.outcomes = parseOutcomes(rightPart);

        return game;
    }

    /**
     * Parses label and 1–2 bets, e.g.:
     *   T1 {5}
     *   T2 {5,10}
     */
    private static void parseLabelAndBet(String text, Game game) {
        Pattern p = Pattern.compile("(\\w+)\\s*\\{\\s*(\\d+)\\s*(?:,\\s*(\\d+)\\s*)?\\}");
        Matcher m = p.matcher(text);
        if (!m.find())
            throw new IllegalArgumentException("Invalid label/bet format: " + text);

        game.label = m.group(1);
        game.bets.add(Integer.parseInt(m.group(2)));
        if (m.group(3) != null)
            game.bets.add(Integer.parseInt(m.group(3)));
    }

    /**
     * Parses a hand like:
     *   YOU 7+7+P!{2+4,5+9}
     *   DEALER 10+2+D!10
     *   HUEY 9+2+H!5
     *   DEWEY 8+8+D!5
     */
    private static Hand parseHand(String text) {
        // Expanded to include HUEY and DEWEY as valid players
        Pattern p = Pattern.compile("(YOU|DEALER|HUEY|DEWEY)\\s+([A-Z0-9+!{}\\,]+)");
        Matcher m = p.matcher(text);
        if (!m.find())
            throw new IllegalArgumentException("Invalid hand format: " + text);

        Hand hand = new Hand();
        hand.who = m.group(1);
        String cardsPart = m.group(2).trim();

        // Check for directive (P!, D!, or H!)
        int exclIdx = cardsPart.indexOf('!');
        if (exclIdx > 0) {
            String basePart = cardsPart.substring(0, exclIdx - 1); // everything before the directive letter
            for (String c : basePart.split("\\+")) {
                if (!c.isEmpty() && Character.isLetterOrDigit(c.charAt(0)))
                    hand.cards.add(c);
            }

            // Pass starting at directive letter, e.g., "P!{2+4,5+9}"
            hand.directive = parseDirective(cardsPart.substring(exclIdx - 1));
        } else {
            for (String c : cardsPart.split("\\+"))
                if (!c.isEmpty()) hand.cards.add(c);
        }

        return hand;
    }

    /**
     * Parses directive parts such as:
     *   P!{2+4,5+9}
     *   D!10
     *   H!5
     */
    private static Directive parseDirective(String directivePart) {
        Directive dir = new Directive();
        directivePart = directivePart.trim();

        if (directivePart.length() < 2 || directivePart.charAt(1) != '!')
            throw new IllegalArgumentException("Invalid directive syntax: " + directivePart);

        char type = directivePart.charAt(0);
        dir.type = type;

        if (type == 'P') {
            // Parse P!{2+4,5+9}
            Pattern p = Pattern.compile("P!\\{([^}]+)\\}");
            Matcher m = p.matcher(directivePart);
            if (m.find()) {
                String inside = m.group(1);
                String[] hands = inside.split(",");
                for (String h : hands) {
                    List<String> cards = new ArrayList<>();
                    for (String c : h.trim().split("\\+"))
                        if (!c.isEmpty()) cards.add(c);
                    dir.splitHands.add(cards);
                }
            } else {
                throw new IllegalArgumentException("Invalid split directive: " + directivePart);
            }
        } else if (type == 'D' || type == 'H') {
            // Parse D!10 or H!5
            Pattern p = Pattern.compile("[DH]!([A-Z0-9+]+)");
            Matcher m = p.matcher(directivePart);
            if (m.find()) {
                for (String c : m.group(1).split("\\+"))
                    if (!c.isEmpty()) dir.extraCards.add(c);
            } else {
                throw new IllegalArgumentException("Invalid double/hit directive: " + directivePart);
            }
        } else {
            throw new IllegalArgumentException("Unknown directive type: " + type);
        }

        return dir;
    }

    /**
     * Parses one or two outcomes separated by commas.
     * Example: "WIN{5}" or "WIN{5}, PUSH{10}"
     */
    private static List<Outcome> parseOutcomes(String text) {
        List<Outcome> outcomes = new ArrayList<>();
        String[] parts = text.split(",");
        for (String part : parts) {
            part = part.trim();
            Outcome o = parseOutcome(part);
            outcomes.add(o);
        }
        return outcomes;
    }

    private static Outcome parseOutcome(String text) {
        Pattern p = Pattern.compile("(WIN|LOSE|PUSH|BUST|BJ|BLACKJACK)\\s*\\{(\\d+)\\}",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (!m.find())
            throw new IllegalArgumentException("Invalid outcome format: " + text);
        Outcome o = new Outcome();
        o.result = m.group(1).toUpperCase();
        o.amount = Integer.parseInt(m.group(2));
        return o;
    }

    // === Test Main ===
    public static void main(String[] args) {
        String[] samples = {
                "T1 {5}: YOU 7+7+P!{2+4,5+9} | DEALER 10+6 >> WIN{5}, PUSH{5}",
                "T2 {5,10}: HUEY 10+2+D!10 | DEALER 9+8 >> LOSE{5}, LOSE{10}",
                "T3 {5}: DEWEY 9+2+H!5 | DEALER 10+7 >> WIN{5}"
        };

        for (String line : samples) {
            Game g = parse(line);
            System.out.println(g);
        }
    }
}