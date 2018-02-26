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
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;

enum EdgeColors {
    SOURCE, TARGET, MIXED, ORIGINAL
}
public class Main {

    public static void main(String[] args) {

        ArgumentParser parser = ArgumentParsers.newFor("Main").build()
                .defaultHelp(true)
                .description("Create ForceAtlas2 visualisation for graph.");

        parser.addArgument("-O", "--outdir").type(Arguments.fileType().verifyIsDirectory().verifyCanWrite())
                .setDefault(".")
                .help("Output directory");

        parser.addArgument("input_file").type(Arguments.fileType().acceptSystemIn().verifyCanRead())
                .help("File to create visualisation for");

        ArgumentGroup layout_group = parser.addArgumentGroup("Layout options");


        layout_group.addArgument("--gravity").type(Double.class).setDefault(1.0)
                 .help("Gravity parameter of ForceAtlas2");
        layout_group.addArgument("--scale").type(Double.class).setDefault(10.0)
                .help("Scale parameter of ForceAtlas2");

        layout_group.addArgument("--theta").type(Double.class).setDefault(1.2);
        layout_group.addArgument("--tolerance").type(Double.class).setDefault(1.0);
        layout_group.addArgument("--linlog").type(Arguments.booleanType()).setDefault(Boolean.FALSE);
        layout_group.addArgument("--weightinfluence").type(Double.class).setDefault(1.0);
        layout_group.addArgument("--stronggravity").type(Arguments.booleanType()).setDefault(Boolean.FALSE);
        layout_group.addArgument("--threads").type(Integer.class).setDefault(7);

        ArgumentGroup autolayout_group = parser.addArgumentGroup("Autolayout options");

        autolayout_group.addArgument("--duration").type(Integer.class).setDefault(60)
                .help("Duration in seconds to run the algorithm for");
        autolayout_group.addArgument("--proportion").type(Float.class).setDefault(0.8f)
                .help("Proportion of time to allocate for fast ForceAtlas2." +
                        "The rest is allocated for slow, no-overlap fa2");
        autolayout_group.addArgument("--labeladjustratio").type(Float.class).setDefault(0.2f)
                .help("Proportion of time allocated to LabelAdjust algorithm");


        ArgumentGroup visualisation_group = parser.addArgumentGroup("Visualisation options");
        visualisation_group.addArgument("--straight").type(Arguments.booleanType()).setDefault(Boolean.FALSE)
                .help("Draw straight edges");

        visualisation_group.addArgument("--edgecolor").type(EdgeColors.class).setDefault(EdgeColors.SOURCE)
                .help("Edge color mode");


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

        Boolean curved_edges = !ns.getBoolean("straight");


        EdgeColors selected_color = (EdgeColors) ns.get("edgecolor");

        Float labeladjust_ratio = ns.getFloat("labeladjustratio");

        EdgeColor.Mode edge_color = null;
        if (selected_color == EdgeColors.MIXED) edge_color = EdgeColor.Mode.MIXED;
        else if (selected_color == EdgeColors.ORIGINAL) edge_color = EdgeColor.Mode.ORIGINAL;
        else if (selected_color == EdgeColors.SOURCE) edge_color = EdgeColor.Mode.SOURCE;
        else if (selected_color == EdgeColors.TARGET) edge_color = EdgeColor.Mode.TARGET;

        ForceAtlasVisualisation autoLayout = new ForceAtlasVisualisation(input_file, output_directory,
                gravity, scale, barnes_hut_theta, jitter_tolerance, lin_log_mode, edge_weight_influence,
                strong_gravity, threads,
                duration_seconds, fast_proportion, curved_edges, new EdgeColor(edge_color), labeladjust_ratio);
        autoLayout.script();

    }
}
