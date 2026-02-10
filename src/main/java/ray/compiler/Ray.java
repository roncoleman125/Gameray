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

package ray.compiler;

import ray.generator.ShoeBuilderTarget;
import ray.model.Game;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class compiles games into a single shoe and saves it.
 * @author Ron.Coleman
 */
public class Ray {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("usage: ray.compiler.Ray input-path [output-path]");
            System.exit(0);
        }

        String inputPath = args[0];
        String outputPath = args.length < 2 ? null : args[1];

        int lineno = 0;
        int rc = 0;

        List<Game> games = new ArrayList<>();

        Parser parser = new Parser();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputPath));

            String line = "";
            while((line = br.readLine()) != null) {
                lineno++;

                line = line.trim();

                if(line.isEmpty())
                    continue;

                if(line.startsWith("#"))
                    continue;

                Game game = parser.parse(line);

                List<String> errors = Validator.validate(game);
                if(!errors.isEmpty()) {
                    for(String error: errors)
                        reportError(lineno, error);
                    System.exit(1);
                }
                games.add(game);
            }

            PrintWriter target = (outputPath == null) ?
                    new PrintWriter(System.out) : new PrintWriter(new FileWriter(outputPath));
            ShoeBuilderTarget builder =
                    new ShoeBuilderTarget(target);

            builder.generate(games);

            builder.close();
        } catch(Exception e) {
            reportError(lineno, e.getMessage());
            rc = 1;
        }
        System.exit(rc);
    }

    static void reportError(int lineno, String msg) {
        System.err.println(lineno+": "+msg);
    }
}
