/*
 * Copyright (c) 2026 Hexant, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ray.shoe;

import ray.parser.GameParser;
import ray.parser.Hand;
import ray.parser.Player;

import java.util.*;

import static ray.shoe.Suit.*;

/**
 * This class...
 *
 * @author ronnc
 */
public class Generator {
    long seed = Long.parseLong(System.getProperty("ray.seed",System.currentTimeMillis()+""));
    Random ran = new Random(seed);
    final String INDENT = "    ";

    List<Player> players = new ArrayList<>(Arrays.asList(
            Player.Huey,
            Player.You,
            Player.Dewey,
            Player.Dealer));

    Map<Player,Integer> cardnos = new HashMap<>(Map.of(
            Player.You,0,
            Player.Huey, 0,
            Player.Dewey, 0,
            Player.Dealer, 0
    ));

    Suit[] suits = {HEARTS, SPADES, DIAMONDS, CLUBS };

    public void generate(GameParser.Game game) {
        start(game);
        play(game);
    }

    void play(GameParser.Game game) {
        for(Player player: players) {
            Hand hand = game.whodat(player);
            if(hand == null)
                continue;

            if(hand.directive != null) {
                switch(hand.directive.type) {
                    case 'P' -> {
                        for(List<String> splitHand: hand.directive.splitHands) {
                            for(String rank: splitHand) {
                                write(newCard(rank));
                            }
                        }
                    }
                    case 'H', 'D' -> {
                        for(String extra: hand.directive.extraCards) {
                            write(newCard(extra));
                        }
                    }

                };
            }
            else if(hand.cards.size() >= 2) {
                for(int cardno = cardnos.get(player); cardno < hand.cards.size(); cardno++) {
                    String rank = hand.cards.get(cardno);
                    write(newCard(rank));
                }
            }
        }
    }

    void start(GameParser.Game game) {
        // Round 1
        for(Player player: players) {
            deal(game, player);
        }

        // Round 2
        for(Player player: players) {
            deal(game, player);
        }
    }

    void deal(GameParser.Game game, Player player) {
        Hand hand = game.whodat(player);
        if(hand == null)
            return;

        int cardno = cardnos.get(player);

        if(cardno >= 2)
            return;

        String rank = hand.cards.get(cardno);

        write(newCard(rank));

        cardnos.put(player,cardno+1);
    }

    void write(String stt) {
        System.out.println(stt);
    }

    String newCard(String rank) {
        int suitno = ran.nextInt(suits.length);

        Suit suit = suits[suitno];

        String stt = indent(1)+"cards.add(new Card("+rank+", "+suit+"));";
        return stt;
    }

    String indent(int amount) {
        StringBuilder buffer = new StringBuilder();

        for(int spaceno=0; spaceno < amount; spaceno++) {
            buffer.append(INDENT);
        }

        return buffer.toString();
    }
}
