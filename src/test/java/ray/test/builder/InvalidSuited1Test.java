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

package ray.test.builder;

import junit.framework.TestCase;
import ray.compiler.Ray;

/**
 * This class exercises the compiler for an invalid suit.
 * @author Ron.Coleman
 */
public class InvalidSuited1Test extends TestCase {
    static final String INPUT_PATH = "src/test/ray/test4.ray";

    public void test() {
        Ray.main(new String[]{INPUT_PATH});

        assert Ray.rc != 0: "expected failure for "+INPUT_PATH;
    }
}
