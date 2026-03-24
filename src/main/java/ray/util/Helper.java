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

package ray.util;

import ray.type.Suit;
import java.util.HashMap;
import java.util.Map;
import static ray.type.Suit.*;
import static ray.type.Suit.CLUBS;

/**
 * This class contains convenience methods.
 * @author Ron.Coleman
 */
public class Helper {
    static Map<Character,Suit> charToSuits = new HashMap<>(Map.of(
            'H', HEARTS,
            'S', SPADES,
            'D', DIAMONDS,
            'C', CLUBS
    ));

    /**
     * Gets a suit.
     * @param card Card[suit], eg, 2C for two of clubs
     * @return Suit if there is one.
     */
    public static Suit getSuit(String card) {
        assert !card.isEmpty() && card.length() <= 3: "invalid card "+card;

        int lastIdx = card.length()-1;

        char c = card.charAt(lastIdx);

        if(Character.isDigit(c) || c == 'K' || c == 'Q' || c == 'J' || c == 'A')
            return Suit.None;

        assert charToSuits.containsKey(c) : "invalid suit "+c+" in "+card;
        return charToSuits.get(c);
    }

    /**
     * Gets the card rank.
     * @param card Card
     * @return Rank
     */
    public static String getRank(String card) {
        if(card.startsWith("10"))
            return "10";

//        assert rank.length() == 1: "bad rank "+rank;

        switch(card.charAt(0)) {
            case 'A' -> { return "Card.ACE";}
            case 'K' -> { return "Card.KING";}
            case 'Q' -> { return "Card.QUEEN";}
            case 'J' -> { return "Card.JACK";}
        }

        assert Character.isDigit(card.charAt(0)): "invalid card rank in "+card;
        return card;
    }
}
