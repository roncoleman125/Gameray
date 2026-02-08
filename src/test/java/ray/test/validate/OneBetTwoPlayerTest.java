package ray.test.validate;/*
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
import ray.parser.Validator;

import java.util.List;

/**
 * This class...
 *
 * @author ronnc
 */
public class OneBetTwoPlayerTest extends TestCase {
    public void test() {
        String ray = "T4 {5}: You 3+3 | Huey 9+2+5 | Dealer 10+7 >> Win{5}, Win{15}";

        Parser parser = new Parser();

        Game game = parser.parse(ray);
        
        List<String> errors = Validator.validate(game);

        for(String error: errors)
            System.out.println("error: "+error);

        assert !errors.isEmpty();
    }
}
