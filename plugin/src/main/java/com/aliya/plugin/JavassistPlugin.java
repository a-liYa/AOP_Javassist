package com.aliya.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

class JavassistPlugin implements Plugin<Project> {

    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.error("========================");
        logger.error("Javassist开始修改Class!");
        logger.error("========================");
//        project.android.registerTransform(new JavassistTransform(project))
    }
}