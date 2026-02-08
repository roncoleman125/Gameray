package ray.test.build;/*
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
import ray.parser.Parser;

/**
 * This class...
 *
 * @author ronnc
 */
public class ThreePlayer1Test extends TestCase {
    public void test() {

        String ray = "T8 {5,15}: You 3+3 | Dewey 9+2+5 | Dealer 10+7 >> Win{5}, Win{15}";

        Parser parser = new Parser();

        Game game = parser.parse(ray);

        ShoeBuilder shoe = new ShoeBuilder();

        System.setProperty("ray.seed","0");

        shoe.generate(game);
    }
}
