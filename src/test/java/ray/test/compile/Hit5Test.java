package ray.test.compile;/*
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

import junit.framework.TestCase;
import ray.generator.ShoeBuilder;
import ray.model.Game;
import ray.compiler.Parser;

/**
 * This class exercises two implied hits on one player and one on another.
 * @author Ron.Coleman
 */
public class Hit5Test extends TestCase {
    public void test() {
        String ray = "T9 {5}: You 3+2+J+2 | Dealer 7+10+4 >> Lose{5}";

        Parser parser = new Parser();

        Game game = parser.parse(ray);

        ShoeBuilder shoe = new ShoeBuilder();

        System.setProperty("ray.seed","0");

        shoe.generate(game);
    }
}
