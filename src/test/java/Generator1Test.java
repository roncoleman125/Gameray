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

import junit.framework.TestCase;
import ray.model.Game;
import ray.parser.Parser;
import ray.generator.ShoeBuilder;

/**
 * This class...
 *
 * @author ronnc
 */
public class Generator1Test extends TestCase {
    public void test() {

        String ray = "T1 {5}: You 7+7+P!{2+4,5+9} | Dealer 10+6 >> WIN{5}, PUSH{5}";

        Parser parser = new Parser();

        Game game = parser.parse(ray);

        ShoeBuilder shoe = new ShoeBuilder();

        System.setProperty("ray.seed","0");

        shoe.generate(game);
    }
}
