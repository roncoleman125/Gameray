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

package ray.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * This class...
 *
 * @author ronnc
 */

public class Directive {
    public char type = ' '; // P, D, H
    public List<List<String>> splitHands = new ArrayList<>(); // used for P!
    public List<String> extraCards = new ArrayList<>();       // used for D! or H!

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append("!");
        if (type == 'P') {
            sb.append("{");
            for (int i = 0; i < splitHands.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(String.join("+", splitHands.get(i)));
            }
            sb.append("}");
        } else if (!extraCards.isEmpty()) {
            sb.append(String.join("+", extraCards));
        }
        return sb.toString();
    }
}
