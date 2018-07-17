package com.aliya.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryPlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

class JavassistPlugin implements Plugin<Project> {

    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.error("========================");
        logger.error("Javassist开始修改Class!");
        logger.error("========================");

        // has plugin: 'com.android.application'
        if (project.getPlugins().hasPlugin(AppPlugin.class)) {
            logger.error("ApplicationPlugin");

//            project.getDependencies().add("implementation", "io.github.prototypez:save-state-core:0.1");
//            project.getDependencies().add("annotationProcessor", "io.github.prototypez:save-state-processor:0.1.4");

        }

        // has plugin: 'com.android.library'
        if (project.getPlugins().hasPlugin(LibraryPlugin.class)) {
            logger.error("LibraryPlugin");
        }

        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        android.registerTransform(new JavassistTransform(project));

    }
}