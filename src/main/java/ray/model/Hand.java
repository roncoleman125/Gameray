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
public class Hand {
    public Player who;
    public List<String> cards = new ArrayList<>();
    public Directive directive; // optional directive: P, D, H

    @Override
    public String toString() {
        if (directive != null)
            return who + " " + cards + " " + directive;
        return who + " " + cards;
    }
}
