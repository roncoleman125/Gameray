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

package ray.generator;

import java.io.PrintWriter;

/**
 * This class builds a shoe and writes the statements to a file.
 * @author Ron.Coleman
 */
public class ShoeBuilderTarget extends ShoeBuilder {
    PrintWriter writer;

    /**
     * Constructor
     * @param writer Writer to output the shoe
     */
    public ShoeBuilderTarget(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    void write(String stt) {
        try {
            writer.println(stt);
            writer.flush();
        }
        catch(Exception _) { }
    }

    /**
     * Flushes and closes the builder file.
     */
    public void close() {
        try {
            writer.flush();
            writer.close();
        }
        catch(Exception _) { }
    }
}
