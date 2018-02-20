/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lukauskas.gephitoolkitfa2;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

    public static void main(String[] args) {

        ArgumentParser parser = ArgumentParsers.newFor("Main").build()
                .defaultHelp(true)
                .description("Create ForceAtlas2 visualisation for graph.");
        parser.addArgument("--gravity").type(Double.class).setDefault(1.0)
                 .help("Gravity parameter of ForceAtlas2");
        parser.addArgument("--scale").type(Double.class).setDefault(10.0)
                .help("Scale parameter of ForceAtlas2");

        parser.addArgument("--theta").type(Double.class).setDefault(1.2);
        parser.addArgument("--tolerance").type(Double.class).setDefault(1.0);
        parser.addArgument("--linlog").type(Boolean.class).setDefault(Boolean.FALSE);
        parser.addArgument("--weightinfluence").type(Double.class).setDefault(1.0);
        parser.addArgument("--stronggravity").type(Boolean.class).setDefault(Boolean.FALSE);
        parser.addArgument("--threads").type(Integer.class).setDefault(7);

        parser.addArgument("--duration").type(Integer.class).setDefault(60)
                .help("Duration in seconds to run the algorithm for");
        parser.addArgument("--proportion").type(Float.class).setDefault(0.8f)
                .help("Proportion of time to allocate for fast ForceAtlas2." +
                        "The rest is allocated for slow, no-overlap fa2");
        parser.addArgument("-O", "--outdir").type(Arguments.fileType().verifyIsDirectory().verifyCanWrite())
                .setDefault(".")
                .help("Output directory");

        parser.addArgument("input_file").type(Arguments.fileType().acceptSystemIn().verifyCanRead())
                .help("File to create visualisation for");


        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String input_file = ns.getString("input_file");
        System.out.println("Processing " + input_file);

        Double gravity = ns.getDouble("gravity");
        Double scale = ns.getDouble("scale");
        Double barnes_hut_theta = ns.getDouble("theta");
        Double jitter_tolerance = ns.getDouble("tolerance");
        Boolean lin_log_mode = ns.getBoolean("linlog");
        Double edge_weight_influence = ns.getDouble("weightinfluence");

        Integer duration_seconds = ns.getInt("duration");
        Float fast_proportion = ns.getFloat("proportion");

        String output_directory = ns.getString("outdir");

        Boolean strong_gravity = ns.getBoolean("stronggravity");
        Integer threads = ns.getInt("threads");

        ForceAtlasVisualisation autoLayout = new ForceAtlasVisualisation(input_file, output_directory,
                gravity, scale, barnes_hut_theta, jitter_tolerance, lin_log_mode, edge_weight_influence,
                strong_gravity, threads,
                duration_seconds, fast_proportion);
        autoLayout.script();

    }
}
