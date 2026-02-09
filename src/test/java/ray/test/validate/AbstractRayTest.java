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

package ray.test.validate;

import junit.framework.TestCase;
import ray.compiler.Parser;
import ray.compiler.Validator;
import ray.model.Game;

import java.util.List;

/**
 * This class is the base for all tests.
 *
 * @author ronnc
 */
abstract public class AbstractRayTest extends TestCase {
    protected void test(String ray) {
        Parser parser = new Parser();

        List<String> errors =  null;

        try {
            Game game = parser.parse(ray);

            errors = Validator.validate(game);

            for (String error : errors)
                System.out.println("error: " + error);

            assert errors.isEmpty();
        }
        catch(IllegalArgumentException e) {
            System.out.println("syntax error: "+e.getMessage());
        }
    }
}
