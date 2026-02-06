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

package ray.model;

import ray.type.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class...
 *
 * @author ronnc
 */
// === Data Models ===
public class Game {
    public String label;
    public List<Integer> bets = new ArrayList<>();      // supports one or two bets
    //        Hand playerHand;
//        Hand dealerHand;
    public final List<Hand> hands = new ArrayList<>();
    public List<Outcome> outcomes = new ArrayList<>();  // supports one or two outcomes

    /**
     * Gets a player hand.
     * @param player Player
     * @return Hand
     */
    public Hand whodat(Player player) {
        for(Hand hand: hands) {
            if(hand.who.equals(player))
                return hand;
        }
        return null;
    }

    /**
     * Gets YOU hand.
     * @return Hand
     */
    public Hand you() {
        return whodat(Player.You);
    }

    /**
     * Gets DEALER hand
     * @return Hand
     */
    public Hand dealer() {
        return whodat(Player.Dealer);
    }

    @Override
    public String toString() {
        StringBuilder handsBuffer = new StringBuilder();
        for(Hand hand: hands) {
            handsBuffer.append(hand).append(", ");
        }

        return String.format(
                "Game[label=%s, bets=%s, hands=%soutcomes=%s]",
                label, bets, handsBuffer, outcomes);
    }
}
