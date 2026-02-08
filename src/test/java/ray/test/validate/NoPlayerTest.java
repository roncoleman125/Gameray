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

import java.util.ArrayList;
import java.util.List;

/**
 * This class tests only one player, You.
 * @author Ron.Coleman
 */
public class NoPlayerTest extends TestCase {
    public void test() {

        String ray = "T0 {5}: >> Lose{5}";

        List<String> errors = new ArrayList<>();

        Parser parser = new Parser();
        try {
            Game game = parser.parse(ray);

            errors = Validator.validate(game);
            assert errors.size() == 1;
        }
        catch(Exception _) {
            assert false: "syntax error";
        }

        for(String error: errors)
            System.out.println("error: "+error);
    }
}
