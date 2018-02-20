/*

Based on WithAutomaticLayout.

Original copyright & license:

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;


public class ForceAtlasVisualisation {

    private String input_file;
    private String output_directory;

    private Double gravity;
    private Double scale;
    private Double barnes_hut_theta;
    private Double jitter_tolerance;

    private Boolean lin_log_mode;
    private Double edge_weight_influence;
    private Boolean strong_gravity;

    private Integer threads;
    private Integer duration_seconds;
    private Float fast_proportion;


    public ForceAtlasVisualisation(String input_file, String output_directory, Double gravity, Double scale,
                                   Double barnes_hut_theta, Double jitter_tolerance, Boolean lin_log_mode,
                                   Double edge_weight_influence,
                                   Boolean strong_gravity, Integer threads,
                                   Integer duration_seconds, Float fast_proportion) {
        this.input_file = input_file;
        this.output_directory = output_directory;
        this.gravity = gravity;
        this.scale = scale;
        this.barnes_hut_theta = barnes_hut_theta;
        this.jitter_tolerance = jitter_tolerance;
        this.lin_log_mode = lin_log_mode;
        this.edge_weight_influence = edge_weight_influence;
        this.strong_gravity = strong_gravity;
        this.threads = threads;
        this.duration_seconds = duration_seconds;
        this.fast_proportion = fast_proportion;
    }

    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();


        //Import file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        File file = new File(this.input_file);
        try {
            container = importController.importFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        String basename = file.getName();


        //Append container to graph structure
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        DirectedGraph graph = graphModel.getDirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());


        AutoLayout autoLayout = new AutoLayout(this.duration_seconds, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);


        ForceAtlas2 fa2 = new ForceAtlas2Builder().buildLayout();
        fa2.setScalingRatio(this.scale);
        fa2.setGravity(this.gravity);
        fa2.setBarnesHutTheta(this.barnes_hut_theta);
        fa2.setJitterTolerance(this.jitter_tolerance);
        fa2.setLinLogMode(this.lin_log_mode);
        fa2.setEdgeWeightInfluence(this.edge_weight_influence);
        fa2.setStrongGravityMode(this.strong_gravity);
        fa2.setThreadsCount(this.threads);


        fa2.setBarnesHutOptimize(Boolean.TRUE);
        fa2.setAdjustSizes(Boolean.FALSE);
        
        fa2.initAlgo();


        System.out.println("Scaling: " + fa2.getScalingRatio());
        System.out.println("Gravity: " + fa2.getGravity());
        System.out.println("Theta: " + fa2.getBarnesHutTheta());
        System.out.println("Jitter tolerance: " + fa2.getJitterTolerance());
        System.out.println("Lin-log mode: "+ fa2.isLinLogMode());
        System.out.println("Edge weight influence: " + fa2.getEdgeWeightInfluence());
        System.out.println("Strong gravity: " + fa2.isStrongGravityMode());
        System.out.println("Threads: " + fa2.getThreadsCount());


        AutoLayout.DynamicProperty barnesHutProperty = AutoLayout.createDynamicProperty("forceAtlas2.barnesHutOptimization.name", Boolean.FALSE, this.fast_proportion);
        AutoLayout.DynamicProperty adjustSizesProperty = AutoLayout.createDynamicProperty("forceAtlas2.AdjustSizes.name", Boolean.TRUE, this.fast_proportion);

        autoLayout.addLayout(fa2, 1.0f, new AutoLayout.DynamicProperty[]{barnesHutProperty, adjustSizesProperty});

        autoLayout.execute();

        fa2.endAlgo();

        System.out.println("Layout finished");

        //Set 'show labels' option in Preview - and disable node size influence on text size
        PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.FALSE);

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);

        try {
            ec.exportFile(new File(this.output_directory, basename + ".pdf"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            ec.exportFile(new File(this.output_directory, basename + ".gexf"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
